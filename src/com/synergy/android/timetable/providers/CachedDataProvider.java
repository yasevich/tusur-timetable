package com.synergy.android.timetable.providers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.synergy.android.timetable.ApplicationSettings;
import com.synergy.android.timetable.parsers.TimetableParser;
import com.synergy.android.timetable.parsers.WebDataParser;
import com.synergy.android.timetable.plain.Day;
import com.synergy.android.timetable.plain.Lesson;
import com.synergy.android.timetable.plain.Week;
import com.synergy.android.timetable.utils.NumberUtils;
import com.synergy.android.timetable.web.WebPageUtils;
import com.synergy.sql.SqlColumnInfo;
import com.synergy.sql.SqlDataType;
import com.synergy.sql.SqlQueryHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CachedDataProvider extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "timetable";
    private static final int DATABASE_VERSION = 2;
    
    private static final String TABLE_LESSON = Lesson.class.getSimpleName();
    
    private static final String KEY_ID = "ID";
    private static final String KEY_SUBJECT = "SUBJECT";
    private static final String KEY_SUBJECT_SHORT = "SUBJECT_SHORT";
    private static final String KEY_KIND = "KIND";
    private static final String KEY_KIND_SHORT = "KIND_SHORT";
    private static final String KEY_CLASSROOM = "CLASSROOM";
    private static final String KEY_CLASSROOM_SHORT = "CLASSROOM_SHORT";
    private static final String KEY_TEACHER = "TEACHER";
    private static final String KEY_TEACHER_SHORT = "TEACHER_SHORT";
    private static final String KEY_NOTE = "NOTE";
    private static final String KEY_ENABLED = "ENABLED";
    private static final String KEY_ORIGINAL = "ORIGINAL";
    private static final String KEY_IS_NEW = "IS_NEW";
    
    private static CachedDataProvider instance;
    
    private Context context;
    
    protected CachedDataProvider(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
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
        columns.add(new SqlColumnInfo(KEY_SUBJECT_SHORT, SqlDataType.TEXT, false));
        columns.add(new SqlColumnInfo(KEY_KIND, SqlDataType.TEXT, false));
        columns.add(new SqlColumnInfo(KEY_KIND_SHORT, SqlDataType.TEXT, false));
        columns.add(new SqlColumnInfo(KEY_CLASSROOM, SqlDataType.TEXT, false));
        columns.add(new SqlColumnInfo(KEY_CLASSROOM_SHORT, SqlDataType.TEXT, false));
        columns.add(new SqlColumnInfo(KEY_TEACHER, SqlDataType.TEXT, false));
        columns.add(new SqlColumnInfo(KEY_TEACHER_SHORT, SqlDataType.TEXT, false));
        columns.add(new SqlColumnInfo(KEY_NOTE, SqlDataType.TEXT, false));
        columns.add(new SqlColumnInfo(KEY_ENABLED, SqlDataType.INTEGER, false));
        columns.add(new SqlColumnInfo(KEY_ORIGINAL, SqlDataType.INTEGER, false));
        columns.add(new SqlColumnInfo(KEY_IS_NEW, SqlDataType.INTEGER, false));
        db.execSQL(SqlQueryHelper.createTable(TABLE_LESSON, columns));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
        case 1:
            db.execSQL(SqlQueryHelper.alterTableAddColumn(TABLE_LESSON,
                    new SqlColumnInfo(KEY_SUBJECT_SHORT, SqlDataType.TEXT, false)));
            db.execSQL(SqlQueryHelper.alterTableAddColumn(TABLE_LESSON,
                    new SqlColumnInfo(KEY_KIND_SHORT, SqlDataType.TEXT, false)));
            db.execSQL(SqlQueryHelper.alterTableAddColumn(TABLE_LESSON,
                    new SqlColumnInfo(KEY_CLASSROOM_SHORT, SqlDataType.TEXT, false)));
            db.execSQL(SqlQueryHelper.alterTableAddColumn(TABLE_LESSON,
                    new SqlColumnInfo(KEY_TEACHER_SHORT, SqlDataType.TEXT, false)));
            db.execSQL(SqlQueryHelper.alterTableAddColumn(TABLE_LESSON,
                    new SqlColumnInfo(KEY_IS_NEW, SqlDataType.INTEGER, false)));
            ApplicationSettings settings = ApplicationSettings.getInstance(context);
            try {
                String pageData = WebPageUtils.readPage(settings.getUrl());
                WebDataParser<Week[]> parser = new TimetableParser();
                Week[] weeks = parser.parse(pageData);
                insertOrUpdateWeeks(db, weeks);
            } catch (IOException e) {
                // do nothing
            }
            break;
        default:
            db.execSQL(SqlQueryHelper.dropTableIfExists(TABLE_LESSON));
            onCreate(db);
        }
    }
    
    public synchronized Week[] getWeeks() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = SqlQueryHelper.selectAll(TABLE_LESSON);
        Week[] result = null;
        
        Cursor cursor = db.rawQuery(sql, null);
        try {
            result = getWeeksFromCursor(cursor);
        } finally {
            cursor.close();
        }
        
        db.close();
        return result;
    }
    
    public synchronized Lesson getLesson(int week, int day, int lesson) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = SqlQueryHelper.selectAll(TABLE_LESSON) + " WHERE " +
                KEY_ID + " = " + Lesson.PrimaryKey.buildPrimaryKey(week, day, lesson);
        
        Cursor cursor = db.rawQuery(sql, null);
        Lesson result = null;
        try {
            if (cursor.moveToFirst()) {
                final int indexSubject = cursor.getColumnIndex(KEY_SUBJECT);
                final int indexSubjectShort = cursor.getColumnIndex(KEY_SUBJECT_SHORT);
                final int indexKind = cursor.getColumnIndex(KEY_KIND);
                final int indexKindShort = cursor.getColumnIndex(KEY_KIND_SHORT);
                final int indexClassroom = cursor.getColumnIndex(KEY_CLASSROOM);
                final int indexClassroomShort = cursor.getColumnIndex(KEY_CLASSROOM_SHORT);
                final int indexTeacher = cursor.getColumnIndex(KEY_TEACHER);
                final int indexTeacherShort = cursor.getColumnIndex(KEY_TEACHER_SHORT);
                final int indexNote = cursor.getColumnIndex(KEY_NOTE);
                final int indexEnabled = cursor.getColumnIndex(KEY_ENABLED);
                final int indexOriginal = cursor.getColumnIndex(KEY_ORIGINAL);
                final int indexIsNew = cursor.getColumnIndex(KEY_IS_NEW);
                
                result = new Lesson(new Lesson.PrimaryKey(week, day, lesson));
                result.subject = getNullableString(cursor.getString(indexSubject));
                result.subjectShort = getNullableString(cursor.getString(indexSubjectShort));
                result.kind = getNullableString(cursor.getString(indexKind));
                result.kindShort = getNullableString(cursor.getString(indexKindShort));
                result.classroom = getNullableString(cursor.getString(indexClassroom));
                result.classroomShort = getNullableString(cursor.getString(indexClassroomShort));
                result.teacher = getNullableString(cursor.getString(indexTeacher));
                result.teacherShort = getNullableString(cursor.getString(indexTeacherShort));
                result.note = getNullableString(cursor.getString(indexNote));
                result.enabled = NumberUtils.intToBoolean(cursor.getInt(indexEnabled));
                result.original = NumberUtils.intToBoolean(cursor.getInt(indexOriginal));
                result.isNew = NumberUtils.intToBoolean(cursor.getInt(indexIsNew));
            }
        } finally {
            cursor.close();
        }
        
        db.close();
        return result;
    }
    
    public synchronized void insertOrUpdateWeeks(Week[] weeks) {
        SQLiteDatabase db = getWritableDatabase();
        insertOrUpdateWeeks(db, weeks);
        db.close();
    }
    
    public synchronized void updateLesson(Lesson lesson) {
        ContentValues values = buildContentValues(lesson);
        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_LESSON, values, KEY_ID + " = ?",
                new String[] { lesson.getPrimaryKey().toString() });
        db.close();
    }
    
    public synchronized void insertOrUpdateWeeks(SQLiteDatabase db, Week[] weeks) {
        for (int i = 0; i < weeks.length; ++i) {
            Week week = weeks[i];
            for (int j = 0; j < week.days.length; ++j) {
                Day day = week.days[j];
                for (int k = 0; k < day.lessons.length; ++k) {
                    Lesson lesson = day.lessons[k];
                    List<String> columns = getDefaultColumnsList();
                    List<String> values =  getDefaultValuesArray(i, j, k, lesson);
                    String sql = SqlQueryHelper.insertOrReplace(TABLE_LESSON,
                            columns, values);
                    db.execSQL(sql);
                }
            }
        }
    }
    
    private static Week[] getWeeksFromCursor(Cursor cursor) {
        Week[] weeks = Week.initWeeksArray(2);
        if (cursor.moveToFirst()) {
            final int indexId = cursor.getColumnIndex(KEY_ID);
            final int indexSubject = cursor.getColumnIndex(KEY_SUBJECT);
            final int indexSubjectShort = cursor.getColumnIndex(KEY_SUBJECT_SHORT);
            final int indexKind = cursor.getColumnIndex(KEY_KIND);
            final int indexKindShort = cursor.getColumnIndex(KEY_KIND_SHORT);
            final int indexClassroom = cursor.getColumnIndex(KEY_CLASSROOM);
            final int indexClassroomShort = cursor.getColumnIndex(KEY_CLASSROOM_SHORT);
            final int indexTeacher = cursor.getColumnIndex(KEY_TEACHER);
            final int indexTeacherShort = cursor.getColumnIndex(KEY_TEACHER_SHORT);
            final int indexNote = cursor.getColumnIndex(KEY_NOTE);
            final int indexEnabled = cursor.getColumnIndex(KEY_ENABLED);
            final int indexOriginal = cursor.getColumnIndex(KEY_ORIGINAL);
            final int indexIsNew = cursor.getColumnIndex(KEY_IS_NEW);
            
            do {
                int id = cursor.getInt(indexId);
                Lesson.PrimaryKey pk = Lesson.PrimaryKey.getPrimaryKey(id);
                Day day = weeks[pk.getWeek()].days[pk.getDay()];
                Lesson lesson = day.lessons[pk.getLesson()];
                lesson.subject = getNullableString(cursor.getString(indexSubject));
                lesson.subjectShort = getNullableString(cursor.getString(indexSubjectShort));
                lesson.kind = getNullableString(cursor.getString(indexKind));
                lesson.kindShort = getNullableString(cursor.getString(indexKindShort));
                lesson.classroom = getNullableString(cursor.getString(indexClassroom));
                lesson.classroomShort = getNullableString(cursor.getString(indexClassroomShort));
                lesson.teacher = getNullableString(cursor.getString(indexTeacher));
                lesson.teacherShort = getNullableString(cursor.getString(indexTeacherShort));
                lesson.note = getNullableString(cursor.getString(indexNote));
                lesson.enabled = NumberUtils.intToBoolean(cursor.getInt(indexEnabled));
                lesson.original = NumberUtils.intToBoolean(cursor.getInt(indexOriginal));
                lesson.isNew = NumberUtils.intToBoolean(cursor.getInt(indexIsNew));
                if (lesson.subject != null) {
                    day.isEmpty = false;
                    weeks[pk.getWeek()].isEmpty = false;
                    if (day.firstLesson == -1 || day.firstLesson > pk.getLesson()) {
                        day.firstLesson = pk.getLesson();
                    }
                }
            } while (cursor.moveToNext());
        } else {
            weeks = null;
        }
        return weeks;
    }
    
    private static String getNullableString(String str) {
        if (str == null || str.equalsIgnoreCase("null")) {
            return null;
        }
        return str;
    }
    
    private static List<String> getDefaultColumnsList() {
        List<String> list = new ArrayList<String>();
        list.add(KEY_ID);
        list.add(KEY_SUBJECT);
        list.add(KEY_SUBJECT_SHORT);
        list.add(KEY_KIND);
        list.add(KEY_KIND_SHORT);
        list.add(KEY_CLASSROOM);
        list.add(KEY_CLASSROOM_SHORT);
        list.add(KEY_TEACHER);
        list.add(KEY_TEACHER_SHORT);
        list.add(KEY_NOTE);
        list.add(KEY_ENABLED);
        list.add(KEY_ORIGINAL);
        list.add(KEY_IS_NEW);
        return list;
    }
    
    private static List<String> getDefaultValuesArray(int week, int day, int lesson, Lesson data) {
        List<String> list = new ArrayList<String>();
        list.add(Lesson.PrimaryKey.buildPrimaryKey(week, day, lesson));
        list.add(getQuotedString(data.subject));
        list.add(getQuotedString(data.subjectShort));
        list.add(getQuotedString(data.kind));
        list.add(getQuotedString(data.kindShort));
        list.add(getQuotedString(data.classroom));
        list.add(getQuotedString(data.classroomShort));
        list.add(getQuotedString(data.teacher));
        list.add(getQuotedString(data.teacherShort));
        list.add(getQuotedString(data.note));
        list.add(Integer.toString(NumberUtils.booleanToInt(data.enabled)));
        list.add(Integer.toString(NumberUtils.booleanToInt(data.original)));
        list.add(Integer.toString(NumberUtils.booleanToInt(data.isNew)));
        return list;
    }
    
    private static String getQuotedString(String str) {
        return "\"" + str + "\"";
    }
    
    private static ContentValues buildContentValues(Lesson lesson) {
        ContentValues values = new ContentValues();
        values.put(KEY_SUBJECT, lesson.subject);
        values.put(KEY_SUBJECT_SHORT, lesson.subjectShort);
        values.put(KEY_KIND, lesson.kind);
        values.put(KEY_KIND_SHORT, lesson.kindShort);
        values.put(KEY_CLASSROOM, lesson.classroom);
        values.put(KEY_CLASSROOM_SHORT, lesson.classroomShort);
        values.put(KEY_TEACHER, lesson.teacher);
        values.put(KEY_TEACHER_SHORT, lesson.teacherShort);
        values.put(KEY_NOTE, lesson.note);
        values.put(KEY_ENABLED, Integer.toString(NumberUtils.booleanToInt(lesson.enabled)));
        values.put(KEY_ORIGINAL, Integer.toString(NumberUtils.booleanToInt(lesson.original)));
        values.put(KEY_IS_NEW, Integer.toString(NumberUtils.booleanToInt(lesson.isNew)));
        return values;
    }
}
