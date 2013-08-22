package com.synergy.android.timetable.utils;

import com.synergy.android.timetable.TimetableApplication;
import com.synergy.android.timetable.listeners.SwitchableView;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Common {
    public static void switchView(SwitchableView view, boolean isEnabled) {
        TimetableApplication app = TimetableApplication.getInstance();
        int color = isEnabled ? SwitchableView.DEFAULT_COLOR : app.getDataEmptyColor();
        view.switchTextColor(color);
    }

    public static Calendar getCalendarNoSunday() {
        Calendar calendar = new GregorianCalendar();

        // Check if the 1st of September is in two weeks. If so we need to download full timetable
        // for two weeks beginning from the 1st of September in advance.
        if (calendar.get(Calendar.MONTH) == Calendar.AUGUST && calendar.get(Calendar.DATE) >= 18) {
            calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
            calendar.set(Calendar.DATE, 1);
        }

        // check if current day is Sunday
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            calendar.add(Calendar.DATE, 1);
        }

        return calendar;
    }
}
