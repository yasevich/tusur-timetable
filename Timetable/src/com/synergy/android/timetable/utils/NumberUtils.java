package com.synergy.android.timetable.utils;

public class NumberUtils {
    public static boolean isOdd(int number) {
        return number % 2 == 1;
    }
    
    public static boolean intToBoolean(int value) {
        return value != 0;
    }
    
    public static int booleanToInt(boolean value) {
        return value ? 1 : 0;
    }
}
