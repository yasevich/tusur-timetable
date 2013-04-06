package com.synergy.android.timetable.plain;

public class Day {
    public boolean isEmpty;
    public int firstLesson;
    public Lesson[] lessons;
    
    public Day(int week, int day) {
        isEmpty = true;
        firstLesson = -1;
        lessons = new Lesson[7];
        for (int lesson = 0; lesson < lessons.length; ++lesson) {
            lessons[lesson] = new Lesson(new Lesson.PrimaryKey(week, day, lesson));
        }
    }
}
