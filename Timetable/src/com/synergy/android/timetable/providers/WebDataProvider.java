package com.synergy.android.timetable.providers;

import android.content.Context;

import com.synergy.android.timetable.ApplicationSettings;
import com.synergy.android.timetable.TimetableApplication;
import com.synergy.android.timetable.domains.Day;
import com.synergy.android.timetable.domains.Lesson;
import com.synergy.android.timetable.domains.Week;
import com.synergy.android.timetable.parsers.LessonsParser;
import com.synergy.android.timetable.utils.NumberUtils;
import com.synergy.android.timetable.web.WebPageUtils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class WebDataProvider implements Provider {
    private String[] urls;
    
    public WebDataProvider(Context context) {
        ApplicationSettings settings = ApplicationSettings.getInstance(context);
        initUrls(settings.getUrl());
    }
    
    @Override
    public Week[] getWeeks() {
        Week[] weeks = Week.initWeeksArray(2);
        LessonsParser parser = new LessonsParser();
        int index = 0;
        
        for (int i = 0; i < weeks.length; ++i) {
            Week week = weeks[i];
            for (int j = 0; j < week.days.length; ++j) {
                try {
                    String pageData = WebPageUtils.readPage(urls[index]);
                    if (pageData != null) {
                        Day day = parser.parse(pageData);
                        if (day == null) return null;
                        week.days[j] = day;
                        for (int k = 0; k < day.lessons.length; ++k) { 
                            day.lessons[k].setPrimaryKey(new Lesson.PrimaryKey(i, j, k));
                        }
                    }
                } catch (IOException e) {
                    return null;
                }
                index++;
            }
        }
        return weeks;
    }
    
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    
    private void initUrls(final String url) {
        urls = new String[TimetableApplication.NUMBER_OF_DAYS * 2];
        Calendar calendar = getCalendarNoSunday();
        int index = calendar.get(Calendar.DAY_OF_WEEK) - 2;
        if (!NumberUtils.isOdd(calendar.get(Calendar.WEEK_OF_YEAR))) {
            index += 6;
        }
        for (int i = 0; i < TimetableApplication.NUMBER_OF_DAYS * 2; ++i) {
            urls[index] = url + dateFormat.format(calendar.getTime());
            calendar.add(Calendar.DATE, 1);
            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                calendar.add(Calendar.DATE, 1);
            }
            index++;
            if (index == TimetableApplication.NUMBER_OF_DAYS * 2) {
                index = 0;
            }
        }
    }
    
    private Calendar getCalendarNoSunday() {
        Calendar calendar = GregorianCalendar.getInstance();

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
