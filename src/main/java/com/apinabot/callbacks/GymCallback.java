package com.apinabot.callbacks;

import com.apinabot.api.ApinaApiService;
import com.apinabot.api.dto.GymInfo;
import com.apinabot.bots.ApinaBot;
import com.apinabot.utils.MessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.concurrent.CompletableFuture;
public class GymCallback implements ApinaCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(GymCallback.class);
    private final ApinaApiService apinaApiService;

    public GymCallback(ApinaApiService apinaApiService) {
        this.apinaApiService = apinaApiService;
    }
    @Override
    public void handleCallback(String callbackData, long chatId, int messageId, ApinaBot bot) {
        LOGGER.debug("Received gym street address request: {}", callbackData);
        CompletableFuture.supplyAsync(() -> apinaApiService.getGymByAddress(callbackData)).thenAccept(result -> {
            try {
                if (!result.isSuccess()) {
                    LOGGER.error("Failed to fetch gyms. {}", result.getError().getMessage());
                    bot.execute(MessageUtil.updateExistingMenu(chatId, "Failed to fetch gym details. Please try again later.", null, messageId));
                    return;
                }
                GymInfo gym = result.getData();
                if (gym != null) {
                    bot.execute(MessageUtil.gymInfoResponse(chatId, gym, messageId));
                } else {
                    bot.execute(MessageUtil.updateExistingMenu(chatId, "Gym details not found.", null, messageId));
                }
            } catch (TelegramApiException e) {
                LOGGER.error("Failed to send gym details", e);
            }
        }).exceptionally(e -> {
            try {
                bot.execute(MessageUtil.updateExistingMenu(chatId, "Failed to fetch gym details. Please try again later.", null, messageId));
            } catch (TelegramApiException ex) {
                LOGGER.error("Failed to send error message", ex);
            }
            return null;
        });
    }

    @Override
    public String toString() {
        return "gym";
    }
}
