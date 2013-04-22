package com.synergy.android.timetable.events;

import com.synergy.android.timetable.domains.Lesson;

public class LessonStateChanged extends Event {
    private Lesson.PrimaryKey primaryKey;
    private boolean enabled;
    
    public LessonStateChanged() {
    }

    public LessonStateChanged(Subscriber subscriber, Lesson.PrimaryKey pk, boolean state) {
        super(subscriber);
        primaryKey = pk;
        enabled = state;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public Lesson.PrimaryKey getPrimaryKey() {
        return primaryKey;
    }
}
