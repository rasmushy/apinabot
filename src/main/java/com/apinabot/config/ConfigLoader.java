package com.apinabot.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
public class ConfigLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigLoader.class);
    private static final String PROPERTIES_FILE = "application.properties";

    private static final Properties properties = new Properties();

    private ConfigLoader() {
    }

    public static void loadConfig() {
        LOGGER.debug("Loading application.properties");
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (input == null) {
                LOGGER.error("Sorry, unable to find application.properties");
                return;
            }
            properties.load(input);
        } catch (IOException ex) {
            LOGGER.error("Failed to load application.properties", ex);
        }
    }

    public static String getBotToken() {
        return properties.getProperty("bot.token");
    }

    public static String getBotUsername() {
        return properties.getProperty("bot.username");
    }

    public static String getBotWebhook() {
        return properties.getProperty("bot.webhook");
    }

    public static String getApiUrl() {
        return properties.getProperty("api.url");
    }

    public static String getBotCommands() {
        return properties.getProperty("bot.commands");
    }

    public static String getBotCommandHelp() {
        return properties.getProperty("bot.command.help");
    }
}

