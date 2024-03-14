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
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.apinabot.utils.MessageUtil.sendGymInfos;
public class CompanyCommand implements Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyCommand.class);
    private final ApinaApiService apinaApiService;

    public CompanyCommand(ApinaApiService apinaApiService) {
        this.apinaApiService = apinaApiService;
    }

    @Override
    public void execute(Long chatId, ApinaBot bot) {
        try {
            List<GymInfo> gyms = bot.getStateHandler().getAllGyms();
            Set<String> companySet = gyms.stream().map(gym -> gym.getCompany().getName()).collect(Collectors.toSet());
            InlineKeyboardMarkup companyKeyboard = KeyboardUtil.createCompanyKeyboard(new ArrayList<>(companySet));
            LOGGER.debug("Sending company keyboard: {}", companyKeyboard);
            bot.execute(MessageUtil.sendMenu(chatId, "Select a company:", companyKeyboard));
        } catch (TelegramApiException e) {
            LOGGER.error("Failed to send company infos", e);
        }
    }
    @Override
    public void execute(Long chatId, String[] args, ApinaBot bot) {
        LOGGER.debug("Received args: {}", (Object) args);
        String company = String.join(" ", args);
        LOGGER.debug("Received company: {}", company);
        CompletableFuture.supplyAsync(() -> apinaApiService.getGymsByCompany(company)).thenAccept(result -> {
            try {
                if (!result.isSuccess()) {
                    LOGGER.error("Failed to fetch gyms for the selected company. {}", result.getError().getMessage());
                    bot.execute(MessageUtil.sendText(chatId, "Failed to fetch gyms for the selected company. " + result.getError().getMessage()));
                    return;
                }
                List<GymInfo> gyms = result.getData();
                bot.execute(sendGymInfos(chatId, gyms));
            } catch (TelegramApiException e) {
                LOGGER.error("Failed to send gym infos", e);
            }
        }).exceptionally(e -> {
            try {
                bot.execute(MessageUtil.sendText(chatId, "Failed to fetch gyms for the selected company. Please try again later."));
            } catch (TelegramApiException ex) {
                LOGGER.error("Failed to send error message", ex);
            }
            return null;
        });
    }
}
