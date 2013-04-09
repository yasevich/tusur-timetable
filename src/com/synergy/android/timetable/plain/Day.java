package com.synergy.android.timetable.plain;

import com.synergy.android.timetable.TimetableApplication;

public class Day {
    public boolean isEmpty;
    public int firstLesson;
    public Lesson[] lessons;
    
    public Day() {
        isEmpty = true;
        firstLesson = -1;
        lessons = new Lesson[TimetableApplication.NUMBER_OF_LESSONS];
        for (int lesson = 0; lesson < TimetableApplication.NUMBER_OF_LESSONS; ++lesson) {
            lessons[lesson] = new Lesson(null);
        }
    }
    
    public Day(int week, int day) {
        isEmpty = true;
        firstLesson = -1;
        lessons = new Lesson[TimetableApplication.NUMBER_OF_LESSONS];
        for (int lesson = 0; lesson < TimetableApplication.NUMBER_OF_LESSONS; ++lesson) {
            lessons[lesson] = new Lesson(new Lesson.PrimaryKey(week, day, lesson));
        }
    }
}
