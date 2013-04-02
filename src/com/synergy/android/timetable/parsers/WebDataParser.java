package com.synergy.android.timetable.parsers;

import java.util.regex.Pattern;

public abstract class WebDataParser<T> {
    public abstract T parse(String pageData);
    
    protected static Pattern compilePattern(String pattern) {
        return Pattern.compile(pattern, Pattern.DOTALL);
    }
}
