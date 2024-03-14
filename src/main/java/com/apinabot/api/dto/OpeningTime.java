package com.apinabot.api.dto;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class OpeningTime {
    private String sunday;
    private String saturday;
    private String tuesday;
    private String thursday;
    private String wednesday;
    private String friday;
    private String monday;

    public OpeningTime(String sunday, String saturday, String tuesday, String thursday, String wednesday, String friday, String monday) {
        this.sunday = sunday;
        this.saturday = saturday;
        this.tuesday = tuesday;
        this.thursday = thursday;
        this.wednesday = wednesday;
        this.friday = friday;
        this.monday = monday;
    }

    public OpeningTime() {
    }

    public String getTime(String day) {
        return switch (day) {
            case "sunday" -> sunday;
            case "saturday" -> saturday;
            case "tuesday" -> tuesday;
            case "thursday" -> thursday;
            case "wednesday" -> wednesday;
            case "friday" -> friday;
            case "monday" -> monday;
            default -> null;
        };
    }

    @Override
    public String toString() {
        return "OpeningTime{" +
               "sunday='" + sunday + '\'' +
               ", saturday='" + saturday + '\'' +
               ", tuesday='" + tuesday + '\'' +
               ", thursday='" + thursday + '\'' +
               ", wednesday='" + wednesday + '\'' +
               ", friday='" + friday + '\'' +
               ", monday='" + monday + '\'' +
               '}';
    }

}
