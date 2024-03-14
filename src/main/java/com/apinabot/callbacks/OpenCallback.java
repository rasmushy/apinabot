package com.apinabot.callbacks;

import com.apinabot.api.ApinaApiService;
import com.apinabot.api.dto.GymInfo;
import com.apinabot.bots.ApinaBot;
import com.apinabot.handlers.StateHandler;
import com.apinabot.utils.ApinaUtil;
import com.apinabot.utils.KeyboardUtil;
import com.apinabot.utils.MessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
public class OpenCallback implements ApinaCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenCallback.class);
    private static final String TYPE = "open";
    private final ApinaApiService apinaApiService;
    private StateHandler stateHandler = null;
    public OpenCallback(ApinaApiService apinaApiService) {
        this.apinaApiService = apinaApiService;
    }
    @Override
    public void handleCallback(String callbackData, long chatId, int messageId, ApinaBot bot) {
        LOGGER.debug("Received open request: {}", callbackData);
        if (stateHandler == null) {
            stateHandler = bot.getStateHandler();
        }
        if (stateHandler.getSelections(chatId, TYPE).contains(callbackData)) {
            stateHandler.removeSelection(chatId, callbackData, TYPE);
        }
        if (stateHandler.isEmpty(chatId, TYPE)) {
            try {
                bot.execute(MessageUtil.deleteMessage(chatId, messageId));
            } catch (TelegramApiException e) {
                LOGGER.error("Failed to clear openNow menu", e);
            }
        } else {
            InlineKeyboardMarkup companyKeyboard = KeyboardUtil.createCompanySelectionKeyboard(new ArrayList<>(stateHandler.getSelections(chatId, TYPE)), TYPE);
            try {
                bot.execute(MessageUtil.updateExistingMenu(chatId, "Select a company to see open gyms:", companyKeyboard, messageId));
            } catch (TelegramApiException e) {
                LOGGER.error("Failed to update openNow menu", e);
            }
        }
        // Fetch gyms by company and send open gyms
        CompletableFuture.supplyAsync(() -> apinaApiService.getGymsByCompany(callbackData)).thenAccept(result -> {
            try {
                if (!result.isSuccess()) {
                    LOGGER.error("Failed to fetch gyms. {}", result.getError().getMessage());
                    bot.execute(MessageUtil.updateExistingMenu(chatId, "Failed to fetch gyms for the selected company. Please try again later.", null, messageId));
                    return;
                }
                List<GymInfo> gyms = result.getData();
                gyms.removeIf(ApinaUtil::isCurrentlyClosed);
                bot.execute(MessageUtil.sendOpenNow(chatId, gyms));
            } catch (TelegramApiException e) {
                LOGGER.error("Failed to send info of currently open gyms", e);
            }
        }).exceptionally(e -> {
            try {
                bot.execute(MessageUtil.sendText(chatId, "Failed to fetch currently open gym information. Please try again later."));
            } catch (TelegramApiException ex) {
                LOGGER.error("Failed to send error message", ex);
            }
            return null;
        });
    }

    @Override
    public String toString() {
        return TYPE;
    }
}
