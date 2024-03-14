package com.apinabot.callbacks;

import com.apinabot.api.ApinaApiService;
import com.apinabot.api.dto.GymInfo;
import com.apinabot.bots.ApinaBot;
import com.apinabot.handlers.StateHandler;
import com.apinabot.utils.KeyboardUtil;
import com.apinabot.utils.MessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.apinabot.utils.ApinaUtil.getUniqueCompanies;
public class PriceCallback implements ApinaCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(PriceCallback.class);
    private static final String TYPE = "price";
    private final ApinaApiService apinaApiService;
    private StateHandler stateHandler = null;

    public PriceCallback(ApinaApiService apinaApiService) {
        this.apinaApiService = apinaApiService;
    }

    @Override
    public void handleCallback(String callbackData, long chatId, int messageId, ApinaBot bot) {
        LOGGER.debug("Handling price callback with data: {}", callbackData);
        if (stateHandler == null) {
            stateHandler = bot.getStateHandler();
        }
        // if selected company is at state handler, remove it
        if (stateHandler.getSelections(chatId, TYPE).contains(callbackData)) {
            stateHandler.removeSelection(chatId, callbackData, TYPE);
        }
        // If the user has not selected any companies, clear the menu
        if (stateHandler.isEmpty(chatId, TYPE)) {
            try {
                bot.execute(MessageUtil.deleteMessage(chatId, messageId));
            } catch (TelegramApiException e) {
                LOGGER.error("Failed to clear price menu", e);
            }
        } else { // If the user has selected companies, update the menu
            InlineKeyboardMarkup companyKeyboard = KeyboardUtil.createCompanySelectionKeyboard(new ArrayList<>(stateHandler.getSelections(chatId, TYPE)), TYPE);
            try {
                bot.execute(MessageUtil.updateExistingMenu(chatId, "Select a company to see prices:", companyKeyboard, messageId));
            } catch (TelegramApiException e) {
                LOGGER.error("Failed to update price menu", e);
            }
        }
        // Fetch gyms by company and send prices
        CompletableFuture.supplyAsync(() -> apinaApiService.getGymsByCompany(callbackData)).thenAccept(result -> {
            try {
                if (!result.isSuccess()) {
                    LOGGER.error("Failed to fetch gyms. {}", result.getError().getMessage());
                    bot.execute(MessageUtil.updateExistingMenu(chatId, "Failed to fetch gyms for the selected company. Please try again later.", null, messageId));
                    return;
                }
                List<GymInfo> uniqueCompanies = getUniqueCompanies(result.getData());
                LOGGER.debug("Sending uniqueCompanies: {}", uniqueCompanies);
                if (uniqueCompanies.isEmpty()) {
                    bot.execute(MessageUtil.sendText(chatId, "No prices found for the selected company."));
                    return;
                }
                bot.execute(MessageUtil.sendPrices(chatId, uniqueCompanies));
            } catch (TelegramApiException e) {
                LOGGER.error("Failed to send price infos", e);
            }

        }).exceptionally(e -> {
            try {
                bot.execute(MessageUtil.sendText(chatId, "Failed to fetch gym price information. Please try again later."));
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
