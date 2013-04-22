package com.synergy.android.timetable.utils;

import com.synergy.android.timetable.domains.Group;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GroupValidator {
    public static String getGroupNumber(List<Group> groups, String group) {
        if (group == null) {
            return null;
        }
        
        group = getGroupFormatString(group);
        Pattern pattern = StringUtils.compilePattern(group);
        if (pattern == null) {
            return null;
        }
        
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
