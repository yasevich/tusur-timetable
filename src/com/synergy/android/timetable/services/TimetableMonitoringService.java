package com.synergy.android.timetable.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.synergy.android.timetable.ApplicationSettings;
import com.synergy.android.timetable.MainActivity;
import com.synergy.android.timetable.R;
import com.synergy.android.timetable.TimetableApplication;
import com.synergy.android.timetable.parsers.TimetableParser;
import com.synergy.android.timetable.plain.Day;
import com.synergy.android.timetable.plain.Lesson;
import com.synergy.android.timetable.plain.Week;
import com.synergy.android.timetable.providers.CachedDataProvider;
import com.synergy.android.timetable.utils.AndroidUtils;
import com.synergy.android.timetable.web.WebPageUtils;

import java.io.IOException;

public class TimetableMonitoringService extends IntentService {
    public TimetableMonitoringService() {
        super(TimetableMonitoringService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ApplicationSettings settings = ApplicationSettings.getInstance(this);
        String url = settings.getUrl();
        if (url != null) {
            monitor(url);
        }
    }
    
    private void monitor(String url) {
        try {
            String pageData = WebPageUtils.readPage(url);
            if (pageData != null) {
                CachedDataProvider provider = CachedDataProvider.getInstance(this);
                Week[] cache = provider.getWeeks();
                Week[] weeks = parsePageData(pageData);
                if (weeks[0].isEmpty && weeks[1].isEmpty) {
                    return;
                }
                boolean isEqual = compareData(cache, weeks);
                if (!isEqual) {
                    provider.insertOrUpdateWeeks(weeks);
                    NotificationCompat.Builder builder = AndroidUtils.buildNotification(this,
                            MainActivity.class,
                            R.drawable.ic_notification, R.string.notification_content_title,
                            R.string.notification_content_text_newtimetable);
                    AndroidUtils.sendNotification(this,
                            TimetableApplication.MONITORING_NOTIFICATION_ID, builder.build());
                }
            }
        } catch (IOException e) {
            // do nothing
        }
    }
    
    private Week[] parsePageData(String pageData) {
        TimetableParser parser = new TimetableParser();
        return parser.parse(pageData);
    }
    
    private static boolean compareData(Week[] cache, Week[] weeks) {
        for (int i = 0; i < cache.length; ++i) {
            Week w1 = cache[i];
            Week w2 = weeks[i];
            for (int j = 0; j < w1.days.length; ++j) {
                Day d1 = w1.days[j];
                Day d2 = w2.days[j];
                for (int k = 0; k < d1.lessons.length; ++k) {
                    Lesson l1 = d1.lessons[k];
                    Lesson l2 = d2.lessons[k];
                    if (!l1.equals(l2)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
