package com.synergy.android.timetable;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;

import com.synergy.android.timetable.parsers.LessonsParser;

public class ApplicationSettings {
    public static final String APP_EMPTY = "appEmpty";
    public static final String SILENT_MODE_ENABLED = "silentModeEnabled";
    public static final String NOTIFICATIONS_ENABLED = "notificationsEnabled";
    public static final String NOTIFICATIONS_TIME = "notificationsTime";
    
    private static final String GENERAL_GROUP = "generalGroup";
    private static final String GENERAL_URL = "generalUrl";
    private static final String PREVOIUS_RINGER_MODE = "previousRingerMode";
    
    private static ApplicationSettings instance;
    
    private Context context;
    private SharedPreferences preferences;
    private String[] notificationsTimes;
    
    private ApplicationSettings(Context context) {
        this.context = context;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.notificationsTimes = context.getResources().getStringArray(R.array.time);
    }
    
    public static synchronized ApplicationSettings getInstance(Context context) {
        if (instance == null) {
            instance = new ApplicationSettings(context);
        }
        String url = instance.getUrl();
        if (url != null && url.startsWith("http://timetable.tusur.ru/faculties/")) {
            int index = url.lastIndexOf('/');
            url = String.format(LessonsParser.URL_FORMAT, url.substring(index + 1));
            instance.setUrl(url);
        }
        return instance;
    }
    
    public synchronized void registerOnSharedPreferenceChangeListener(
            OnSharedPreferenceChangeListener listener) {
        preferences.registerOnSharedPreferenceChangeListener(listener);
    }
    
    public synchronized void unregisterOnSharedPreferenceChangeListener(
            OnSharedPreferenceChangeListener listener) {
        preferences.unregisterOnSharedPreferenceChangeListener(listener);
    }
    
    public synchronized String getGroup() {
        return preferences.getString(GENERAL_GROUP, null);
    }
    
    public synchronized void setGroup(String group) {
        putString(GENERAL_GROUP, group);
    }
    
    public synchronized String getUrl() {
        return preferences.getString(GENERAL_URL, null);
    }
    
    public synchronized void setUrl(String url) {
        putString(GENERAL_URL, url);
    }
    
    public synchronized boolean showEmptyLessons() {
        return preferences.getBoolean(APP_EMPTY, true);
    }
    
    public synchronized boolean isNotificationsEnabled() {
        return preferences.getBoolean(NOTIFICATIONS_ENABLED, true);
    }
    
    public synchronized int getNotificationsTime() {
        return preferences.getInt(NOTIFICATIONS_TIME, 4) - 1;
    }
    
    public synchronized int getNotificationsTimeInMinutes() {
        switch (getNotificationsTime()) {
        case 0:
            return 15;
        case 1:
            return 30;
        case 2:
            return 45;
        case 3:
            return 60;
        case 4:
            return 90;
        case 5:
            return 120;
        case 6:
            return 180;
        default:
            return 0;
        }
    }
    
    public synchronized String getNotificationsTimeAsString() {
        int index = getNotificationsTime();
        if (index < 0) {
            index = 0;
            putInt(NOTIFICATIONS_TIME, index);
        } else if (index >= notificationsTimes.length) {
            index = notificationsTimes.length - 1;
            putInt(NOTIFICATIONS_TIME, index);
        }
        return notificationsTimes[index];
    }
    
    public String getNotificationsTimeSummary() {
        String format = context.getString(R.string.activity_settings_notifications_time_summary);
        return String.format(format, getNotificationsTimeAsString());
    }
    
    public synchronized boolean isSilentModeEnabled() {
        return preferences.getBoolean(SILENT_MODE_ENABLED, false);
    }
    
    public synchronized void setPreviousRingerMode(int ringerMode) {
        putInt(PREVOIUS_RINGER_MODE, ringerMode);
    }
    
    public synchronized int getPreviousRingerMode() {
        return preferences.getInt(PREVOIUS_RINGER_MODE, -1);
    }
    
    private synchronized void putString(String key, String value) {
        Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }
    private synchronized void putInt(String key, int value) {
        Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }
}
