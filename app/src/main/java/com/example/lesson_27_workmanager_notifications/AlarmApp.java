package com.example.lesson_27_workmanager_notifications;

import android.app.Application;

import com.example.lesson_27_workmanager_notifications.repository.AlarmRepository;

public class AlarmApp extends Application {

    private static AlarmApp sAlarmApp;

    public static AlarmApp getInstance(){
        return sAlarmApp;
    }

    private AlarmRepository mAlarmRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        sAlarmApp = AlarmApp.this;
        mAlarmRepository = new AlarmRepository(getApplicationContext());
    }

    public AlarmRepository getAlarmRepository(){
        return mAlarmRepository;
    }
}
