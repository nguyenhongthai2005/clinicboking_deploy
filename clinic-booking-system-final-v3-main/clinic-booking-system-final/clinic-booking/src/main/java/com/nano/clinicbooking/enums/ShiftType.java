package com.nano.clinicbooking.enums;

import java.time.LocalTime;

public enum ShiftType {
    MORNING("08:00", "12:00"),
    AFTERNOON("13:00", "17:00"),
    EVENING("18:00", "21:00");

    private final String start;
    private final String end;

    ShiftType(String start, String end) {
        this.start = start;
        this.end = end;
    }

    public LocalTime getStartTime() {
        return LocalTime.parse(start);
    }

    public LocalTime getEndTime() {
        return LocalTime.parse(end);
    }
}
