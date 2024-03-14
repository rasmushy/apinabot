package com.apinabot.callbacks;

import com.apinabot.api.ApinaApiService;
import com.apinabot.handlers.StateHandler;

import java.util.HashMap;
import java.util.Map;
public class CallbackFactory {
    private final Map<String, ApinaCallback> callbackMap = new HashMap<>();

    public CallbackFactory() {
        callbackMap.put("menu", new MenuCallback(new ApinaApiService()));
        callbackMap.put("gym", new GymCallback());
        callbackMap.put("company", new CompanyCallback(new ApinaApiService()));
        callbackMap.put("city", new CityCallback(new ApinaApiService()));
        callbackMap.put("open", new OpenCallback(new ApinaApiService()));
        callbackMap.put("price", new PriceCallback(new ApinaApiService()));
        callbackMap.put("poll", new PollCallback());
    }

    public ApinaCallback getCallback(String callback) {
        String callbackText = callback.split("_")[0];
        return callbackMap.getOrDefault(callbackText, null);
    }
}
