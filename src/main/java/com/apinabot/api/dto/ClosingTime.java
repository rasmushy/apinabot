package com.apinabot.api.dto;

public class ClosingTime extends OpeningTime {

    public ClosingTime(String sunday, String saturday, String tuesday, String thursday, String wednesday, String friday, String monday) {
        super(sunday, saturday, tuesday, thursday, wednesday, friday, monday);
    }

    public ClosingTime() {
    }
    @Override
    public String toString() {
        return "ClosingTime{" +
               "sunday='" + getSunday() + '\'' +
               ", saturday='" + getSaturday() + '\'' +
               ", tuesday='" + getTuesday() + '\'' +
               ", thursday='" + getThursday() + '\'' +
               ", wednesday='" + getWednesday() + '\'' +
               ", friday='" + getFriday() + '\'' +
               ", monday='" + getMonday() + '\'' +
               '}';
    }
}
