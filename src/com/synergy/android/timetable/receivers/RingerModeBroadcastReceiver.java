package com.synergy.android.timetable.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

import com.synergy.android.timetable.TimetableApplication;
import com.synergy.android.timetable.services.RingerModeService;

public class RingerModeBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getExtras().getInt(AudioManager.EXTRA_RINGER_MODE) !=
                AudioManager.RINGER_MODE_SILENT) {
            RingerModeService.resetRingerMode(context);
            ScheduleBroadcastReceiver.cancelRingerMode(context);
            ScheduleBroadcastReceiver.scheduleRingerMode(context,
                    TimetableApplication.ACTION_RINGER_MODE_SILENT);
        }
    }
}
