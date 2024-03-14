package com.apinabot.commands;

import com.apinabot.bots.ApinaBot;
import com.apinabot.utils.KeyboardUtil;
import com.apinabot.utils.MessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static com.apinabot.utils.PollUtil.createDayPoll;
public class PollCommand implements Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(PollCommand.class);

    public PollCommand() {
        // Empty constructor
    }
    @Override
    public void execute(Long chatId, ApinaBot bot) {
        try {
            InlineKeyboardMarkup pollKeyboard = KeyboardUtil.createPollKeyboard();
            bot.execute(MessageUtil.sendMenu(chatId, "Select poll type:", pollKeyboard));
        } catch (TelegramApiException e) {
            LOGGER.error("Failed to send poll keyboard", e);
        }
    }
    @Override
    public void execute(Long chatId, String[] args, ApinaBot bot) {
        LOGGER.debug("Received args for poll creation: {}", (Object) args);
        try {
            bot.execute(createDayPoll(chatId, args));
        } catch (TelegramApiException e) {
            LOGGER.error("Failed to send day poll", e);
        }
    }
}
