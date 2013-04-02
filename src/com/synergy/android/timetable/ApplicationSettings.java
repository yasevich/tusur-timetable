package com.synergy.android.timetable;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;

public class ApplicationSettings {
    public static final String APP_EMPTY = "appEmpty";
    public static final String NOTIFICATIONS_TIME = "notificationsTime";
    
    private static final String GENERAL_GROUP = "generalGroup";
    private static final String GENERAL_URL = "generalUrl";
    private static final String NOTIFICATIONS_ENABLED = "notificationsEnabled";
    
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
    
    public synchronized boolean getNotificationsEnabled() {
        return preferences.getBoolean(NOTIFICATIONS_ENABLED, true);
    }
    
    public synchronized int getNotificationsTime() {
        return preferences.getInt(NOTIFICATIONS_TIME, 4) - 1;
    }
    
    public synchronized String getNotificationsTimeAsString() {
        return notificationsTimes[getNotificationsTime()];
    }
    
    public String getNotificationsTimeSummary() {
        String format = context.getString(R.string.activity_settings_notifications_time_summary);
        return String.format(format, getNotificationsTimeAsString());
    }
    
    private synchronized void putString(String key, String value) {
        Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }
}
