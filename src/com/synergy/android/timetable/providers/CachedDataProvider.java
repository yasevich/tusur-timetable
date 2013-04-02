package com.synergy.android.timetable.providers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.synergy.android.timetable.plain.Day;
import com.synergy.android.timetable.plain.Lesson;
import com.synergy.android.timetable.plain.Week;
import com.synergy.android.timetable.utils.NumberUtils;
import com.synergy.sql.SqlColumnInfo;
import com.synergy.sql.SqlDataType;
import com.synergy.sql.SqlQueryHelper;

import java.util.ArrayList;
import java.util.List;

public class CachedDataProvider extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "timetable";
    private static final int DATABASE_VERSION = 1;
    
    private static final String KEY_ID = "ID";
    private static final String KEY_SUBJECT = "SUBJECT";
    private static final String KEY_KIND = "KIND";
    private static final String KEY_CLASSROOM = "CLASSROOM";
    private static final String KEY_TEACHER = "TEACHER";
    private static final String KEY_NOTE = "NOTE";
    private static final String KEY_ENABLED = "ENABLED";
    private static final String KEY_ORIGINAL = "ORIGINAL";
    
    private static CachedDataProvider instance;
    
    protected CachedDataProvider(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    public static synchronized CachedDataProvider getInstance(Context context) {
        if (instance == null) {
            instance = new CachedDataProvider(context);
        }
        return instance;
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        List<SqlColumnInfo> columns = new ArrayList<SqlColumnInfo>();
        columns.add(new SqlColumnInfo(KEY_ID, SqlDataType.INTEGER, true, true));
        columns.add(new SqlColumnInfo(KEY_SUBJECT, SqlDataType.TEXT, false));
        columns.add(new SqlColumnInfo(KEY_KIND, SqlDataType.TEXT, false));
        columns.add(new SqlColumnInfo(KEY_CLASSROOM, SqlDataType.TEXT, false));
        columns.add(new SqlColumnInfo(KEY_TEACHER, SqlDataType.TEXT, false));
        columns.add(new SqlColumnInfo(KEY_NOTE, SqlDataType.TEXT, false));
        columns.add(new SqlColumnInfo(KEY_ENABLED, SqlDataType.INTEGER, false));
        columns.add(new SqlColumnInfo(KEY_ORIGINAL, SqlDataType.INTEGER, false));
        db.execSQL(SqlQueryHelper.createTable(Lesson.class.getSimpleName(), columns));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // does nothing
    }
    
    public synchronized Week[] getWeeks() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = SqlQueryHelper.selectAll(Lesson.class.getSimpleName());
        Cursor cursor = db.rawQuery(sql, null);
        Week[] result = getWeeksFromCursor(cursor);
        cursor.close();
        db.close();
        return result;
    }
    
    public synchronized Lesson getLesson(int week, int day, int lesson) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = SqlQueryHelper.selectAll(Lesson.class.getSimpleName()) + " WHERE " +
                KEY_ID + " = " + buildPrimaryKey(week, day, lesson);
        Cursor cursor = db.rawQuery(sql, null);
        
        Lesson result = new Lesson();
        if (cursor.moveToFirst()) {
            final int indexSubject = cursor.getColumnIndex(KEY_SUBJECT);
            final int indexKind = cursor.getColumnIndex(KEY_KIND);
            final int indexClassroom = cursor.getColumnIndex(KEY_CLASSROOM);
            final int indexTeacher = cursor.getColumnIndex(KEY_TEACHER);
            final int indexNote = cursor.getColumnIndex(KEY_NOTE);
            final int indexEnabled = cursor.getColumnIndex(KEY_ENABLED);
            final int indexOriginal = cursor.getColumnIndex(KEY_ORIGINAL);
            
            result.subject = getNullableString(cursor.getString(indexSubject));
            result.kind = getNullableString(cursor.getString(indexKind));
            result.classroom = getNullableString(cursor.getString(indexClassroom));
            result.teacher = getNullableString(cursor.getString(indexTeacher));
            result.note = getNullableString(cursor.getString(indexNote));
            result.enabled = NumberUtils.intToBoolean(cursor.getInt(indexEnabled));
            result.original = NumberUtils.intToBoolean(cursor.getInt(indexOriginal));
        } else {
            result = null;
        }
        
        cursor.close();
        db.close();
        return result;
    }
    
    public synchronized void insertOrUpdateWeeks(Week[] weeks) {
        SQLiteDatabase db = getWritableDatabase();
        for (int i = 0; i < weeks.length; ++i) {
            Week week = weeks[i];
            for (int j = 0; j < week.days.length; ++j) {
                Day day = week.days[j];
                for (int k = 0; k < day.lessons.length; ++k) {
                    Lesson lesson = day.lessons[k];
                    List<String> columns = getDefaultColumnsList();
                    List<String> values =  getDefaultValuesArray(i, j, k, lesson);
                    String sql = SqlQueryHelper.insertOrReplace(Lesson.class.getSimpleName(),
                            columns, values);
                    db.execSQL(sql);
                }
            }
        }
        db.close();
    }
    
    private static Week[] getWeeksFromCursor(Cursor cursor) {
        Week[] weeks = Week.initTwoWeeks();
        if (cursor.moveToFirst()) {
            final int indexId = cursor.getColumnIndex(KEY_ID);
            final int indexSubject = cursor.getColumnIndex(KEY_SUBJECT);
            final int indexKind = cursor.getColumnIndex(KEY_KIND);
            final int indexClassroom = cursor.getColumnIndex(KEY_CLASSROOM);
            final int indexTeacher = cursor.getColumnIndex(KEY_TEACHER);
            final int indexNote = cursor.getColumnIndex(KEY_NOTE);
            final int indexEnabled = cursor.getColumnIndex(KEY_ENABLED);
            final int indexOriginal = cursor.getColumnIndex(KEY_ORIGINAL);
            
            do {
                int id = cursor.getInt(indexId);
                int weekIndex = id / 100 - 1;
                int dayIndex = (id / 10) % 10 - 1;
                int lessonIndex = id % 10 - 1;
                Day day = weeks[weekIndex].days[dayIndex];
                Lesson lesson = day.lessons[lessonIndex];
                lesson.subject = getNullableString(cursor.getString(indexSubject));
                lesson.kind = getNullableString(cursor.getString(indexKind));
                lesson.classroom = getNullableString(cursor.getString(indexClassroom));
                lesson.teacher = getNullableString(cursor.getString(indexTeacher));
                lesson.note = getNullableString(cursor.getString(indexNote));
                lesson.enabled = NumberUtils.intToBoolean(cursor.getInt(indexEnabled));
                lesson.original = NumberUtils.intToBoolean(cursor.getInt(indexOriginal));
                if (lesson.subject != null) {
                    day.isEmpty = false;
                    weeks[weekIndex].isEmpty = false;
                    if (day.firstLesson == -1 || day.firstLesson > lessonIndex) {
                        day.firstLesson = lessonIndex;
                    }
                }
            } while (cursor.moveToNext());
        } else {
            weeks = null;
        }
        return weeks;
    }
    
    private static String getNullableString(String str) {
        if (str.equalsIgnoreCase("null")) {
            return null;
        }
        return str;
    }
    
    private static List<String> getDefaultColumnsList() {
        List<String> list = new ArrayList<String>();
        list.add(KEY_ID);
        list.add(KEY_SUBJECT);
        list.add(KEY_KIND);
        list.add(KEY_CLASSROOM);
        list.add(KEY_TEACHER);
        list.add(KEY_NOTE);
        list.add(KEY_ENABLED);
        list.add(KEY_ORIGINAL);
        return list;
    }
    
    private static List<String> getDefaultValuesArray(int week, int day, int lesson, Lesson data) {
        List<String> list = new ArrayList<String>();
        list.add(buildPrimaryKey(week, day, lesson));
        list.add(getQuotedString(data.subject));
        list.add(getQuotedString(data.kind));
        list.add(getQuotedString(data.classroom));
        list.add(getQuotedString(data.teacher));
        list.add(getQuotedString(data.note));
        list.add(Integer.toString(NumberUtils.booleanToInt(data.enabled)));
        list.add(Integer.toString(NumberUtils.booleanToInt(data.original)));
        return list;
    }
    
    private static String buildPrimaryKey(int week, int day, int lesson) {
        if (week > 8 || day > 8 || lesson > 8) {
            throw new IllegalArgumentException("Unable to build primary key");
        }
        StringBuilder stringBuilder = new StringBuilder()
                .append(week + 1)
                .append(day + 1)
                .append(lesson + 1);
        return stringBuilder.toString();
    }
    
    private static String getQuotedString(String str) {
        return "\"" + str + "\"";
    }
}
