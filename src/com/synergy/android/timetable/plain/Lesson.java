package com.synergy.android.timetable.plain;

import com.synergy.android.timetable.utils.StringUtils;

public class Lesson {
    private PrimaryKey primaryKey;
    
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
    
    public Lesson(PrimaryKey primaryKey) {
        this.primaryKey = primaryKey;
    }
    
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
    
    public PrimaryKey getPrimaryKey() {
        return primaryKey;
    }
    
    public static class PrimaryKey {
        private int week;
        private int day;
        private int lesson;
        
        public PrimaryKey(int week, int day, int lesson) {
            this.week = week;
            this.day = day;
            this.lesson = lesson;
        }
        
        @Override
        public String toString() {
            return buildPrimaryKey(week, day, lesson);
        }
        
        public int getWeek() {
            return week;
        }
        
        public int getDay() {
            return day;
        }
        
        public int getLesson() {
            return lesson;
        }
        
        public static PrimaryKey getPrimaryKey(int id) {
            return new PrimaryKey(id / 100 - 1, (id / 10) % 10 - 1, id % 10 - 1);
        }
        
        public static PrimaryKey getPrimaryKey(String tag) {
            try {
                return getPrimaryKey(Integer.parseInt(tag));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        
        public static String buildPrimaryKey(int week, int day, int lesson) {
            if (week > 8 || day > 8 || lesson > 8) {
                throw new IllegalArgumentException("Unable to build primary key");
            }
            StringBuilder stringBuilder = new StringBuilder()
                    .append(week + 1)
                    .append(day + 1)
                    .append(lesson + 1);
            return stringBuilder.toString();
        }
    }
}
