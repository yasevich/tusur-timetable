package com.synergy.android.timetable.utils;

import com.synergy.android.timetable.domains.Group;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GroupValidator {
    private static final String URL_FACULTY_PATTERN = "http://timetable.tusur.ru/faculties/%1$s";
    private static final String FACULTY_RTF = "rtf";
    private static final String FACULTY_RKF = "rkf";
    private static final String FACULTY_FET = "fet";
    private static final String FACULTY_FSU = "fsu";
    private static final String FACULTY_FVS = "fvs";
    private static final String FACULTY_GF = "gf";
    private static final String FACULTY_EF = "ef";
    private static final String FACULTY_FIT = "fit";
    private static final String FACULTY_YUF = "yuf";
    private static final String FACULTY_FMS = "fms";
    private static final String FACULTY_VF = "vf";
    
    @Deprecated
    public static String getFacultyUrlByGroup(String group) {
        String url = null;
        if (group.endsWith("В")) {
            url = String.format(URL_FACULTY_PATTERN, FACULTY_VF);
        } else if (group.startsWith("1")) {
            url = String.format(URL_FACULTY_PATTERN, FACULTY_RTF);
        } else if (group.startsWith("2")) {
            url = String.format(URL_FACULTY_PATTERN, FACULTY_RKF);
        } else if (group.startsWith("3")) {
            url = String.format(URL_FACULTY_PATTERN, FACULTY_FET);
        } else if (group.startsWith("4")) {
            url = String.format(URL_FACULTY_PATTERN, FACULTY_FSU);
        } else if (group.startsWith("5")) {
            url = String.format(URL_FACULTY_PATTERN, FACULTY_FVS);
        } else if (group.startsWith("6")) {
            url = String.format(URL_FACULTY_PATTERN, FACULTY_GF);
        } else if (group.startsWith("8")) {
            url = String.format(URL_FACULTY_PATTERN, FACULTY_EF);
        } else if (group.startsWith("9")) {
            url = String.format(URL_FACULTY_PATTERN, FACULTY_FIT);
        } else if (group.startsWith("0")) {
            if (group.startsWith("09")) {
                url = String.format(URL_FACULTY_PATTERN, FACULTY_YUF);
            } else {
                url = String.format(URL_FACULTY_PATTERN, FACULTY_FMS);
            }
        }
        return url;
    }
    
    public static String getGroupNumber(List<Group> groups, String group) {
        group = getGroupFormatString(group);
        Pattern pattern = StringUtils.compilePattern(group);
        Iterator<Group> iterator = groups.iterator();
        while (iterator.hasNext()) {
            Matcher matcher = pattern.matcher(iterator.next().number);
            if (matcher.find()) {
                return matcher.group();
            }
        }
        return null;
    }
    
    public static String getGroupFormatString(String group) {
        char[] chars = group.toCharArray();
        StringBuilder result = new StringBuilder()
                .append(chars[0]);
        for (int i = 1; i < chars.length; ++i) {
            result.append("(-)?").append(chars[i]);
        }
        return result.toString();
    }
}
