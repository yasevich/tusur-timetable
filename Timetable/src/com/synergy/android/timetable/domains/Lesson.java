package com.synergy.android.timetable.domains;

import com.synergy.android.timetable.utils.StringUtils;

public class Lesson {
    private PrimaryKey primaryKey;
    
    public String subject;
    public String subjectShort;
    public String kindTitle;
    public String kindShort;
    public String classroom;
    public String classroomShort;
    public String teacher;
    public String teacherShort;
    public String note;
    
    public Kind kind = Kind.UNKNOWN;
    
    public boolean enabled = true;
    public boolean original = true;
    public boolean isNew = false;
    
    public Lesson(PrimaryKey primaryKey) {
        this.primaryKey = primaryKey;
    }
    
    @Override
    public int hashCode() {
        return primaryKey.hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Lesson)) {
            return false;
        }

        Lesson other = (Lesson) o;
        return StringUtils.nullEqual(subject, other.subject) &&
                StringUtils.nullEqual(subjectShort, other.subjectShort) &&
                StringUtils.nullEqual(classroom, other.classroom) &&
                StringUtils.nullEqual(classroomShort, other.classroomShort) &&
                StringUtils.nullEqual(teacher, other.teacher) &&
                StringUtils.nullEqual(teacherShort, other.teacherShort) &&
                StringUtils.nullEqual(note, other.note) &&
                kind == other.kind;
    }
    
    @Override
    public String toString() {
        return subject + " (" + subjectShort + ")";
    }
    
    public void changeState() {
        enabled = !enabled;
    }
    
    public void setPrimaryKey(PrimaryKey primaryKey) {
        this.primaryKey = primaryKey;
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
        
        @Override
        public int hashCode() {
            return (week + 1) * 100 + (day + 1) * 10 + (lesson + 1);
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
