package com.synergy.android.timetable.events;

import com.synergy.android.timetable.domains.Lesson;

public class LessonStateChanged extends Event {
    private Lesson.PrimaryKey primaryKey;
    private boolean enabled;
    
    public LessonStateChanged() {
    }

    public LessonStateChanged(Lesson lesson) {
        primaryKey = lesson.getPrimaryKey();
        enabled = lesson.enabled;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public Lesson.PrimaryKey getPrimaryKey() {
        return primaryKey;
    }
}
