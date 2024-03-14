package com.apinabot.commands;

import com.apinabot.api.ApinaApiService;
import com.apinabot.api.dto.GymInfo;
import com.apinabot.bots.ApinaBot;
import com.apinabot.utils.KeyboardUtil;
import com.apinabot.utils.MessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
public class GymsCommand implements Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(GymsCommand.class);
    private final ApinaApiService apinaApiService;

    public GymsCommand(ApinaApiService apinaApiService) {
        this.apinaApiService = apinaApiService;
    }

    @Override
    public void execute(Long chatId, ApinaBot bot) {
        try {
            InlineKeyboardMarkup gymKeyboard = KeyboardUtil.createGymKeyboard(bot.getStateHandler().getAllGymsDisplayName());
            LOGGER.debug("Sending gym keyboard: {}", gymKeyboard);
            bot.execute(MessageUtil.sendMenu(chatId, "Select a climbing gym:", gymKeyboard));
        } catch (TelegramApiException e) {
            LOGGER.error("Failed to send gym infos", e);
        }
    }
    @Override
    public void execute(Long chatId, String[] args, ApinaBot bot) {
        LOGGER.debug("Received args: {}", (Object) args);
        String infos = String.join(" ", args);
        LOGGER.debug("Received info: {}", infos);
        CompletableFuture.supplyAsync(() -> apinaApiService.getGymsByInfo(infos))
                .thenAccept(result -> {
                    if (!result.isSuccess()) {
                        LOGGER.error("Failed to fetch gyms for the selected info. {}", result.getError().getMessage());
                        return;
                    }
                    try {
                        List<GymInfo> gyms = result.getData();
                        bot.execute(MessageUtil.sendGymInfos(chatId, gyms));
                    } catch (TelegramApiException e) {
                        LOGGER.error("Failed to send gym infos", e);
                    }
                })
                .exceptionally(e -> {
                    try {
                        bot.execute(MessageUtil.sendText(chatId, "Failed to fetch gym information. Please try again later."));
                    } catch (TelegramApiException ex) {
                        LOGGER.error("Failed to send error message", ex);
                    }
                    return null;
                });

    }
}
