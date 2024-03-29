package com.apinabot.callbacks;

import com.apinabot.bots.ApinaBot;
import com.apinabot.handlers.StateHandler;
import com.apinabot.utils.KeyboardUtil;
import com.apinabot.utils.MessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Set;

import static com.apinabot.utils.MessageUtil.deleteMessage;
import static com.apinabot.utils.PollUtil.createGymPoll;
public class PollCallback implements ApinaCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(PollCallback.class);
    private static final String TYPE = "poll";
    private StateHandler stateHandler = null;
    public PollCallback() {
        // Empty constructor
    }
    @Override
    public void handleCallback(String callbackData, long chatId, int messageId, ApinaBot bot) {
        LOGGER.debug("Handling poll callback with data: {}", callbackData);
        if (stateHandler == null) {
            stateHandler = bot.getStateHandler();
        }
        // Handle menu selections and update state
        if (!handlePollState(callbackData, stateHandler, chatId)) {
            // Confirm button pressed generate gym poll (Venue poll)
            LOGGER.debug("Creating gym poll");
            Set<String> selectedGyms = stateHandler.getSelections(chatId, TYPE);
            try {
                if (!selectedGyms.isEmpty()) {
                    bot.execute(createGymPoll(chatId, stateHandler.getSelections(chatId, TYPE)));
                    //Clear poll creation message and state
                    bot.execute(deleteMessage(chatId, messageId));
                    stateHandler.clearState(chatId, TYPE);
                } else {
                    bot.execute(MessageUtil.sendText(chatId, "No gyms selected for poll creation."));
                }
            } catch (TelegramApiException e) {
                LOGGER.error("Failed to send poll", e);
            }
            return;
        }
        // Update poll creation message
        try {
            LOGGER.debug("Updating poll creation message");
            InlineKeyboardMarkup updatedKeyboard = KeyboardUtil.createGymPollKeyboard(stateHandler.getAllGymsDisplayName(), stateHandler.getSelections(chatId, TYPE));
            bot.execute(MessageUtil.updateGymPollSelections(chatId, messageId, updatedKeyboard));
        } catch (
                TelegramApiException e) {
            LOGGER.error("Failed to update poll creation message", e);

        }
    }
    private boolean handlePollState(String callbackData, StateHandler stateHandler, long chatId) {
        if (!callbackData.equals("confirm")) {
            if (stateHandler.getSelections(chatId, TYPE).contains(callbackData)) {
                LOGGER.debug("Removing selection: {}", callbackData);
                stateHandler.removeSelection(chatId, callbackData, TYPE);
            } else {
                LOGGER.debug("Adding selection: {}", callbackData);
                stateHandler.addSelection(chatId, callbackData, TYPE);
            }
        }
        return !callbackData.equals("confirm");
    }

    @Override
    public String toString() {
        return TYPE;
    }
}
