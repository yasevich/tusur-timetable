package com.synergy.android.timetable.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.synergy.android.timetable.ApplicationSettings;
import com.synergy.android.timetable.TimeStruct;
import com.synergy.android.timetable.TimetableApplication;
import com.synergy.android.timetable.domains.Day;
import com.synergy.android.timetable.domains.Lesson;
import com.synergy.android.timetable.domains.Week;
import com.synergy.android.timetable.providers.CachedDataProvider;
import com.synergy.android.timetable.services.AlarmNotificationService;
import com.synergy.android.timetable.services.RingerModeService;
import com.synergy.android.timetable.services.TimetableMonitoringService;
import com.synergy.android.timetable.utils.Common;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class ScheduleBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        scheduleTimetableMonitoringService(context);
        scheduleAlarmNotificationService(context);
        scheduleRingerModeService(context);
    }
    
    public static void scheduleTimetableMonitoringService(Context context) {
        Intent intent = new Intent(context, TimetableMonitoringService.class);
        intent.setAction(TimetableApplication.ACTION_MONITOR_TIMETABLE);
        PendingIntent operation = PendingIntent.getService(context, 0,
                new Intent(context, TimetableMonitoringService.class),
                PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(
                Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC,
                System.currentTimeMillis() + AlarmManager.INTERVAL_HOUR,
                AlarmManager.INTERVAL_DAY, operation);
    }
    
    public static void scheduleAlarmNotificationService(Context context) {
        ApplicationSettings settings = ApplicationSettings.getInstance(context);
        if (settings.isNotificationsEnabled()) {
            ScheduleBroadcastReceiver.scheduleAlarmNotification(context);
        } else {
            ScheduleBroadcastReceiver.cancelAlarmNotification(context);
        }
    }
    
    public static void scheduleAlarmNotification(Context context) {
        TimeStruct time = getNextTriggerTime(context,
                - ApplicationSettings.getInstance(context).getNotificationsTimeInMinutes(), true);
        Intent intent = new Intent(context, AlarmNotificationService.class);
        intent.setAction(TimetableApplication.ACTION_ALARM_NOTIFICATION);
        intent.putExtra(TimetableApplication.EXTRA_WEEK, time.week);
        intent.putExtra(TimetableApplication.EXTRA_DAY, time.day);
        
        PendingIntent operation = PendingIntent.getService(context, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(
                Context.ALARM_SERVICE);
        if (time.inMillis != -1L) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, time.inMillis, operation);
        }
    }
    
    public static void cancelAlarmNotification(Context context) {
        cancelScheduledIntent(context, AlarmNotificationService.class,
                TimetableApplication.ACTION_ALARM_NOTIFICATION);
    }
    
    public static void scheduleRingerModeService(Context context) {
        ApplicationSettings settings = ApplicationSettings.getInstance(context);
        if (settings.isSilentModeEnabled()) {
            ScheduleBroadcastReceiver.scheduleRingerMode(context,
                    TimetableApplication.ACTION_RINGER_MODE_SILENT);
        } else {
            ScheduleBroadcastReceiver.cancelRingerMode(context);
        }
    }
    
    public static void scheduleRingerMode(Context context, String action) {
        int timeOffset = 0;
        if (TimetableApplication.ACTION_RESET_RINGER_MODE.equals(action)) {
            timeOffset = TimetableApplication.MINUTES_IN_LESSON;
        }
        
        Intent intent = new Intent(context, RingerModeService.class);
        intent.setAction(action);
        PendingIntent operation = PendingIntent.getService(context, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(
                Context.ALARM_SERVICE);
        TimeStruct time = getNextTriggerTime(context, timeOffset, false);
        if (time.inMillis != -1L) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, time.inMillis, operation);
        }
    }
    
    public static void cancelRingerMode(Context context) {
        cancelScheduledIntent(context, RingerModeService.class,
                TimetableApplication.ACTION_RESET_RINGER_MODE);
        cancelScheduledIntent(context, RingerModeService.class,
                TimetableApplication.ACTION_RINGER_MODE_SILENT);
    }
    
    private static void cancelScheduledIntent(Context context, Class<?> cls, String action) {
        Intent intent = new Intent(context, cls);
        intent.setAction(action);
        PendingIntent operation = PendingIntent.getService(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(
                Context.ALARM_SERVICE);
        alarmManager.cancel(operation);
    }
    
    public static TimeStruct getNextTriggerTime(Context context, int minuteOffset,
            boolean firstLessonOnly) {
        Calendar calendar = Common.getDateAwareCalendar();
        TimeStruct time = new TimeStruct(calendar);
        
        List<Calendar> candidates = prepareTimeCandidates(minuteOffset);
        for (int i = 0; i < candidates.size(); ++i) {
            Calendar candidate = candidates.get(i);
            candidate.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
            candidate.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
            candidate.set(Calendar.DATE, calendar.get(Calendar.DATE));
            candidate.add(Calendar.DATE, time.dayOffset);
            
            if (calendar.compareTo(candidate) == -1) {
                time.lesson = i;
                break;
            }
        }
        
        if (time.lesson == -1) {
            time.nextDay();
        }

        CachedDataProvider provider = CachedDataProvider.getInstance(context);
        Week[] weeks = provider.getWeeks();
        if (weeks == null) {
            return time;
        }
        
        for (int i = 0; i < 12; ++i) {
            Day d = weeks[time.week].days[time.day];
            if (firstLessonOnly) {
                int firstLessonIndex = d.getFirstLessonIndex();
                if (firstLessonIndex == -1 || time.lesson > firstLessonIndex) {
                    time.nextDay();
                } else {
                    time.lesson = firstLessonIndex;
                    break;
                }
            } else {
                if (d.getFirstLessonIndex() == -1) {
                    time.nextDay();
                } else {
                    int firstLessonIndex = d.getFirstLessonIndex();
                    if (time.lesson <= firstLessonIndex) {
                        time.lesson = firstLessonIndex;
                        break;
                    }
                    int lastLessonIndex = d.getLastLessonIndex();
                    if (time.lesson > lastLessonIndex) {
                        time.nextDay();
                        continue;
                    }
                    for (int j = time.lesson; j <= lastLessonIndex; ++j) {
                        Lesson l = d.lessons[j];
                        if (l.subject != null && l.enabled) {
                            time.lesson = j;
                            break;
                        }
                    }
                    break;
                }
            }
        }
        
        if (time.lesson == -1) {
            return time;
        }
        
        Calendar result = candidates.get(time.lesson);
        result.add(Calendar.DATE, time.dayOffset);
        time.inMillis = result.getTimeInMillis();
        return time;
    }
    
    private static List<Calendar> prepareTimeCandidates(int minuteOffset) {
        List<Calendar> times = initCalendars();
        for (Calendar c : times) {
            c.add(Calendar.MINUTE, minuteOffset);
        }
        return times;
    }
    
    private static List<Calendar> initCalendars() {
        List<Calendar> times = new ArrayList<Calendar>();
        for (int i = 0; i < 7; ++i) {
            times.add(new GregorianCalendar());
        }
        times.get(0).set(Calendar.HOUR_OF_DAY, 8);
        times.get(0).set(Calendar.MINUTE, 50);
        times.get(0).set(Calendar.SECOND, 0);
        times.get(0).set(Calendar.MILLISECOND, 0);
        times.get(1).set(Calendar.HOUR_OF_DAY, 10);
        times.get(1).set(Calendar.MINUTE, 40);
        times.get(1).set(Calendar.SECOND, 0);
        times.get(1).set(Calendar.MILLISECOND, 0);
        times.get(2).set(Calendar.HOUR_OF_DAY, 13);
        times.get(2).set(Calendar.MINUTE, 15);
        times.get(2).set(Calendar.SECOND, 0);
        times.get(2).set(Calendar.MILLISECOND, 0);
        times.get(3).set(Calendar.HOUR_OF_DAY, 15);
        times.get(3).set(Calendar.MINUTE, 0);
        times.get(3).set(Calendar.SECOND, 0);
        times.get(3).set(Calendar.MILLISECOND, 0);
        times.get(4).set(Calendar.HOUR_OF_DAY, 16);
        times.get(4).set(Calendar.MINUTE, 45);
        times.get(4).set(Calendar.SECOND, 0);
        times.get(4).set(Calendar.MILLISECOND, 0);
        times.get(5).set(Calendar.HOUR_OF_DAY, 18);
        times.get(5).set(Calendar.MINUTE, 30);
        times.get(5).set(Calendar.SECOND, 0);
        times.get(5).set(Calendar.MILLISECOND, 0);
        times.get(6).set(Calendar.HOUR_OF_DAY, 20);
        times.get(6).set(Calendar.MINUTE, 15);
        times.get(6).set(Calendar.SECOND, 0);
        times.get(6).set(Calendar.MILLISECOND, 0);
        return times;
    }
}
