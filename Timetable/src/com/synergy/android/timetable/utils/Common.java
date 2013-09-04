package com.synergy.android.timetable.utils;

import com.synergy.android.timetable.TimetableApplication;
import com.synergy.android.timetable.listeners.SwitchableView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Common {
    public static void switchView(SwitchableView view, boolean isEnabled) {
        TimetableApplication app = TimetableApplication.getInstance();
        int color = isEnabled ? SwitchableView.DEFAULT_COLOR : app.getDataEmptyColor();
        view.switchTextColor(color);
    }

    public static Calendar getDateAwareCalendar() {
        Calendar calendar = new GregorianCalendar();

        // Check if the 1st of September is in two weeks. If so we need to download full timetable
        // for two weeks beginning from the 1st of September in advance.
        if (calendar.get(Calendar.MONTH) == Calendar.AUGUST && calendar.get(Calendar.DATE) >= 18) {
            calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
            calendar.set(Calendar.DATE, 1);
        }

        prepareCalendarNoSunday(calendar);
        return calendar;
    }

    public static boolean isWeekOdd(Calendar calendar) {
        return NumberUtils.isOdd(getWeekOfYear(calendar));
    }

    private static int getWeekOfYear(Calendar calendar) {
        Calendar september = new GregorianCalendar(calendar.get(Calendar.YEAR),
                Calendar.SEPTEMBER, 1);
        prepareCalendarNoSunday(september);

        if (calendar.compareTo(september) < 0) {
            september = new GregorianCalendar(calendar.get(Calendar.YEAR) - 1,
                    Calendar.SEPTEMBER, 1);
            prepareCalendarNoSunday(september);
        }
        prepareCalendarStartsMonday(september);

        long difference = MillisecondsIn.WEEK + calendar.getTimeInMillis() -
                september.getTimeInMillis();

        return (int) (difference / MillisecondsIn.WEEK);
    }

    private static void prepareCalendarNoSunday(Calendar calendar) {
        if (calendar.get(Calendar.DAY_OF_WEEK) == calendar.SUNDAY) {
            calendar.add(Calendar.DATE, 1);
        }
    }

    private static void prepareCalendarStartsMonday(Calendar calendar) {
        if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek == Calendar.SUNDAY) {
                calendar.add(Calendar.DATE, -6);
            } else {
                calendar.add(Calendar.DATE, -dayOfWeek + 2);
            }
        }
    }
}
