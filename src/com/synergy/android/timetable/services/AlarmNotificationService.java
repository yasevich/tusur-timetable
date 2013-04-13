package com.synergy.android.timetable.services;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.synergy.android.timetable.ApplicationSettings;
import com.synergy.android.timetable.MainActivity;
import com.synergy.android.timetable.R;
import com.synergy.android.timetable.ScheduleBroadcastReceiver;
import com.synergy.android.timetable.TimetableApplication;
import com.synergy.android.timetable.domains.Lesson;
import com.synergy.android.timetable.providers.CachedDataProvider;
import com.synergy.android.timetable.utils.AndroidUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AlarmNotificationService extends IntentService {
    public AlarmNotificationService() {
        super(AlarmNotificationService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        if (action.equals(TimetableApplication.ACTION_ALARM_NOTIFICATION)) {
            sendNotification(intent);
            ScheduleBroadcastReceiver.scheduleAlarmNotificationService(this);
        } else if (action.equals(TimetableApplication.ACTION_DISMISS)) {
            AndroidUtils.dismissNotification(this, TimetableApplication.ALARM_NOTIFICATION_ID);
        } else if (action.equals(TimetableApplication.ACTION_OPEN)) {
            AndroidUtils.dismissNotification(this, TimetableApplication.ALARM_NOTIFICATION_ID);
            MainActivity.startActivity(this, Intent.FLAG_ACTIVITY_NEW_TASK);
        }
    }
    
    private void sendNotification(Intent intent) {
        NotificationCompat.Builder builder = AndroidUtils.buildNotification(this,
                MainActivity.class, R.drawable.ic_notification, R.string.notification_content_title,
                String.format(getString(R.string.notification_content_text_alarm),
                        ApplicationSettings.getInstance(this).getNotificationsTimeAsString()));
        
        int week = intent.getIntExtra(TimetableApplication.EXTRA_WEEK, 0);
        int day = intent.getIntExtra(TimetableApplication.EXTRA_DAY, 0);
        List<String> lessons = getLessons(week, day);
        
        NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
        style.setBigContentTitle(getString(R.string.notification_content_inbox_title_alarm));
        Iterator<String> iterator = lessons.iterator();
        while (iterator.hasNext()) {
            style.addLine(iterator.next());
        }
        
        Intent dismiss = new Intent(this, AlarmNotificationService.class);
        dismiss.setAction(TimetableApplication.ACTION_DISMISS);
        PendingIntent actionDismiss = PendingIntent.getService(this, 0, dismiss, 0);
        
        Intent open = new Intent(this, AlarmNotificationService.class);
        open.setAction(TimetableApplication.ACTION_OPEN);
        PendingIntent actionOpen = PendingIntent.getService(this, 0, open, 0);
        
        builder.setStyle(style)
                .addAction(R.drawable.ic_action_dismiss, getString(R.string.action_dismiss),
                        actionDismiss)
                .addAction(R.drawable.ic_action_open, getString(R.string.action_open),
                        actionOpen);
        
        AndroidUtils.sendNotification(this, TimetableApplication.ALARM_NOTIFICATION_ID,
                builder.build());
    }
    
    private List<String> getLessons(int week, int day) {
        TimetableApplication app = (TimetableApplication) getApplication();
        CachedDataProvider provider = CachedDataProvider.getInstance(this);
        List<String> lessons = new ArrayList<String>();
        for (int i = 0; i < TimetableApplication.NUMBER_OF_LESSONS; ++i) {
            Lesson l = provider.getLesson(week, day, i);
            if (l.subject != null && l.enabled) {
                lessons.add(app.getBeginTimes()[i] + " " + l.subjectShort + " (" +
                        l.kindShort + ")");
            }
        }
        return lessons;
    }
}
