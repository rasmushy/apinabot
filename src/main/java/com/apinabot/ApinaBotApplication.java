package com.apinabot;

import com.apinabot.bots.ApinaBot;
import com.apinabot.config.ConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;

/**
 * This class is the main class of the application. The class is used to start the bot.
 * The class uses the TelegramBotsApi class to register the bot.
 *
 * @author rasmushy
 */
public class ApinaBotApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApinaBotApplication.class);

    public static void main(String[] args) throws TelegramApiException, IOException {
        String botToken = ConfigLoader.getBotToken();
        LOGGER.debug("Starting bot");
        // The bot token is read from the environment
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        ApinaBot bot = new ApinaBot(botToken);
        botsApi.registerBot(bot);
    }
}
