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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static com.apinabot.utils.MessageUtil.sendGymInfos;
public class CityCommand implements Command {

    private final ApinaApiService apinaApiService;
    private static final Logger LOGGER = LoggerFactory.getLogger(CityCommand.class);

    public CityCommand(ApinaApiService apinaApiService) {
        this.apinaApiService = apinaApiService;
    }

    @Override
    public void execute(Long chatId, ApinaBot bot) {
        LOGGER.debug("Executing city command");
        try {
            List<GymInfo> gyms = bot.getStateHandler().getAllGyms();
            Set<String> uniqueCities = new HashSet<>();
            for (GymInfo gym : gyms) {
                String city = gym.getAddress().getCity();
                uniqueCities.add(city);
            }
            InlineKeyboardMarkup cityKeyboard = KeyboardUtil.createCityKeyboard(new ArrayList<>(uniqueCities));
            bot.execute(MessageUtil.sendMenu(chatId, "Select a city:", cityKeyboard));
        } catch (TelegramApiException e) {
            LOGGER.error("Failed to send gym infos", e);
        }
    }

    @Override
    public void execute(Long chatId, String[] args, ApinaBot bot) {
        LOGGER.debug("Executing city command. Received args: {}", (Object) args);
        String cities = String.join(" ", args);
        CompletableFuture.supplyAsync(() -> apinaApiService.getGymsByCity(cities)).thenAccept(result -> {
            if (!result.isSuccess()) {
                LOGGER.error("Failed to fetch gyms for the selected city. {}", result.getError().getMessage());
                return;
            }
            try {
                List<GymInfo> gyms = result.getData();
                bot.execute(sendGymInfos(chatId, gyms));
            } catch (TelegramApiException e) {
                LOGGER.error("Failed to send gym infos", e);
            }
        }).exceptionally(e -> {
            try {
                bot.execute(MessageUtil.sendText(chatId, "Failed to fetch gym location information. Please try again later."));
            } catch (TelegramApiException ex) {
                LOGGER.error("Failed to send error message", ex);
            }
            return null;
        });
    }
}
