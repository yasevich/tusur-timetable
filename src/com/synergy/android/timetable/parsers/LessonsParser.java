package com.synergy.android.timetable.parsers;

import com.synergy.android.timetable.TimetableApplication;
import com.synergy.android.timetable.plain.Lesson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LessonsParser extends WebDataParser<Lesson[]> {
    @Override
    public Lesson[] parse(String pageData) {
        Lesson[] result = Lesson.initLessonsArray(TimetableApplication.NUMBER_OF_LESSONS);
        try {
            JSONArray lessons = new JSONObject(pageData).getJSONArray("lessons");
            for (int i = 0; i < lessons.length(); ++i) {
                JSONObject lesson = lessons.getJSONObject(i);
                Lesson l = result[lesson.getInt("lesson_number") - 1];
                l.classroom = lesson.getString("classroom").trim();
                l.classroomShort = shortenString(l.classroom);
                
                JSONObject inner = lesson.getJSONObject("discipline");
                l.subject = inner.getString("title").trim();
                l.subjectShort = inner.getString("abbr").trim();
                
                inner = lesson.getJSONObject("kind");
                l.kind = inner.getString("title").trim();
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
                
                l.note = lesson.getString("note");
            }
            return result;
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
