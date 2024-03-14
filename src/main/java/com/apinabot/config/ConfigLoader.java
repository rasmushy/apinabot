package com.apinabot.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
public class ConfigLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigLoader.class);
    public static String getBotToken() throws IOException {
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("application.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                LOGGER.error("Sorry, unable to find application.properties");
                return null;
            }
            prop.load(input);
            return prop.getProperty("bot.token");
        } catch (IOException ex) {
            LOGGER.error("Failed to load application.properties", ex);
            return null;
        }
    }
}

