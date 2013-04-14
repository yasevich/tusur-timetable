package com.synergy.android.timetable;

import com.synergy.android.timetable.utils.NumberUtils;

import java.util.Calendar;

public class TimeStruct {
    public int week;
    public int day;
    public int lesson = -1;
    public int dayOffset = 0;
    public long inMillis = -1L;
    
    public TimeStruct(Calendar calendar) {
        week = NumberUtils.isOdd(calendar.get(Calendar.WEEK_OF_YEAR)) ?
                TimetableApplication.WEEK_ODD : TimetableApplication.WEEK_EVEN;
        day = calendar.get(Calendar.DAY_OF_WEEK);
        if (day == Calendar.SUNDAY) {
            day = 0;
            dayOffset++;
            if (calendar.getFirstDayOfWeek() == Calendar.MONDAY) {
                week ^= 1;
            }
        } else {
            day -= 2;
        }
    }
    
    public void nextDay() {
        lesson = -1;
        day++;
        dayOffset++;
        if (day == 6) {
            day = 0;
            dayOffset++;
            week ^= 1;
        }
    }
    
    @Override
    public String toString() {
        return "week = " + week + "; day = " + day + "; lesson = " + lesson;
    }
}
