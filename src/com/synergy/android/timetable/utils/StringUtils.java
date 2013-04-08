package com.synergy.android.timetable.utils;

import java.util.regex.Pattern;

public class StringUtils {
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.length() == 0;
    }
    
    public static boolean nullEqual(String str1, String str2) {
        if (str1 == null && str2 == null) {
            return true;
        } else if (str1 == null || str2 == null) {
            return false;
        }
        return str1.equals(str2);
    }
    
    public static String toUpperCase(String input) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); ++i) {
            result.append(Character.toUpperCase(input.charAt(i)));
        }
        return result.toString();
    }
    
    public static Pattern compilePattern(String regex) {
        return Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    }
}
