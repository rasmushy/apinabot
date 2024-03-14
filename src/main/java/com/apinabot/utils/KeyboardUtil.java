package com.apinabot.utils;

import com.apinabot.api.dto.GymInfo;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
/***
 * Utility classes like KeyboardUtil and MessageUtil can help reduce redundancy in your main bot class by providing common functionalities like building message formats or custom keyboards.
 * This class is responsible for creating custom keyboards for the bot.
 * The class is used by the MessageUtil class to create custom keyboards for the bot.
 */
public class KeyboardUtil {

    private KeyboardUtil() {
    }

    public static InlineKeyboardMarkup createGymKeyboard(List<String> gyms) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        for (String gym : gyms) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(gym);
            String callbackData = gym.substring(gym.indexOf("-") + 1).trim();
            button.setCallbackData("gym_" + callbackData);
            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(button);
            keyboard.add(row);
        }
        return new InlineKeyboardMarkup(keyboard);
    }

    public static InlineKeyboardMarkup createCityKeyboard(List<String> gyms) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        for (String gym : gyms) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(gym);
            button.setCallbackData("city_" + gym);
            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(button);
            keyboard.add(row);
        }
        return new InlineKeyboardMarkup(keyboard);
    }

    public static InlineKeyboardMarkup createCompanyKeyboard(List<String> companies) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        for (String company : companies) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(company);
            button.setCallbackData("company_" + company);
            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(button);
            keyboard.add(row);
        }
        return new InlineKeyboardMarkup(keyboard);
    }
    public static InlineKeyboardMarkup createCompanySelectionKeyboard(List<String> companies, String type) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        for (String company : companies) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(company);
            button.setCallbackData(type + "_" + company);
            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(button);
            keyboard.add(row);
        }
        return new InlineKeyboardMarkup(keyboard);
    }

    public static InlineKeyboardMarkup createGymPollKeyboard(Set<String> gyms) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        for (String gym : gyms) {
            String label = gyms.contains(gym) ? "✅ " + gym : gym;
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(label);
            button.setCallbackData("poll_" + gym);
            keyboard.add(Collections.singletonList(button));
        }
        InlineKeyboardButton confirmButton = new InlineKeyboardButton();
        confirmButton.setText("Confirm Selections");
        confirmButton.setCallbackData("poll_confirm");
        keyboard.add(Collections.singletonList(confirmButton));
        return new InlineKeyboardMarkup(keyboard);
    }

    public static InlineKeyboardMarkup createMenuForGymPoll(List<GymInfo> gyms) {
        Set<String> gymNames = gyms.stream().map(GymInfo::getDisplayName).collect(Collectors.toSet());
        return KeyboardUtil.createGymPollKeyboard(gymNames);
    }

    // Create a keyboard for poll menu to have quick way to create Gym or Day poll
    public static InlineKeyboardMarkup createPollKeyboard() {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        InlineKeyboardButton dayPollBtn = new InlineKeyboardButton();
        InlineKeyboardButton gymPollBtn = new InlineKeyboardButton();
        dayPollBtn.setText("Day poll");
        dayPollBtn.setCallbackData("menu_dayPoll");
        gymPollBtn.setText("Venue poll");
        gymPollBtn.setCallbackData("menu_gymPoll");
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(dayPollBtn);
        row.add(gymPollBtn);
        keyboard.add(row);
        return new InlineKeyboardMarkup(keyboard);
    }

    public static InlineKeyboardMarkup createMainMenu() {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        InlineKeyboardButton gymBtn = new InlineKeyboardButton();
        InlineKeyboardButton companyBtn = new InlineKeyboardButton();
        InlineKeyboardButton cityBtn = new InlineKeyboardButton();
        InlineKeyboardButton priceBtn = new InlineKeyboardButton();
        InlineKeyboardButton openNowBtn = new InlineKeyboardButton();
        InlineKeyboardButton pollBtn = new InlineKeyboardButton();
        gymBtn.setText("Gyms");
        gymBtn.setCallbackData("menu_gyms");
        companyBtn.setText("Companies");
        companyBtn.setCallbackData("menu_company");
        cityBtn.setText("Cities");
        cityBtn.setCallbackData("menu_city");
        priceBtn.setText("Prices");
        priceBtn.setCallbackData("menu_price");
        openNowBtn.setText("Open now");
        openNowBtn.setCallbackData("menu_open");
        pollBtn.setText("Poll");
        pollBtn.setCallbackData("menu_poll");
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(gymBtn);
        row.add(companyBtn);
        row.add(cityBtn);
        keyboard.add(row);
        row = new ArrayList<>();
        row.add(priceBtn);
        row.add(openNowBtn);
        row.add(pollBtn);
        keyboard.add(row);
        return new InlineKeyboardMarkup(keyboard);
    }
}
