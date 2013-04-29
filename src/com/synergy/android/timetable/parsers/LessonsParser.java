package com.synergy.android.timetable.parsers;

import com.synergy.android.timetable.domains.Day;
import com.synergy.android.timetable.domains.Kind;
import com.synergy.android.timetable.domains.Lesson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LessonsParser extends WebDataParser<Day> {
    public static final String URL_FORMAT = "http://timetable.tusur.ru/api/v1/lessons/%1$s/";
    
    @Override
    public Day parse(String pageData) {
        Day day = new Day();
        try {
            JSONArray lessons = new JSONObject(pageData).getJSONArray("lessons");
            for (int i = 0; i < lessons.length(); ++i) {
                JSONObject lesson = lessons.getJSONObject(i);
                int index = lesson.getInt("lesson_number") - 1;
                Lesson l = day.lessons[index];
                l.classroom = lesson.getString("classroom").trim();
                l.classroomShort = shortenString(l.classroom);
                
                JSONObject inner = lesson.getJSONObject("discipline");
                l.subject = inner.getString("title").trim();
                l.subjectShort = inner.getString("abbr").trim();
                
                inner = lesson.getJSONObject("kind");
                l.kind = Kind.getKind(inner.getString("kind").trim());
                l.kindTitle = inner.getString("title").trim();
                l.kindShort = inner.getString("abbr").trim();
                
                JSONArray teachers = lesson.getJSONArray("lecturers");
                for (int j = 0; j < teachers.length(); ++j) {
                    JSONObject teacher = teachers.getJSONObject(j);
                    String name = teacher.getString("lastname").trim() + " " +
                            teacher.getString("firstname").charAt(0) + ". " +
                            teacher.getString("middlename").charAt(0) + ".";
                    if (l.teacher == null) {
                        l.teacher = name;
                        l.teacherShort = shortenString(name);
                    } else {
                        l.teacher += ", " + name;
                    }
                }
                
                l.note = lesson.getString("note").trim();
            }
            return day;
        } catch (JSONException e) {
            return null;
        }
    }
    
    private static final int MAX_SHORT_STRING_LENGTH = 10;
    
    private static String shortenString(String str) {
        if (str.length() > 10) {
            return str.substring(0, MAX_SHORT_STRING_LENGTH - 3) + "...";
        } else {
            return str;
        }
    }
}
