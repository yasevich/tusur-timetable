package com.synergy.android.timetable;

import android.app.Application;
import android.content.Intent;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.synergy.android.timetable.domains.Day;
import com.synergy.android.timetable.domains.Lesson;
import com.synergy.android.timetable.domains.Week;
import com.synergy.android.timetable.providers.CachedDataProvider;
import com.synergy.android.timetable.providers.WebDataProvider;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TimetableApplication extends Application {
    public static final String ACTION_DATA_LOADING =
            "com.synergy.android.timetable.intent.action.DATA_LOADING";
    public static final String ACTION_DATA_LOADED =
            "com.synergy.android.timetable.intent.action.DATA_LOADED";
    public static final String ACTION_DISMISS =
            "com.synergy.android.timetable.intent.action.DISMISS";
    public static final String ACTION_OPEN =
            "com.synergy.android.timetable.intent.action.OPEN";
    public static final String ACTION_MONITOR_TIMETABLE =
            "com.synergy.android.timetable.intent.action.MONITOR_TIMETABLE";
    public static final String ACTION_ALARM_NOTIFICATION =
            "com.synergy.android.timetable.intent.action.ALARM_NOTIFICATION";
    
    public static final String EXTRA_WEEK =
            "com.synergy.android.timetable.intent.extra.WEEK";
    public static final String EXTRA_DAY =
            "com.synergy.android.timetable.intent.extra.DAY";
    public static final String EXTRA_WEEK_DAYS =
            "com.synergy.android.timetable.intent.extra.WEEK_DAYS";

    public static final int NUMBER_OF_DAYS = 6;
    public static final int NUMBER_OF_LESSONS = 7;
    
    public static final int WEEK_ODD = 0;
    public static final int WEEK_EVEN = 1;

    public static final int MONITORING_NOTIFICATION_ID = 101;
    public static final int ALARM_NOTIFICATION_ID = 102;
    
    private static TimetableApplication instance;
    
    private ExecutorService backgroundExecutor;
    
    private Calendar timestamp;
    private Week[] weeks;
    private String[] weekDays;
    private String[] beginTimes;
    private String[] endTimes;
    private CachedDataProvider provider;
    
    @Override
    public void onCreate() {
        super.onCreate();
        initResources();
        backgroundExecutor = Executors.newScheduledThreadPool(Runtime.getRuntime()
                .availableProcessors());
        backgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                ScheduleBroadcastReceiver.scheduleTimetableMonitoringService(
                        TimetableApplication.this);
                ScheduleBroadcastReceiver.scheduleAlarmNotificationService(
                        TimetableApplication.this);
            } 
        });
        
        instance = this;
    }
    
    public Calendar getTimestamp() {
        return timestamp;
    }
    
    public Week[] getWeeks() {
        return weeks;
    }
    
    public synchronized Week getWeek(int id) {
        if (weeks == null) return null;
        return weeks[id];
    }
    
    public String[] getWeekDays() {
        return weekDays;
    }
    
    public String[] getBeginTimes() {
        return beginTimes;
    }
    
    public String[] getEndTimes() {
        return endTimes;
    }
    
    public ExecutorService getBackgroundExecutor() {
        return backgroundExecutor;
    }
    
    public void updateLesson(Lesson lesson) {
        provider.updateLesson(lesson);
    }
    
    public synchronized void loadCache() {
        new AsyncTask<Void, Void, Week[]>() {
            @Override
            protected void onPreExecute() {
                sendBroadcast(new Intent(ACTION_DATA_LOADING));
            }
            
            @Override
            protected Week[] doInBackground(Void... params) {
                return provider.getWeeks();
            }
            
            @Override
            protected void onPostExecute(Week[] result) {
                weeks = result;
                sendBroadcast(new Intent(ACTION_DATA_LOADED));
            }
        }.execute();
    }
    
    public synchronized void loadWebData() {
        AsyncTask<WebDataProvider, Void, Week[]> task =
                new AsyncTask<WebDataProvider, Void, Week[]>() {
            @Override
            public void onPreExecute() {
                sendBroadcast(new Intent(ACTION_DATA_LOADING));
            }
            
            @Override
            protected Week[] doInBackground(WebDataProvider... params) {
                return params[0].getWeeks();
            }
            
            @Override
            protected void onPostExecute(Week[] result) {
                if (result == null) {
                    Toast.makeText(TimetableApplication.this, getString(R.string.error_connection),
                            Toast.LENGTH_SHORT).show();
                    sendBroadcast(new Intent(ACTION_DATA_LOADED));
                    return;
                }
                
                if (!compareData(weeks, result)) {
                    weeks = result;
                    backgroundExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            provider.insertOrUpdateWeeks(weeks);
                            ScheduleBroadcastReceiver.scheduleAlarmNotificationService(
                                    TimetableApplication.this);
                        }
                    });
                }
                
                sendBroadcast(new Intent(ACTION_DATA_LOADED));
            }
        };
        task.execute(new WebDataProvider(this));
    }
    
    public static TimetableApplication getInstance() {
        return instance;
    }
    
    public static int getBgColor(String kind) {
        if (kind.startsWith("Лек")) {
            return R.color.class_type_lecture;
        } else if (kind.startsWith("Пра")) {
            return R.color.class_type_practice;
        } else if (kind.startsWith("Лаб")) {
            return R.color.class_type_lab;
        } else if (kind.startsWith("Курсовая")) {
            return R.color.class_type_coursework;
        } else if (kind.startsWith("Курсовое")) {
            return R.color.class_type_courseproject;
        } else if (kind.startsWith("Экз")) {
            return R.color.class_type_finalexam;
        } else if (kind.startsWith("Зач")) {
            return R.color.class_type_passfailexam;
        }
        return -1;
    }
    
    private void initResources() {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        timestamp = new GregorianCalendar();
        weekDays = getResources().getStringArray(R.array.weekDays);
        beginTimes = getResources().getStringArray(R.array.beginTimes);
        endTimes = getResources().getStringArray(R.array.endTimes);
        provider = CachedDataProvider.getInstance(this);
    }
    
    private boolean compareData(Week[] cache, Week[] weeks) {
        boolean isEqual = true;
        for (int i = 0; i < cache.length; ++i) {
            Week w1 = cache[i];
            Week w2 = weeks[i];
            for (int j = 0; j < w1.days.length; ++j) {
                Day d1 = w1.days[j];
                Day d2 = w2.days[j];
                for (int k = 0; k < d1.lessons.length; ++k) {
                    Lesson l1 = d1.lessons[k];
                    Lesson l2 = d2.lessons[k];
                    if (l1.equals(l2)) {
                        l2.enabled = l1.enabled;
                    } else {
                        isEqual = false;
                    }
                }
            }
        }
        return isEqual;
    }
}
