package com.apinabot.handlers;

import com.apinabot.bots.ApinaBot;
import com.apinabot.callbacks.ApinaCallback;
import com.apinabot.callbacks.CallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
/***
 *  Separate the logic for handling different types of updates (e.g., messages, callback queries) into different handler classes.
 */
public class CallbackQueryHandler {

    private final CallbackFactory callbackFactory;
    private static final Logger LOGGER = LoggerFactory.getLogger(CallbackQueryHandler.class);

    public CallbackQueryHandler(CallbackFactory callbackFactory) {
        this.callbackFactory = callbackFactory;
    }

    public void handle(CallbackQuery callbackQuery, ApinaBot bot) {
        String[] callbackData = callbackQuery.getData().split("_");
        String callbackType = callbackData[0];
        LOGGER.debug("Received callback type: {}", callbackType);
        ApinaCallback callback = callbackFactory.getCallback(callbackType);
        if (callback == null) {
            LOGGER.error("Callback not found for type: {}", callbackType);
            return;
        }
        String callbackParameter = callbackData[1];
        long chatId = callbackQuery.getMessage().getChatId();
        int messageId = callbackQuery.getMessage().getMessageId();
        callback.handleCallback(callbackParameter, chatId, messageId, bot);
    }
}
