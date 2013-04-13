package com.synergy.android.timetable.services;

import android.app.IntentService;
import android.content.Intent;

public class RingerModeService extends IntentService {
    public static final String ACTION_MODE_NORMAL =
            "com.synergy.android.timetable.intent.action.MODE_NORMAL";
    public static final String ACTION_MODE_SILENT =
            "com.synergy.android.timetable.intent.action.MODE_SILENT";
    
    public RingerModeService(String name) {
        super(RingerModeService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        if (action.equals(ACTION_MODE_NORMAL)) {
        } else if (action.equals(ACTION_MODE_SILENT)) {
        }
    }
}
