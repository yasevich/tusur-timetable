package com.synergy.android.timetable.providers;

import android.content.Context;

import com.synergy.android.timetable.ApplicationSettings;
import com.synergy.android.timetable.TimetableApplication;
import com.synergy.android.timetable.parsers.LessonsParser;
import com.synergy.android.timetable.plain.Day;
import com.synergy.android.timetable.plain.Lesson;
import com.synergy.android.timetable.plain.Week;
import com.synergy.android.timetable.utils.NumberUtils;
import com.synergy.android.timetable.web.WebPageUtils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class WebDataProvider {
    private List<String> urls;
    
    public WebDataProvider(Context context) {
        ApplicationSettings settings = ApplicationSettings.getInstance(context);
        initUrls(settings.getUrl());
    }
    
    public Week[] getWeeks() {
        Week[] weeks = Week.initWeeksArray(2);
        LessonsParser parser = new LessonsParser();
        int index = 0;
        
        for (int i = 0; i < weeks.length; ++i) {
            Week week = weeks[i];
            for (int j = 0; j < week.days.length; ++j) {
                try {
                    String pageData = WebPageUtils.readPage(urls.get(index));
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
        urls = new ArrayList<String>();
        Calendar calendar = getCalendarMondayOddWeek();
        for (int i = 0; i < TimetableApplication.NUMBER_OF_DAYS * 2; ++i) {
            urls.add(url + dateFormat.format(calendar.getTime()));
            if (i == TimetableApplication.NUMBER_OF_DAYS - 1) {
                calendar.add(Calendar.DATE, 2);
            } else {
                calendar.add(Calendar.DATE, 1);
            }
        }
    }
    
    private Calendar getCalendarMondayOddWeek() {
        Calendar calendar = GregorianCalendar.getInstance();
        if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            int value = 2 - calendar.get(Calendar.DAY_OF_WEEK);
            if (value == 1) {
                value = -6;
            }
            calendar.add(Calendar.DATE, value);
        }
        if (!NumberUtils.isOdd(calendar.get(Calendar.WEEK_OF_YEAR))) {
            calendar.add(Calendar.WEEK_OF_YEAR, -1);
        }
        return calendar;
    }
}
