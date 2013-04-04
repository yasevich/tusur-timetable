package com.synergy.android.timetable.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.synergy.android.timetable.ApplicationSettings;
import com.synergy.android.timetable.MainActivity;
import com.synergy.android.timetable.R;
import com.synergy.android.timetable.ScheduleBroadcastReceiver;
import com.synergy.android.timetable.TimetableApplication;
import com.synergy.android.timetable.plain.Lesson;
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
        sendNotification(intent);
        ScheduleBroadcastReceiver.scheduleAlarmNotificationService(this);
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
        builder.setStyle(style);
        
        AndroidUtils.sendNotification(this, TimetableApplication.ALARM_NOTIFICATION_ID,
                builder.build());
    }
    
    private List<String> getLessons(int week, int day) {
        TimetableApplication app = (TimetableApplication) getApplication();
        CachedDataProvider provider = CachedDataProvider.getInstance(this);
        List<String> lessons = new ArrayList<String>();
        for (int i = 0; i < TimetableApplication.NUMBER_OF_LESSONS; ++i) {
            Lesson l = provider.getLesson(week, day, i);
            if (l.subject != null) {
                lessons.add(app.getBeginTimes()[i] + " " + l.subjectShort + " (" +
                        l.kindShort + ")");
            }
        }
        return lessons;
    }
}
