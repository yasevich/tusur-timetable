package com.synergy.android.timetable.parsers;

import com.synergy.android.timetable.plain.Lesson;
import com.synergy.android.timetable.plain.Week;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimetableParser extends WebDataParser<Week[]> {
    private static final Pattern HOUR_PATTERN = compilePattern(
            "<tr class=\"lesson_(\\d)\">(.*?)</tr>");
    private static final Pattern DAY_PATTERN = compilePattern(
            "<td class=\"day_(\\d).*?\">(.*?)</td>");
    private static final Pattern DATA_PATTERN = compilePattern(
            "<div class=\"hidden for_print\">\\s+?<p class=\"discipline\">" +
            "(<abbr rel=\"tipsy\" title=\")?(.*?)(\">.*?</abbr>)?</p>\\s+?<p class=\"kind\">" +
            "(.*?)</p>\\s+?<p class=\"auditoriums\">(.*?)</p>\\s+?<p class=\"group\">(.*?)" +
            "</p>\\s+?(<p class=\"note\">(.+?)</p>\\s+?)?</div>");
    
    @Override
    public Week[] parse(String pageData) {
        Week[] weeks = Week.initTwoWeeks();
        Week week = weeks[0];
        Matcher hourMatcher = HOUR_PATTERN.matcher(pageData);
        while (hourMatcher.find()) {
            int hour = Integer.parseInt(hourMatcher.group(1));
            Matcher dayMatcher = DAY_PATTERN.matcher(hourMatcher.group(2));
            while (dayMatcher.find()) {
                int day = Integer.parseInt(dayMatcher.group(1));
                Matcher dataMatcher = DATA_PATTERN.matcher(dayMatcher.group(2));
                if (dataMatcher.find()) {
                    week.isEmpty = false;
                    week.days[day - 1].isEmpty = false;
                    week.days[day - 1].firstLesson = hour - 1;
                    Lesson lesson = week.days[day - 1].lessons[hour - 1];
                    lesson.subject = dataMatcher.group(2);
                    lesson.kind = dataMatcher.group(4);
                    lesson.classroom = dataMatcher.group(5);
                    lesson.teacher = dataMatcher.group(6);
                    lesson.note = dataMatcher.group(8);
                }
            }
            
            if (hour == 7) {
                week = weeks[1];
            }
        }
        return weeks;
    }
}
