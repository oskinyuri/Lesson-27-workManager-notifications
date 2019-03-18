package com.example.lesson_27_workmanager_notifications.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

public class NotificationUtils {

    private Context mContext;
    private NotificationManager mNotificationManager;

    public static final String ALARM_CHANNEL_ID = "com.example.lesson_27.ALARM_NOTIFICATION_ID";
    public static final String ALARM_CHANNEL_NAME = "Alarm";
    public static final String ALARM_CHANNEL_DESCRIPTION = "Alarm's notification";

    public NotificationUtils(Context context) {
        mContext = context;
    }

    public void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = ALARM_CHANNEL_NAME;
            String description = ALARM_CHANNEL_DESCRIPTION;
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(ALARM_CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = mContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }
}
