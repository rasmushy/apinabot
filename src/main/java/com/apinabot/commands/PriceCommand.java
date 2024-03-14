package com.apinabot.commands;

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

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.apinabot.utils.ApinaUtil.getUniqueCompanies;

/**
 * Command to display the price of a gym.
 *
 * @author rasmushy
 * @see Command
 */
public class PriceCommand implements Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(PriceCommand.class);
    private final ApinaApiService apinaApiService;
    private StateHandler stateHandler = null;
    public PriceCommand(ApinaApiService apinaApiService) {
        this.apinaApiService = apinaApiService;
    }
    @Override
    public void execute(Long chatId, ApinaBot bot) {
        if (stateHandler == null) {
            stateHandler = bot.getStateHandler();
        } else {
            // Clear the selections for the price menu if they exist
            stateHandler.clearState(chatId, "price");
        }
        // Fetch all gyms and send the companies to the user and add them to the state handler
        CompletableFuture.supplyAsync(apinaApiService::getAllGyms).thenAccept(result -> {
            try {
                if (!result.isSuccess()) {
                    LOGGER.error("Failed to fetch gyms. {}", result.getError().getMessage());
                    bot.execute(MessageUtil.sendText(chatId, "Failed to fetch gym information for price display. Please try again later."));
                    return;
                }
                List<GymInfo> gyms = result.getData();
                List<String> companyNames = gyms.stream().map(gym -> gym.getCompany().getName()).distinct().toList();
                stateHandler.addSelections(chatId, companyNames, "price");
                InlineKeyboardMarkup companyKeyboard = KeyboardUtil.createCompanySelectionKeyboard(companyNames, "price");
                bot.execute(MessageUtil.sendMenu(chatId, "Select a company to see prices:", companyKeyboard));
            } catch (TelegramApiException e) {
                LOGGER.error("Failed to send gym infos", e);
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
    public void execute(Long chatId, String[] args, ApinaBot bot) {
        LOGGER.debug("Received args: {}", (Object) args);
        String company = String.join(" ", args);
        LOGGER.debug("Received company: {}", company);
        CompletableFuture.supplyAsync(() -> apinaApiService.getGymsByCompany(company)).thenAccept(result -> {
            if (!result.isSuccess()) {
                LOGGER.error("Failed to fetch gyms. {}", result.getError().getMessage());
                return;
            }
            try {
                List<GymInfo> uniqueCompanies = getUniqueCompanies(result.getData());
                if (!uniqueCompanies.isEmpty()) {
                    bot.execute(MessageUtil.sendPrices(chatId, uniqueCompanies));
                } else {
                    bot.execute(MessageUtil.sendText(chatId, "No gyms found."));
                }
            } catch (TelegramApiException e) {
                LOGGER.error("Failed to send gym infos", e);
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

}
