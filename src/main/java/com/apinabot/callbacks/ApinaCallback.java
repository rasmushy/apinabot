package com.apinabot.callbacks;

import com.apinabot.bots.ApinaBot;
public interface ApinaCallback {
    void handleCallback(String callbackData, long chatId, int messageId, ApinaBot bot);
}
