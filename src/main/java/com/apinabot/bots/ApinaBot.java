package com.apinabot.bots;

import com.apinabot.api.ApinaApiService;
import com.apinabot.callbacks.CallbackFactory;
import com.apinabot.commands.CommandFactory;
import com.apinabot.handlers.CallbackQueryHandler;
import com.apinabot.handlers.MessageHandler;
import com.apinabot.handlers.StateHandler;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.apinabot.utils.ApinaUtil.helpText;

/***
 * Bot class that extends the TelegramLongPollingBot class. The class is used to handle incoming messages and callback queries.
 *
 * @author rasmushy
 */
public class ApinaBot extends TelegramLongPollingBot {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApinaBot.class);

    private final ApinaApiService apinaApiService;
    private final CallbackQueryHandler callbackQueryHandler;
    private final MessageHandler messageHandler;
    @Getter
    private final StateHandler stateHandler;

    public ApinaBot(String token) {
        super(token);
        this.apinaApiService = new ApinaApiService();
        this.messageHandler = new MessageHandler(new CommandFactory());
        this.callbackQueryHandler = new CallbackQueryHandler(new CallbackFactory());
        // Create a new state handler for the bot that is used to store the state of the bot
        this.stateHandler = new StateHandler();
        // Set the commands for the bot
        setMyCommands(this);
        // Fetch all gyms from the API
        fetchGyms();
    }

    @Override
    public String getBotUsername() {
        return "ApinaBot";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            LOGGER.debug("Received message: {}", update.getMessage().getText());
            messageHandler.handle(update.getMessage(), this);
        } else if (update.hasCallbackQuery()) {
            LOGGER.debug("Received callback query: {}", update.getCallbackQuery().getData());
            callbackQueryHandler.handle(update.getCallbackQuery(), this);
        }
    }

    public void setMyCommands(ApinaBot bot) {
        List<BotCommand> commands = new ArrayList<>();
        // Get the list of commands from helpText()
        String help = helpText();
        String[] helpArray = help.split("\n");
        for (String command : helpArray) {
            String[] commandArray = command.split(" - ");
            commands.add(new BotCommand(commandArray[0], commandArray[1]));
        }
        SetMyCommands setMyCommands = new SetMyCommands();
        setMyCommands.setCommands(commands);
        try {
            bot.execute(setMyCommands);
        } catch (TelegramApiException e) {
            LOGGER.error("Failed to set commands", e);
        }
    }

    public void fetchGyms() {
        if (stateHandler.gymsEmpty()) {
            CompletableFuture.supplyAsync(apinaApiService::getAllGyms).thenAccept(result -> {
                if (!result.isSuccess()) {
                    LOGGER.error("Failed to fetch gyms in startup. {}", result.getError().getMessage());
                    return;
                }
                this.getStateHandler().handleGymInfo(result.getData());
            }).exceptionally(e -> {
                LOGGER.debug("Failed to fetch all gyms.");
                return null;
            });
        }
    }

}
