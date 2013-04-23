package com.synergy.android.timetable.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.synergy.android.timetable.ApplicationSettings;
import com.synergy.android.timetable.MainActivity;
import com.synergy.android.timetable.R;
import com.synergy.android.timetable.TimetableApplication;
import com.synergy.android.timetable.domains.Day;
import com.synergy.android.timetable.domains.Lesson;
import com.synergy.android.timetable.domains.Week;
import com.synergy.android.timetable.providers.CachedDataProvider;
import com.synergy.android.timetable.providers.WebDataProvider;
import com.synergy.android.timetable.utils.AndroidUtils;
import com.synergy.android.timetable.web.WebPageUtils;

import java.io.IOException;

public class TimetableMonitoringService extends IntentService {
    private boolean isNotificationNeeded = false;
    
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
                CachedDataProvider cacheProvider = CachedDataProvider.getInstance(this);
                WebDataProvider webProvider = new WebDataProvider(this);
                
                Week[] cache = cacheProvider.getWeeks();
                Week[] weeks = webProvider.getWeeks();
                
                boolean isEqual = compareData(cache, weeks);
                if (!isEqual) {
                    cacheProvider.insertOrUpdateWeeks(weeks);
                    TimetableApplication.onDataUpdated(this);
                    if (isNotificationNeeded) {
                        NotificationCompat.Builder builder = AndroidUtils.buildNotification(this,
                                MainActivity.class,
                                R.drawable.ic_notification, R.string.notification_content_title,
                                R.string.notification_content_text_newtimetable);
                        AndroidUtils.sendNotification(this,
                                TimetableApplication.MONITORING_NOTIFICATION_ID, builder.build());
                    }
                }
            }
        } catch (IOException e) {
            // do nothing
        }
    }
    
    private boolean compareData(Week[] cache, Week[] weeks) {
        boolean isEqual = true;
        for (int i = 0; i < cache.length; ++i) {
            Week w1 = cache[i];
            Week w2 = weeks[i];
            for (int j = 0; j < w1.days.length; ++j) {
                Day d1 = w1.days[j];
                Day d2 = w2.days[j];
                boolean daysAreEqual = true;
                for (int k = 0; k < d1.lessons.length; ++k) {
                    Lesson l1 = d1.lessons[k];
                    Lesson l2 = d2.lessons[k];
                    if (l1.equals(l2)) {
                        l2.enabled = l1.enabled;
                    } else {
                        isEqual = false;
                        daysAreEqual = false;
                    }
                }
                if (!daysAreEqual && !d2.isEmpty()) {
                    isNotificationNeeded = true;
                }
            }
        }
        return isEqual;
    }
}
