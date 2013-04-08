package com.synergy.android.timetable.parsers;

import java.util.regex.Pattern;

public abstract class WebDataParser<T> {
    public abstract T parse(String pageData);
}
