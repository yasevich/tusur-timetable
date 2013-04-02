package com.synergy.android.timetable.plain;

public class Week {
    public boolean isEmpty;
    public Day[] days;
    
    public Week() {
        days = new Day[6];
        for (int i = 0; i < days.length; ++i) {
            days[i] = new Day();
        }
    }
    
    public static Week[] initTwoWeeks() {
        Week[] weeks = new Week[2];
        for (int i = 0; i < weeks.length; ++i) {
            weeks[i] = new Week();
        }
        return weeks;
    }
}
