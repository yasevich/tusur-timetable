package com.synergy.android.timetable;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.media.AudioManager;
import android.preference.PreferenceManager;

import com.synergy.android.timetable.parsers.LessonsParser;

import java.util.Date;

public class ApplicationSettings {
    public static final String APP_EMPTY = "appEmpty";
    public static final String SILENT_MODE_ENABLED = "silentModeEnabled";
    public static final String SILENT_MODE = "silentMode";
    public static final String NOTIFICATIONS_ENABLED = "notificationsEnabled";
    public static final String NOTIFICATIONS_TIME = "notificationsTime";
    
    private static final String GENERAL_GROUP = "generalGroup";
    private static final String GENERAL_URL = "generalUrl";
    private static final String PREVOIUS_RINGER_MODE = "previousRingerMode";
    private static final String LAST_UPDATE = "lastUpdate";
    
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
        // check for 3rd version code or lower
        String url = instance.getUrl();
        if (url != null && url.startsWith("http://timetable.tusur.ru/faculties/")) {
            int index = url.lastIndexOf('/');
            url = String.format(LessonsParser.URL_FORMAT, url.substring(index + 1));
            instance.setUrl(url);
        }
        // check for 8th version code or lower 
        if (instance.getPreviousRingerMode() == -1) {
            instance.setPreviousRingerMode(AudioManager.RINGER_MODE_NORMAL);
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
    
    public synchronized String getNotificationsTimeSummary() {
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
        return preferences.getInt(PREVOIUS_RINGER_MODE, AudioManager.RINGER_MODE_NORMAL);
    }
    
    public synchronized int getSilentMode() {
        String silentMode = preferences.getString(SILENT_MODE, null);
        int result = AudioManager.RINGER_MODE_SILENT;
        if (silentMode != null && silentMode.equals("1")) {
            result = AudioManager.RINGER_MODE_VIBRATE;
        }
        return result;
    }
    
    public synchronized String getSilentModeSummary() {
        return context.getResources().getStringArray(R.array.ringerModes)[getSilentMode()];
    }
    
    public synchronized void setLastUpdateTime(Date date) {
        putLong(LAST_UPDATE, date.getTime());
    }
    
    public synchronized Date getLastUpdateTime() {
        return new Date(preferences.getLong(LAST_UPDATE, 0L));
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
    
    private synchronized void putLong(String key, long value) {
        Editor editor = preferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }
}
