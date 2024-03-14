package com.apinabot.commands;

import com.apinabot.bots.ApinaBot;
import com.apinabot.utils.KeyboardUtil;
import com.apinabot.utils.MessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
public class MenuCommand implements Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(MenuCommand.class);

    @Override
    public void execute(Long chatId, ApinaBot bot) {
        LOGGER.debug("Executing menu command");
        try {
            InlineKeyboardMarkup mainMenu = KeyboardUtil.createMainMenu();
            bot.execute(MessageUtil.sendMenu(chatId, "ApinaBot menu: ", mainMenu));
        } catch (TelegramApiException e) {
            LOGGER.error("Failed to send main menu", e);
        }
    }
    @Override
    public void execute(Long chatId, String[] args, ApinaBot bot) {
        LOGGER.debug("Received args for menu command: {}", (Object) args);
        try {
            bot.execute(MessageUtil.sendText(chatId, "I'm sorry, I don't understand that command."));
        } catch (TelegramApiException e) {
            LOGGER.error("Failed to send error message", e);
        }
    }

}
