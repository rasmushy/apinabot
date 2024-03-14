package com.apinabot.commands;

import com.apinabot.bots.ApinaBot;
/***
 * Implement each bot command as its own class implementing a common Command interface. This approach makes it easier to manage and add new commands.
 *
 */
public interface Command {
    void execute(Long chatId, ApinaBot bot);
    void execute(Long chatId, String[] args, ApinaBot bot);

}
