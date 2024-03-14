package com.apinabot.callbacks;

import com.apinabot.api.dto.GymInfo;
import com.apinabot.bots.ApinaBot;
import com.apinabot.utils.MessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
public class GymCallback implements ApinaCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(GymCallback.class);

    public GymCallback() {
        // Empty constructor
    }
    @Override
    public void handleCallback(String callbackData, long chatId, int messageId, ApinaBot bot) {
        LOGGER.debug("Handling gym callback with data: {}", callbackData);
        try {
            GymInfo gym = bot.getStateHandler().getGymInfo(callbackData);
            if (gym != null) {
                bot.execute(MessageUtil.gymInfoResponse(chatId, gym, messageId));
            } else {
                bot.execute(MessageUtil.updateExistingMenu(chatId, "Gym details not found.", null, messageId));
            }
        } catch (TelegramApiException e) {
            LOGGER.error("Failed to send gym details", e);
        }
    }

    @Override
    public String toString() {
        return "gym";
    }
}
