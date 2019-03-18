package com.example.lesson_27_workmanager_notifications;

import android.app.Application;

import com.example.lesson_27_workmanager_notifications.repository.AlarmRepository;
import com.example.lesson_27_workmanager_notifications.utils.NotificationUtils;

public class AlarmApp extends Application {

    private static AlarmApp sAlarmApp;

    public static AlarmApp getInstance(){
        return sAlarmApp;
    }

    private AlarmRepository mAlarmRepository;
    private NotificationUtils mNotificationUtils;

    @Override
    public void onCreate() {
        super.onCreate();
        sAlarmApp = AlarmApp.this;
        mAlarmRepository = new AlarmRepository(getApplicationContext());
        mNotificationUtils = new NotificationUtils(getApplicationContext());
        mNotificationUtils.createNotificationChannel();
    }

    public AlarmRepository getAlarmRepository(){
        return mAlarmRepository;
    }
}
