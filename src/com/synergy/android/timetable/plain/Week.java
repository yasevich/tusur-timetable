package com.synergy.android.timetable.plain;

public class Week {
    public boolean isEmpty;
    public Day[] days;
    
    public Week(int week) {
        days = new Day[6];
        for (int day = 0; day < days.length; ++day) {
            days[day] = new Day(week, day);
        }
    }
    
    public static Week[] initWeeksArray(final int size) {
        Week[] weeks = new Week[size];
        for (int week = 0; week < size; ++week) {
            weeks[week] = new Week(week);
        }
        return weeks;
    }
}
