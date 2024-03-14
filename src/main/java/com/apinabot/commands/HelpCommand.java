package com.apinabot.commands;

import com.apinabot.bots.ApinaBot;
import com.apinabot.utils.MessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static com.apinabot.utils.ApinaUtil.helpText;
public class HelpCommand implements Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(HelpCommand.class);

    public void execute(Long chatId, ApinaBot bot) {
        LOGGER.debug("Executing help command");
        try {
            bot.execute(MessageUtil.sendText(chatId, helpText()));
        } catch (TelegramApiException e) {
            LOGGER.error("Failed to send help message", e);
        }
    }
    @Override
    public void execute(Long chatId, String[] args, ApinaBot bot) {
        LOGGER.debug("Received args for help command: {}", (Object) args);
        try {
            bot.execute(MessageUtil.sendText(chatId, "I'm sorry, I don't understand that command."));
        } catch (TelegramApiException e) {
            LOGGER.error("Failed to send error message", e);
        }
    }
}
