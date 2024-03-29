package com.apinabot.callbacks;

import com.apinabot.api.ApinaApiService;
import com.apinabot.bots.ApinaBot;
import com.apinabot.commands.*;
import com.apinabot.utils.MessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static com.apinabot.utils.KeyboardUtil.createMenuForGymPoll;
import static com.apinabot.utils.PollUtil.createDayPoll;

public class MenuCallback implements ApinaCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(MenuCallback.class);
    private final ApinaApiService apinaApiService;

    public MenuCallback(ApinaApiService apinaApiService) {
        this.apinaApiService = apinaApiService;
    }

    @Override
    public void handleCallback(String callbackData, long chatId, int messageId, ApinaBot bot) {
        LOGGER.debug("Handling menu callback with data: {}", callbackData);
        switch (callbackData) {
            case "gyms":
                new GymsCommand(apinaApiService).execute(chatId, bot);
                break;
            case "city":
                new CityCommand(apinaApiService).execute(chatId, bot);
                break;
            case "company":
                new CompanyCommand(apinaApiService).execute(chatId, bot);
                break;
            case "price":
                new PriceCommand(apinaApiService).execute(chatId, bot);
                break;
            case "open":
                new OpenCommand(apinaApiService).execute(chatId, bot);
                break;
            case "poll":
                new PollCommand().execute(chatId, bot);
                break;
            case "dayPoll":
                try {
                    bot.execute(createDayPoll(chatId, new String[]{"Choose a day when we can go climbing:"}));
                    bot.execute(MessageUtil.deleteMessage(chatId, messageId));
                } catch (TelegramApiException e) {
                    LOGGER.error("Failed to send day poll", e);
                }
                break;
            case "gymPoll":
                try {
                    bot.execute(MessageUtil.sendMenu(chatId, "Select gyms to include in the poll:", createMenuForGymPoll(bot.getStateHandler().getAllGyms())));
                    bot.execute(MessageUtil.deleteMessage(chatId, messageId));
                } catch (Exception e) {
                    LOGGER.error("Failed to send gym selection menu", e);
                }
                break;
            default:
                LOGGER.error("Unknown menu item selected: {}", callbackData);
                try {
                    bot.execute(MessageUtil.sendText(chatId, "Unknown menu item selected. Please try again."));
                } catch (TelegramApiException e) {
                    LOGGER.error("Failed to send error message", e);
                }
                break;
        }
    }

    @Override
    public String toString() {
        return "menu";
    }
}
