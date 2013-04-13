package com.synergy.android.timetable.domains;

import com.synergy.android.timetable.TimetableApplication;

public class Day {
    public Lesson[] lessons;
    
    public Day() {
        lessons = new Lesson[TimetableApplication.NUMBER_OF_LESSONS];
        for (int lesson = 0; lesson < TimetableApplication.NUMBER_OF_LESSONS; ++lesson) {
            lessons[lesson] = new Lesson(null);
        }
    }
    
    public Day(int week, int day) {
        lessons = new Lesson[TimetableApplication.NUMBER_OF_LESSONS];
        for (int lesson = 0; lesson < TimetableApplication.NUMBER_OF_LESSONS; ++lesson) {
            lessons[lesson] = new Lesson(new Lesson.PrimaryKey(week, day, lesson));
        }
    }
    
    public boolean isEmpty() {
        for (int i = 0; i < lessons.length; ++i) {
            if (lessons[i].subject != null) {
                return false;
            }
        }
        return true;
    }
    
    public int getFirstLessonIndex() {
        for (int i = 0; i < lessons.length; ++i) {
            if (lessons[i].subject != null && lessons[i].enabled) {
                return i;
            }
        }
        return -1;
    }
    
    public int getLastLessonIndex() {
        for (int i = lessons.length - 1; i >= 0; --i) {
            if (lessons[i].subject != null && lessons[i].enabled) {
                return i;
            }
        }
        return -1;
    }
}
