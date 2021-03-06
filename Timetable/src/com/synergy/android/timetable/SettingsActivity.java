package com.synergy.android.timetable;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

import com.synergy.android.timetable.receivers.ScheduleBroadcastReceiver;

public class SettingsActivity extends PreferenceActivity
        implements OnSharedPreferenceChangeListener {
    public static final int REQUEST_CODE = 102;
    public static final int RESULT_EMPTY_CHANGED = 201;
    
    private ApplicationSettings settings;
    private Preference notificationsTime;
    private Preference silentMode;
    
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        
        settings = ApplicationSettings.getInstance(this);
        settings.registerOnSharedPreferenceChangeListener(this);
        
        initViews();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        settings.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (ApplicationSettings.APP_EMPTY.equals(key)) {
            setResult(RESULT_EMPTY_CHANGED);
        } else if (ApplicationSettings.NOTIFICATIONS_ENABLED.equals(key)) {
            ScheduleBroadcastReceiver.scheduleAlarmNotificationService(this);
        } else if (ApplicationSettings.NOTIFICATIONS_TIME.equals(key)) {
            notificationsTime.setSummary(settings.getNotificationsTimeSummary());
            ScheduleBroadcastReceiver.scheduleAlarmNotificationService(this);
        } else if (ApplicationSettings.SILENT_MODE_ENABLED.equals(key)) {
            ScheduleBroadcastReceiver.scheduleRingerModeService(this);
        } else if (ApplicationSettings.SILENT_MODE.equals(key)) {
            silentMode.setSummary(settings.getSilentModeSummary());
        }
    }
    
    static void startActivityForResult(Activity from) {
        if (from == null) {
            throw new NullPointerException("The activity should not be null.");
        }
        
        Intent intent = new Intent(from, SettingsActivity.class);
        from.startActivityForResult(intent, REQUEST_CODE);
    }
    
    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    private void initViews() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        notificationsTime = findPreference(ApplicationSettings.NOTIFICATIONS_TIME);
        notificationsTime.setSummary(settings.getNotificationsTimeSummary());
        silentMode = findPreference(ApplicationSettings.SILENT_MODE);
        silentMode.setSummary(settings.getSilentModeSummary());
    }
}
