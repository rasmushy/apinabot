package com.apinabot.handlers;

import com.apinabot.bots.ApinaBot;
import com.apinabot.commands.CommandFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Message;
public class MessageHandler {

    private final CommandFactory commandFactory;
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageHandler.class);

    public MessageHandler(CommandFactory commandFactory) {
        this.commandFactory = commandFactory;
    }
    public void handle(Message message, ApinaBot bot) {
        if (message.hasText()) {
            String text = message.getText();
            handleTextMessage(text, message, bot);
        }
    }

    private void handleTextMessage(String text, Message message, ApinaBot bot) {
        if (text.startsWith("/")) {
            String commandText = text.split("\\s+")[0].toLowerCase();
            //check for arguments
            if (text.split("\\s+").length > 1) {
                String[] args = text.substring(commandText.length()).trim().split("\\s+");
                LOGGER.debug("Received args: {}", (Object) args);
                commandFactory.getCommand(commandText).execute(message.getChatId(), args, bot);
                return;
            }
            commandFactory.getCommand(commandText).execute(message.getChatId(), bot);
        } else {
            // handle non-command messages
            LOGGER.debug("Received non-command message: {}", text);
        }
    }
}
