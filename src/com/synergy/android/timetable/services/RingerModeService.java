package com.synergy.android.timetable.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.synergy.android.timetable.ApplicationSettings;
import com.synergy.android.timetable.R;
import com.synergy.android.timetable.TimetableApplication;
import com.synergy.android.timetable.receivers.RingerModeBroadcastReceiver;
import com.synergy.android.timetable.receivers.ScheduleBroadcastReceiver;

public class RingerModeService extends Service {
    private static final String ACTION_RESET_MODE =
            "com.synergy.android.timetable.intent.action.RESET_MODE";
    
    private static BroadcastReceiver receiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action.equals(TimetableApplication.ACTION_RINGER_MODE_SILENT)) {
            showNotification();
            ApplicationSettings settings = ApplicationSettings.getInstance(this);
            AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            settings.setPreviousRingerMode(audioManager.getRingerMode());
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            registerReceiver(this);
            ScheduleBroadcastReceiver.scheduleRingerMode(this,
                    TimetableApplication.ACTION_RESET_RINGER_MODE);
        } else if (action.equals(TimetableApplication.ACTION_RESET_RINGER_MODE)) {
            resetRingerMode(this);
            ScheduleBroadcastReceiver.scheduleRingerMode(this,
                    TimetableApplication.ACTION_RINGER_MODE_SILENT);
            stopSelf();
        } else if (action.equals(ACTION_RESET_MODE)) {
            ScheduleBroadcastReceiver.cancelRingerMode(this);
            ScheduleBroadcastReceiver.scheduleRingerMode(this,
                    TimetableApplication.ACTION_RINGER_MODE_SILENT);
            stopSelf();
        }
        
        return START_STICKY;
    }
    
    public static void resetRingerMode(Context context) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(NOTIFICATION_SERVICE);
        cancelNotification(notificationManager);
        unregisterReceiver(context);
        
        ApplicationSettings settings = ApplicationSettings.getInstance(context);
        AudioManager audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        audioManager.setRingerMode(settings.getPreviousRingerMode());
        settings.setPreviousRingerMode(-1);
    }
    
    private static void cancelNotification(NotificationManager notificationManager) {
        notificationManager.cancel(TimetableApplication.RINGER_MODE_NOTIFICATION_ID);
    }
    
    private void showNotification() {
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        
        Intent intent = new Intent(this, RingerModeService.class);
        intent.setAction(ACTION_RESET_MODE);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_silentmode)
                .setContentTitle(getString(R.string.notification_content_silentmode_title))
                .setContentText(getString(R.string.notification_content_silentmode_text))
                .setContentIntent(pendingIntent);
        
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_NO_CLEAR;
        
        notificationManager.notify(TimetableApplication.RINGER_MODE_NOTIFICATION_ID,
                notification);
    }
    
    private static synchronized void registerReceiver(Context context) {
        if (receiver == null) {
            receiver = new RingerModeBroadcastReceiver();
            IntentFilter filter = new IntentFilter(AudioManager.RINGER_MODE_CHANGED_ACTION);
            context.registerReceiver(receiver, filter);
        }
    }
    
    private static synchronized void unregisterReceiver(Context context) {
        if (receiver != null) {
            context.unregisterReceiver(receiver);
            receiver = null;
        }
    }
}
