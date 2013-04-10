package com.synergy.android.timetable;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.synergy.android.timetable.domains.Day;
import com.synergy.android.timetable.domains.Week;
import com.synergy.android.timetable.providers.CachedDataProvider;
import com.synergy.android.timetable.services.AlarmNotificationService;
import com.synergy.android.timetable.services.TimetableMonitoringService;
import com.synergy.android.timetable.utils.NumberUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class ScheduleBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        scheduleTimetableMonitoringService(context);
        scheduleAlarmNotificationService(context);
    }
    
    public static void scheduleTimetableMonitoringService(Context context) {
        PendingIntent operation = PendingIntent.getService(context, 0,
                new Intent(context, TimetableMonitoringService.class),
                PendingIntent.FLAG_CANCEL_CURRENT);
        if (operation != null) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(
                    Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC,
                    System.currentTimeMillis() + AlarmManager.INTERVAL_HOUR,
                    AlarmManager.INTERVAL_DAY, operation);
        }
    }
    
    public static void scheduleAlarmNotificationService(Context context) {
        TimeStruct time = getNextTriggerMillis(context);
        Intent intent = new Intent(context, AlarmNotificationService.class);
        intent.putExtra(TimetableApplication.EXTRA_WEEK, time.week);
        intent.putExtra(TimetableApplication.EXTRA_DAY, time.day);
        
        PendingIntent operation = PendingIntent.getService(context, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        
        if (operation != null) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(
                    Context.ALARM_SERVICE);
            ApplicationSettings settings = ApplicationSettings.getInstance(context);
            if (settings.getNotificationsEnabled() && time.inMillis != -1L) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, time.inMillis, operation);
            } else {
                alarmManager.cancel(operation);
            }
        }
    }
    
    public static TimeStruct getNextTriggerMillis(Context context) {
        TimeStruct time = new TimeStruct();
        Calendar calendar = new GregorianCalendar();
        time.week = NumberUtils.isOdd(calendar.get(Calendar.WEEK_OF_YEAR)) ?
                TimetableApplication.WEEK_ODD : TimetableApplication.WEEK_EVEN;
        time.day = calendar.get(Calendar.DAY_OF_WEEK);
        int dayOffset = 0;
        if (time.day == Calendar.SUNDAY) {
            time.day = 0;
            dayOffset++;
            time.week ^= 1;
        } else {
            time.day -= 2;
        }
        
        int lesson = -1;
        List<Calendar> candidates = prepareTimeCandidates(context, calendar);
        for (int i = 0; i < candidates.size(); ++i) {
            Calendar candidate = candidates.get(i);
            candidate.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
            candidate.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
            candidate.set(Calendar.DATE, calendar.get(Calendar.DATE));
            candidate.add(Calendar.DATE, dayOffset);
            
            if (calendar.compareTo(candidate) == -1) {
                lesson = i;
                break;
            }
        }
        
        if (lesson == -1) {
            time.day++;
            dayOffset++;
            if (time.day == 6) {
                time.day = 0;
                dayOffset++;
                time.week ^= 1;
            }
        }

        CachedDataProvider provider = CachedDataProvider.getInstance(context);
        Week[] weeks = provider.getWeeks();
        if (weeks == null) {
            return time;
        }
        
        for (int i = 0; i < 12; ++i) {
            Day d = weeks[time.week].days[time.day];
            if (d.getFirstLessonIndex() == -1 || lesson > d.getFirstLessonIndex()) {
                lesson = -1;
                time.day++;
                dayOffset++;
                if (time.day == 6) {
                    time.day = 0;
                    dayOffset++;
                    time.week ^= 1;
                }
            } else {
                lesson = d.getFirstLessonIndex();
                break;
            }
        }
        
        if (lesson == -1) {
            return time;
        }
        
        Calendar result = candidates.get(lesson);
        result.add(Calendar.DATE, dayOffset);
        time.inMillis = result.getTimeInMillis();
        return time;
    }
    
    private static List<Calendar> prepareTimeCandidates(Context context, Calendar c) {
        List<Calendar> times = initCalendars();
        ApplicationSettings settings = ApplicationSettings.getInstance(context);
        switch (settings.getNotificationsTime()) {
        case 0:
            batchCalendarAdd(times, Calendar.MINUTE, -15);
            break;
        case 1:
            batchCalendarAdd(times, Calendar.MINUTE, -30);
            break;
        case 2:
            batchCalendarAdd(times, Calendar.MINUTE, -45);
            break;
        case 3:
            batchCalendarAdd(times, Calendar.HOUR_OF_DAY, -1);
            break;
        case 4:
            batchCalendarAdd(times, Calendar.MINUTE, -90);
            break;
        case 5:
            batchCalendarAdd(times, Calendar.HOUR_OF_DAY, -2);
            break;
        case 6:
            batchCalendarAdd(times, Calendar.HOUR_OF_DAY, -3);
            break;
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
        times.get(1).set(Calendar.MINUTE, 40);;
        times.get(1).set(Calendar.SECOND, 0);
        times.get(1).set(Calendar.MILLISECOND, 0);
        times.get(2).set(Calendar.HOUR_OF_DAY, 13);
        times.get(2).set(Calendar.MINUTE, 15);;
        times.get(2).set(Calendar.SECOND, 0);
        times.get(2).set(Calendar.MILLISECOND, 0);
        times.get(3).set(Calendar.HOUR_OF_DAY, 15);
        times.get(3).set(Calendar.MINUTE, 0);;
        times.get(3).set(Calendar.SECOND, 0);
        times.get(3).set(Calendar.MILLISECOND, 0);
        times.get(4).set(Calendar.HOUR_OF_DAY, 16);
        times.get(4).set(Calendar.MINUTE, 45);;
        times.get(4).set(Calendar.SECOND, 0);
        times.get(4).set(Calendar.MILLISECOND, 0);
        times.get(5).set(Calendar.HOUR_OF_DAY, 18);
        times.get(5).set(Calendar.MINUTE, 30);;
        times.get(5).set(Calendar.SECOND, 0);
        times.get(5).set(Calendar.MILLISECOND, 0);
        times.get(6).set(Calendar.HOUR_OF_DAY, 20);
        times.get(6).set(Calendar.MINUTE, 15);;
        times.get(6).set(Calendar.SECOND, 0);
        times.get(6).set(Calendar.MILLISECOND, 0);
        return times;
    }
    
    private static void batchCalendarAdd(List<Calendar> list, int field, int value) {
        for (Calendar calendar : list) {
            calendar.add(field, value);
        }
    }
    
    private static class TimeStruct {
        public int week;
        public int day;
        public long inMillis = -1L;
    }
}
