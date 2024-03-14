package com.apinabot.commands;

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

import java.util.List;
import java.util.concurrent.CompletableFuture;
/**
 * Command to display the currently open gyms.
 *
 * @author rasmushy
 * @see Command
 */
public class OpenCommand implements Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenCommand.class);
    private final ApinaApiService apinaApiService;
    private StateHandler stateHandler = null;

    public OpenCommand(ApinaApiService apinaApiService) {
        this.apinaApiService = apinaApiService;
    }
    @Override
    public void execute(Long chatId, ApinaBot bot) {
        if (stateHandler == null) {
            stateHandler = bot.getStateHandler();
        } else {
            // Clear the selections for the open menu if they exist
            stateHandler.clearState(chatId, "open");
        }
        // Fetch all gyms and send the companies to the user and add them to the state handler
        CompletableFuture.supplyAsync(apinaApiService::getAllGyms).thenAccept(result -> {
            if (!result.isSuccess()) {
                LOGGER.error("Failed to fetch gyms. {}", result.getError().getMessage());
                return;
            }
            try {
                List<GymInfo> gyms = result.getData();
                gyms.removeIf(ApinaUtil::isCurrentlyClosed);
                List<String> companyNames = gyms.stream().map(gym -> gym.getCompany().getName()).distinct().toList();
                stateHandler.addSelections(chatId, companyNames, "open");
                InlineKeyboardMarkup gymKeyboard = KeyboardUtil.createCompanySelectionKeyboard(companyNames, "open");
                bot.execute(MessageUtil.sendMenu(chatId, "Select a company to see currently open gyms:", gymKeyboard));
            } catch (TelegramApiException e) {
                LOGGER.error("Failed to open menu for currently open gyms", e);
            }
        }).exceptionally(e -> {
            try {
                bot.execute(MessageUtil.sendText(chatId, "Failed to fetch open gym information. Please try again later."));
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
            try {
                if (!result.isSuccess()) {
                    LOGGER.error("Failed to fetch gyms. {}", result.getError().getMessage());
                    bot.execute(MessageUtil.sendText(chatId, "Failed to fetch gyms for the selected company. Please try again later."));
                    return;
                }
                List<GymInfo> gyms = result.getData();
                gyms.removeIf(ApinaUtil::isCurrentlyClosed);
                bot.execute(MessageUtil.sendOpenNow(chatId, gyms));
            } catch (TelegramApiException e) {
                LOGGER.error("Failed to send gym infos", e);
            }
        }).exceptionally(e -> {
            try {
                bot.execute(MessageUtil.sendText(chatId, "Failed to fetch open gym information. Please try again later."));
            } catch (TelegramApiException ex) {
                LOGGER.error("Failed to send error message", ex);
            }
            return null;
        });
    }

}
