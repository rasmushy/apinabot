package com.apinabot.utils;

import com.apinabot.api.dto.GymInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
public class ParseUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParseUtil.class);

    private ParseUtil() {
    }

    /**
     * This method is responsible for parsing the JSON response and converting it into a list of GymInfo objects.
     *
     * @param responseBody the JSON response from the API
     * @return a list of GymInfo objects
     */
    public static List<GymInfo> parseGymsInfo(String responseBody) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(responseBody, new TypeReference<>() {
            });
        } catch (Exception e) {
            LOGGER.error("Failed to parse gyms", e);
            return Collections.emptyList();
        }
    }

    /**
     * This method is responsible for parsing the JSON response and converting it into a GymInfo object.
     *
     * @param responseBody the JSON response from the API
     * @return a GymInfo object
     */
    public static GymInfo parseGymInfo(String responseBody) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(responseBody, GymInfo.class);
        } catch (Exception e) {
            LOGGER.error("Failed to parse gym", e);
            return null;
        }
    }

    public static String formatGymInfo(GymInfo gymInfo) {
        LocalDate today = LocalDate.now();
        String day = today.getDayOfWeek().toString().toLowerCase();
        String openingTime = gymInfo.getOpeningTime().getTime(day);
        String closingTime = gymInfo.getClosingTime().getTime(day);
        return gymInfo.getCompany().getName() + " - " +
               gymInfo.getAdditionalInfo() + "\n" +
               gymInfo.getCompany().getHomePage() + "\n" +
               gymInfo.getAddress().getStreet() + " " + gymInfo.getAddress().getNumber() + " - " +
               gymInfo.getAddress().getCity() + "\n" +
               "Open hours today: " + openingTime + " - " + closingTime + "\n";
    }

    public static String formatPriceInfo(GymInfo gymInfo) {
        StringBuilder priceInfo = new StringBuilder();
        gymInfo.getCompany().getPrices().forEach((product, price) -> {
            priceInfo.append(product).append(": ").append(price).append("\n");
        });
        return gymInfo.getCompany().getName() + " prices:\n" +
               priceInfo;
    }

    public static String formatOpenNowInfo(GymInfo gymInfo) {
        LocalDate today = LocalDate.now();
        String day = today.getDayOfWeek().toString().toLowerCase();
        String openingTime = gymInfo.getOpeningTime().getTime(day);
        String closingTime = gymInfo.getClosingTime().getTime(day);
        return gymInfo.getCompany().getName() + " - " +
               gymInfo.getAdditionalInfo() + "\n" +
               gymInfo.getAddress().getStreet() + " " + gymInfo.getAddress().getNumber() + " - " +
               gymInfo.getAddress().getCity() + "\n" +
               "Open hours today: " + openingTime + " - " + closingTime + "\n";
    }
}
