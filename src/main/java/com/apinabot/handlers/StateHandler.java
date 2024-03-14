package com.apinabot.handlers;

import com.apinabot.api.dto.GymInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handler for callback poll states
 *
 * @author rasmushy
 * @see CallbackQueryHandler
 */
public class StateHandler {
    private final Map<String, Map<Long, Set<String>>> stateMap = new ConcurrentHashMap<>();
    private final Map<String, GymInfo> allGyms = new ConcurrentHashMap<>();

    public StateHandler() {
        Map<Long, Set<String>> pollStates = new ConcurrentHashMap<>();
        Map<Long, Set<String>> priceStates = new ConcurrentHashMap<>();
        Map<Long, Set<String>> openStates = new ConcurrentHashMap<>();
        stateMap.put("poll", pollStates);
        stateMap.put("price", priceStates);
        stateMap.put("open", openStates);
    }

    public void handleGymInfo(List<GymInfo> gymInfos) {
        gymInfos.forEach(gymInfo -> allGyms.put(gymInfo.getAddress().getStreet(), gymInfo));
    }

    public GymInfo getGymInfo(String streetName) {
        return allGyms.get(streetName);
    }

    public List<GymInfo> getAllGyms() {
        return List.copyOf(allGyms.values());
    }

    public List<String> getAllGymsDisplayName() {
        return allGyms.values().stream().map(GymInfo::getDisplayName).toList();
    }

    public boolean gymsEmpty() {
        return allGyms.isEmpty();
    }

    public void addSelection(Long chatId, String selection, String type) {
        stateMap.get(type).computeIfAbsent(chatId, k -> ConcurrentHashMap.newKeySet()).add(selection);
    }
    public void addSelections(Long chatId, List<String> companyNames, String type) {
        stateMap.get(type).computeIfAbsent(chatId, k -> ConcurrentHashMap.newKeySet()).addAll(companyNames);
    }

    public void removeSelection(Long chatId, String selection, String type) {
        stateMap.get(type).computeIfPresent(chatId, (k, v) -> {
            v.remove(selection);
            return v.isEmpty() ? null : v;
        });
    }

    public Set<String> getSelections(Long chatId, String type) {
        return stateMap.get(type).getOrDefault(chatId, Set.of());
    }

    public boolean isEmpty(Long chatId, String type) {
        return stateMap.get(type).getOrDefault(chatId, Set.of()).isEmpty();
    }

    public void clearState(Long chatId, String type) {
        stateMap.get(type).remove(chatId);
    }

}
