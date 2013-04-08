package com.synergy.android.timetable.parsers;

public abstract class WebDataParser<T> {
    public abstract T parse(String pageData);
}
