package com.synergy.android.timetable.plain;

import com.synergy.android.timetable.utils.StringUtils;

public class Lesson {
    public String subject;
    public String subjectShort;
    public String kind;
    public String kindShort;
    public String classroom;
    public String classroomShort;
    public String teacher;
    public String teacherShort;
    public String note;
    public boolean enabled = true;
    public boolean original = true;
    public boolean isNew = false;
    
    @Override
    public boolean equals(Object o) {
        Lesson other = (Lesson) o;
        return StringUtils.nullEqual(subject, other.subject) &&
                StringUtils.nullEqual(subjectShort, other.subjectShort) &&
                StringUtils.nullEqual(kind, other.kind) &&
                StringUtils.nullEqual(kindShort, other.kindShort) &&
                StringUtils.nullEqual(classroom, other.classroom) &&
                StringUtils.nullEqual(classroomShort, other.classroomShort) &&
                StringUtils.nullEqual(teacher, other.teacher) &&
                StringUtils.nullEqual(teacherShort, other.teacherShort) &&
                StringUtils.nullEqual(note, other.note);
    }
    
    @Override
    public String toString() {
        return subject + " (" + subjectShort + ")";
    }
}
