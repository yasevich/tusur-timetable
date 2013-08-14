package com.synergy.android.timetable.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class AndroidUtils {
    public static void sendNotification(Context context, int id, Notification notification) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notification);
    }
    
    public static void dismissNotification(Context context, int id) {
        NotificationManager manager = (NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE);
        manager.cancel(id);
    }
    
    public static NotificationCompat.Builder buildNotification(Context context,
            Class<?> cls, int icon, int title, String text) {
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(icon)
                .setContentTitle(context.getString(title))
                .setContentText(text)
                .setAutoCancel(true)
                .setSound(sound)
                .setTicker(text);
        Intent intent = new Intent(context, cls);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context)
                .addParentStack(cls)
                .addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        return builder;
    }
    
    public static NotificationCompat.Builder buildNotification(Context context,
            Class<?> cls, int icon, int title, int text) {
        return buildNotification(context, cls, icon, title, context.getString(text));
    }
}
