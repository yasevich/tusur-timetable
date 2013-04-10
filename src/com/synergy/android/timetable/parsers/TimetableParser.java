package com.synergy.android.timetable.parsers;

import com.synergy.android.timetable.domains.Lesson;
import com.synergy.android.timetable.domains.Week;
import com.synergy.android.timetable.utils.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Deprecated
public class TimetableParser extends WebDataParser<Week[]> {
    private static final Pattern HOUR_PATTERN = StringUtils.compilePattern(
            "<tr class=\"lesson_(\\d)\">(.*?)</tr>");
    private static final Pattern DAY_PATTERN = StringUtils.compilePattern(
            "<td class=\"day_(\\d).*?\">(.*?)</td>");
    private static final Pattern DATA_PATTERN = StringUtils.compilePattern(
            "<div class=\"exist_training \\S*?\">\\s+?<p class=\"discipline\">(.*?)</p>\\s+?" +
            "<p class=\"kind\">(.*?)</p>\\s+?<p class=\"auditoriums\">(.*?)</p>\\s+?" +
            "<p class=\"group\">(.*?)</p>\\s+?" +
            "(<span class=\"note\" rel=\"tipsy\" title=\"(.*?)\">(.*?)</span>\\s+?)?" +
            "</div>");
    private static final Pattern ABBR_PATTERN = StringUtils.compilePattern(
            "<abbr rel=\"tipsy\" title=\"(.*?)\">(.*?)</abbr>");
    
    @Override
    public Week[] parse(String pageData) {
        Week[] weeks = Week.initWeeksArray(2);
        int week = 0;
        Matcher hourMatcher = HOUR_PATTERN.matcher(pageData);
        while (hourMatcher.find()) {
            int hour = Integer.parseInt(hourMatcher.group(1));
            Matcher dayMatcher = DAY_PATTERN.matcher(hourMatcher.group(2));
            while (dayMatcher.find()) {
                int day = Integer.parseInt(dayMatcher.group(1));
                Matcher dataMatcher = DATA_PATTERN.matcher(dayMatcher.group(2));
                if (dataMatcher.find()) {
                    Lesson lesson = weeks[week].days[day - 1].lessons[hour - 1];
                    
                    String data = dataMatcher.group(1);
                    Matcher abbrMatcher = ABBR_PATTERN.matcher(data);
                    if (abbrMatcher.find()) {
                        lesson.subject = abbrMatcher.group(1);
                        lesson.subjectShort = abbrMatcher.group(2);
                    } else {
                        lesson.subject = data;
                        lesson.subjectShort = data;
                    }
                    
                    data = dataMatcher.group(2);
                    abbrMatcher = ABBR_PATTERN.matcher(data);
                    if (abbrMatcher.find()) {
                        lesson.kind = abbrMatcher.group(1);
                        lesson.kindShort = abbrMatcher.group(2);
                    } else {
                        lesson.kind = data;
                        lesson.kindShort = data;
                    }
                    
                    data = dataMatcher.group(3);
                    abbrMatcher = ABBR_PATTERN.matcher(data);
                    if (abbrMatcher.find()) {
                        lesson.classroom = abbrMatcher.group(1);
                        lesson.classroomShort = abbrMatcher.group(2);
                    } else {
                        lesson.classroom = data;
                        lesson.classroomShort = data;
                    }
                    
                    data = dataMatcher.group(4);
                    abbrMatcher = ABBR_PATTERN.matcher(data);
                    if (abbrMatcher.find()) {
                        lesson.teacher = abbrMatcher.group(1);
                        lesson.teacherShort = abbrMatcher.group(2);
                    } else {
                        lesson.teacher = data;
                        lesson.teacherShort = data;
                    }
                    
                    lesson.note = dataMatcher.group(6);
                }
            }
            
            if (hour == 7) {
                week = 1;
            }
        }
        return weeks;
    }
}
