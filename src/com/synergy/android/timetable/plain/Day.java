package com.synergy.android.timetable.plain;

public class Day {
    public boolean isEmpty;
    public int firstLesson;
    public Lesson[] lessons;
    
    public Day() {
        isEmpty = true;
        firstLesson = -1;
        lessons = new Lesson[7];
        for (int i = 0; i < lessons.length; ++i) {
            lessons[i] = new Lesson();
        }
    }
}
