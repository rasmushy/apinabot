package com.apinabot.callbacks;

import com.apinabot.api.ApinaApiService;
import com.apinabot.api.dto.GymInfo;
import com.apinabot.bots.ApinaBot;
import com.apinabot.utils.KeyboardUtil;
import com.apinabot.utils.MessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
public class CityCallback implements ApinaCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(CityCallback.class);
    private final ApinaApiService apinaApiService;

    public CityCallback(ApinaApiService apinaApiService) {
        this.apinaApiService = apinaApiService;
    }
    @Override
    public void handleCallback(String callbackData, long chatId, int messageId, ApinaBot bot) {
        LOGGER.debug("Received city request: {}", callbackData);
        CompletableFuture.supplyAsync(() -> apinaApiService.getGymsByCity(callbackData)).thenAccept(result -> {
            if (!result.isSuccess()) {
                LOGGER.error("Failed to fetch gyms. {}", result.getError().getMessage());
                return;
            }
            try {
                List<GymInfo> gyms = result.getData();
                if (!gyms.isEmpty()) {
                    List<String> list = new ArrayList<>();
                    for (GymInfo gym : gyms) {
                        String s = gym.getCompany().getName() + " - " + gym.getAddress().getStreet();
                        list.add(s);
                    }
                    InlineKeyboardMarkup gymKeyboard = KeyboardUtil.createGymKeyboard(list);
                    bot.execute(MessageUtil.updateExistingMenu(chatId, "Select a climbing gym from: " + callbackData, gymKeyboard, messageId));
                } else {
                    bot.execute(MessageUtil.updateExistingMenu(chatId, "No gyms found in this city: " + callbackData + ".", null, messageId));
                }
            } catch (TelegramApiException e) {
                LOGGER.error("Failed to send gym infos", e);
            }
        }).exceptionally(e -> {
            try {
                bot.execute(MessageUtil.sendText(chatId, "Failed to fetch gyms for the selected city. Please try again later."));
            } catch (TelegramApiException ex) {
                LOGGER.error("Failed to send error message", ex);
            }
            return null;
        });
    }

    @Override
    public String toString() {
        return "city";
    }
}
