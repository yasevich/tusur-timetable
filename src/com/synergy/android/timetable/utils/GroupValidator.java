package com.synergy.android.timetable.utils;

import com.synergy.android.timetable.domains.Group;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GroupValidator {
    public static List<String> getGroupNumber(List<Group> groups, String group) {
        if (group == null || groups == null) {
            return null;
        }
        
        List<String> result = new ArrayList<String>();
        group = getGroupFormatString(group);
        Pattern pattern = StringUtils.compilePattern(group);
        if (pattern == null) {
            return null;
        }
        
        Iterator<Group> iterator = groups.iterator();
        while (iterator.hasNext()) {
            String candidate = iterator.next().number;
            Matcher matcher = pattern.matcher(candidate);
            if (matcher.find()) {
                String firstChar = group.substring(0, 1);
                String lastChar = group.substring(group.length() - 1, group.length());
                if (candidate.startsWith(firstChar) && candidate.endsWith(lastChar)) {
                    result.add(candidate);
                }
            }
        }
        
        return result;
    }
    
    private static String getGroupFormatString(String group) {
        char[] chars = group.toCharArray();
        StringBuilder result = new StringBuilder()
                .append(chars[0]);
        for (int i = 1; i < chars.length; ++i) {
            result.append("(-)?").append(chars[i]);
        }
        return result.toString();
    }
}
