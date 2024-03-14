package com.apinabot.commands;

import com.apinabot.api.ApinaApiService;

import java.util.HashMap;
import java.util.Map;
/***
 * Factory class that returns the command object based on the command string.
 */
public class CommandFactory {

    private final Map<String, Command> commandMap = new HashMap<>();

    public CommandFactory() {
        setCommands();
    }
    public Command getCommand(String command) {
        String commandText = command.split(" ")[0];
        return commandMap.getOrDefault(commandText, new HelpCommand());
    }

    private void setCommands() {
        commandMap.put("/start", new StartCommand());
        commandMap.put("/help", new HelpCommand());
        commandMap.put("/menu", new MenuCommand());
        commandMap.put("/gyms", new GymsCommand(new ApinaApiService()));
        commandMap.put("/city", new CityCommand(new ApinaApiService()));
        commandMap.put("/company", new CompanyCommand(new ApinaApiService()));
        commandMap.put("/price", new PriceCommand(new ApinaApiService()));
        commandMap.put("/open", new OpenCommand(new ApinaApiService()));
        commandMap.put("/poll", new PollCommand());
    }
}
