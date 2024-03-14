package com.apinabot.utils;

import com.apinabot.api.dto.GymInfo;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
public class PollUtil {

    private PollUtil() {
    }
    public static SendPoll createDayPoll(Long chatId, String[] args) {
        String pollTitle = String.join(" ", args);
        SendPoll weekdayPoll = new SendPoll();
        weekdayPoll.setChatId(chatId.toString());
        //Choose a day when we can go climbing
        weekdayPoll.setQuestion(pollTitle.isEmpty() ? "Choose a day when we can go climbing:" : pollTitle);
        weekdayPoll.setIsAnonymous(false);
        weekdayPoll.setOptions(Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"));
        weekdayPoll.setAllowMultipleAnswers(true);
        return weekdayPoll;
    }


    public static SendPoll createGymPoll(Long chatId, Set<String> selectedGyms) {
        List<String> pollOptions = new ArrayList<>(selectedGyms);
        pollOptions.replaceAll(s -> s.split("_")[0]);
        SendPoll sendPoll = new SendPoll();
        sendPoll.setChatId(chatId.toString());
        sendPoll.setQuestion("Which gym this week?");
        sendPoll.setOptions(pollOptions);
        sendPoll.setIsAnonymous(false);
        sendPoll.setAllowMultipleAnswers(true);
        return sendPoll;
    }

}
