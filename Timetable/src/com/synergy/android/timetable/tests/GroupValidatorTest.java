package com.synergy.android.timetable.tests;

import com.synergy.android.timetable.domains.Group;
import com.synergy.android.timetable.parsers.GroupsParser;
import com.synergy.android.timetable.utils.GroupValidator;
import com.synergy.android.timetable.web.WebPageUtils;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class GroupValidatorTest extends TestCase {
    private static List<Group> groups;
    private static List<String> numbers;

    protected void setUp() throws Exception {
        String pageData = WebPageUtils.readPage(GroupsParser.URL);
        GroupsParser parser = new GroupsParser();
        groups = parser.parse(pageData);
        numbers = new ArrayList<String>();
        for (Group g : groups) {
            numbers.add(g.number.replace("-", ""));
        }
    }
    
    public void test() {
        for (String group : numbers) {
            List<String> validated = GroupValidator.getGroupNumber(groups, group);
            assertNotNull("Validated groups is null for: " + group, validated);
            assertEquals("The validated groups list is empty", true, validated.size() != 0);
            if (validated.size() > 1) {
                for (String v : validated) {
                    System.out.println(v);
                }
                System.out.println();
            }
        }
    }
}
