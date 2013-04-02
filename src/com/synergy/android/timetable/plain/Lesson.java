package com.synergy.android.timetable.plain;

import com.synergy.android.timetable.utils.StringUtils;

public class Lesson {
    public String subject;
    public String kind;
    public String classroom;
    public String teacher;
    public String note;
    public boolean enabled = true;
    public boolean original = true;
    
    @Override
    public boolean equals(Object o) {
        Lesson other = (Lesson) o;
        return StringUtils.nullEqual(subject, other.subject) &&
                StringUtils.nullEqual(kind, other.kind) &&
                StringUtils.nullEqual(classroom, other.classroom) &&
                StringUtils.nullEqual(teacher, other.teacher) &&
                StringUtils.nullEqual(note, other.note);
    }
}
