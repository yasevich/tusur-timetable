package com.synergy.android.timetable.domains;

import com.synergy.android.timetable.TimetableApplication;

public class Week {
    public Day[] days;
    
    public Week(int week) {
        days = new Day[TimetableApplication.NUMBER_OF_DAYS];
        for (int day = 0; day < TimetableApplication.NUMBER_OF_DAYS; ++day) {
            days[day] = new Day(week, day);
        }
    }
    
    public boolean isEmpty() {
        for (int i = 0; i < days.length; ++i) {
            if (!days[i].isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    public static Week[] initWeeksArray(final int size) {
        Week[] weeks = new Week[size];
        for (int week = 0; week < size; ++week) {
            weeks[week] = new Week(week);
        }
        return weeks;
    }
}
