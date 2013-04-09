package com.synergy.android.timetable.parsers;

import com.synergy.android.timetable.plain.Group;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GroupsParser extends WebDataParser<List<Group>> {
    public static final String URL = "http://timetable.tusur.ru/api/v1/groups";
    
    @Override
    public List<Group> parse(String pageData) {
        try {
            JSONArray groups = new JSONObject(pageData).getJSONArray("groups");
            List<Group> result = new ArrayList<Group>();
            for (int i = 0; i < groups.length(); ++i) {
                JSONObject group = groups.getJSONObject(i);
                result.add(new Group(group.getString("number")));
            }
            return result;
        } catch (JSONException e) {
            return null;
        }
    }
}
