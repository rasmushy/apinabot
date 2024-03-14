package com.apinabot.commands;

import com.apinabot.bots.ApinaBot;
import com.apinabot.utils.MessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static com.apinabot.utils.ApinaUtil.helpText;
public class StartCommand implements Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartCommand.class);

    @Override
    public void execute(Long chatId, ApinaBot bot) {
        String help = "Welcome to ApinaBot! I'm a simple info bot for indoor climbing gyms. Here are the commands you can use:\n";
        try {
            bot.execute(MessageUtil.sendText(chatId, help + helpText()));
        } catch (TelegramApiException e) {
            LOGGER.error("Failed to send welcome message", e);
        }
    }
    @Override
    public void execute(Long chatId, String[] args, ApinaBot bot) {
        try {
            bot.execute(MessageUtil.sendText(chatId, "I'm sorry, I don't understand that command."));
        } catch (TelegramApiException e) {
            LOGGER.error("Failed to send error message", e);
        }
    }

}
