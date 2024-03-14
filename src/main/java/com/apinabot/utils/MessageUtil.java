package com.apinabot.utils;

import com.apinabot.api.dto.GymInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.apinabot.utils.ParseUtil.*;
public class MessageUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageUtil.class);
    private MessageUtil() {
    }

    public static SendMessage sendText(Long who, String what) {
        LOGGER.debug("Sending message to {}: {}", who, what);
        return SendMessage.builder().chatId(who.toString()).text(what).build();
    }

    public static SendMessage sendMenu(Long chatId, String text, InlineKeyboardMarkup keyboard) {
        LOGGER.debug("Sending menu to {}: {}", chatId, text);
        return SendMessage.builder().chatId(chatId.toString()).text(text).replyMarkup(keyboard).build();
    }

    public static EditMessageText updateExistingMenu(Long chatId, String text, InlineKeyboardMarkup keyboard, int messageId) {
        LOGGER.debug("Updating menu in chat: {} to: {}", chatId, text);
        return EditMessageText.builder().chatId(chatId.toString()).messageId(messageId).text(text).replyMarkup(keyboard).build();
    }

    public static EditMessageReplyMarkup updateGymPollSelections(Long chatId, int messageId, InlineKeyboardMarkup keyboard) {
        LOGGER.debug("Updating Gym Poll in chat: {}", chatId);
        return EditMessageReplyMarkup.builder().chatId(chatId.toString()).messageId(messageId).replyMarkup(keyboard).build();
    }

    public static DeleteMessage deleteMessage(Long chatId, Integer messageId) {
        DeleteMessage clearingMessage = new DeleteMessage();
        clearingMessage.setChatId(chatId.toString());
        clearingMessage.setMessageId(messageId);
        return clearingMessage;
    }

    public static SendMessage sendGymInfos(Long chatId, List<GymInfo> gyms) {
        if (gyms == null || gyms.isEmpty()) {
            return SendMessage.builder().chatId(chatId.toString()).text("No gym information available at the moment.").build();
        }
        StringBuilder messageBuilder = new StringBuilder();
        for (GymInfo gym : gyms) {
            String info = formatGymInfo(gym);
            messageBuilder.append(info).append("\n");
        }
        return SendMessage.builder().chatId(chatId.toString()).text(messageBuilder.toString()).build();
    }

    public static EditMessageText gymInfoResponse(Long chatId, GymInfo gym, int messageId) {
        String info = formatGymInfo(gym);
        return EditMessageText.builder().chatId(chatId.toString()).messageId(messageId).text(info).build();
    }

    public static SendMessage sendPrices(Long chatId, List<GymInfo> prices) {
        StringBuilder messageBuilder = new StringBuilder();
        for (GymInfo price : prices) {
            String info = formatPriceInfo(price);
            messageBuilder.append(info).append("\n");

        }
        return SendMessage.builder().chatId(chatId.toString()).text(messageBuilder.toString()).build();
    }

    public static SendMessage sendOpenNow(Long chatId, List<GymInfo> openNow) {
        StringBuilder messageBuilder = new StringBuilder();
        for (GymInfo open : openNow) {
            String info = formatOpenNowInfo(open);
            messageBuilder.append(info).append("\n");
        }
        return SendMessage.builder().chatId(chatId.toString()).text(messageBuilder.toString()).build();
    }

}
