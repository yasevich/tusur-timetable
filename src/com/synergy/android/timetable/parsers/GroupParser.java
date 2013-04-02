package com.synergy.android.timetable.parsers;

public class GroupParser {
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
}
