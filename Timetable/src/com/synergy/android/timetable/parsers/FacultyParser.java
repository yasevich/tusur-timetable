package com.synergy.android.timetable.parsers;

import com.synergy.android.timetable.utils.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Deprecated
public class FacultyParser extends WebDataParser<String> {
    private static final String URL = "http://timetable.tusur.ru";
    private static final String FORMAT = "<li>\\s+?<h2><a href=\"(\\S*?)\">%1$s</a></h2>\\s+?</li>";
    
    private Pattern groupPattern;
    
    public FacultyParser(String group) {
        groupPattern = StringUtils.compilePattern(String.format(FORMAT, group));
    }
    
    @Override
    public String parse(String pageData) {
        Matcher matcher = groupPattern.matcher(pageData);
        if (matcher.find()) {
            return URL + matcher.group(1);
        }
        return null;
    }
}
