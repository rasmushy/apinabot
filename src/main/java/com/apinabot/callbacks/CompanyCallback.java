package com.apinabot.callbacks;

import com.apinabot.api.ApinaApiService;
import com.apinabot.api.dto.Address;
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
public class CompanyCallback implements ApinaCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyCallback.class);
    private final ApinaApiService apinaApiService;

    public CompanyCallback(ApinaApiService apinaApiService) {
        this.apinaApiService = apinaApiService;
    }
    @Override
    public void handleCallback(String callbackData, long chatId, int messageId, ApinaBot bot) {
        LOGGER.debug("Received company request: {}", callbackData);
        CompletableFuture.supplyAsync(() -> apinaApiService.getGymsByCompany(callbackData)).thenAccept(result -> {
            try {
                if (!result.isSuccess()) {
                    LOGGER.error("Failed to fetch gyms. {}", result.getError().getMessage());
                    bot.execute(MessageUtil.updateExistingMenu(chatId, "Failed to fetch gyms for the selected company. Please try again later.", null, messageId));
                    return;
                }
                List<GymInfo> gyms = result.getData();
                if (!gyms.isEmpty()) {
                    List<String> list = new ArrayList<>();
                    for (GymInfo gym : gyms) {
                        Address gymAddress = gym.getAddress();
                        String s = gymAddress.getCity() + " - " + gymAddress.getStreet();
                        list.add(s);
                    }
                    InlineKeyboardMarkup gymKeyboard = KeyboardUtil.createGymKeyboard(list);
                    bot.execute(MessageUtil.updateExistingMenu(chatId, "Select a climbing gym from " + callbackData + ":", gymKeyboard, messageId));

                } else {
                    bot.execute(MessageUtil.updateExistingMenu(chatId, "No gyms found for this company: " + callbackData + ".", null, messageId));
                }
            } catch (TelegramApiException e) {
                LOGGER.error("Failed to send gym infos", e);
            }
        }).exceptionally(e -> {
            try {
                bot.execute(MessageUtil.updateExistingMenu(chatId, "Failed to fetch gyms for the selected company. Please try again later.", null, messageId));
            } catch (TelegramApiException ex) {
                LOGGER.error("Failed to send error message", ex);
            }
            return null;
        });
    }

    @Override
    public String toString() {
        return "company";
    }
}
