package com.apinabot.utils;

import com.apinabot.api.dto.GymInfo;
import com.apinabot.bots.ApinaBot;
import com.apinabot.commands.CommandFactory;
import com.apinabot.commands.HelpCommand;
import com.apinabot.commands.StartCommand;
import com.apinabot.config.ConfigLoader;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
public class ApinaUtil {

    private ApinaUtil() {
    }

    public static String getCurrentTime() {
        //Finnish time
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Helsinki"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return now.format(formatter);
    }

    public static boolean isCurrentlyClosed(GymInfo gym) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Helsinki"));
        //weekday as "monday", "tuesday" etc
        String day = now.getDayOfWeek().toString().toLowerCase();
        String open = gym.getOpeningTime().getTime(day);
        String close = gym.getClosingTime().getTime(day);
        String currentTime = getCurrentTime();
        return currentTime.compareTo(open) < 0 || currentTime.compareTo(close) > 0;
    }

    public static List<GymInfo> getUniqueCompanies(List<GymInfo> gyms) {
        Map<String, GymInfo> uniqueCompanies = new LinkedHashMap<>();
        for (GymInfo gym : gyms) {
            String companyName = gym.getCompany().getName();
            uniqueCompanies.putIfAbsent(companyName, gym);
        }
        return new ArrayList<>(uniqueCompanies.values());
    }

    /**
     * Returns a help message for the bot.
     *
     * @return the help message
     * @see CommandFactory
     * @see HelpCommand
     * @see StartCommand
     * @see ApinaBot
     */
    public static String helpText() {
        String[] commandHelp = ConfigLoader.getBotCommandHelp().split(",");
        StringBuilder helpText = new StringBuilder();
        for (String command : commandHelp) {
            helpText.append(command).append("\n");
        }
        return helpText.toString();
    }
}
