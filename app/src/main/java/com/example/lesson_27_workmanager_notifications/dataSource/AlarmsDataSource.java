package com.example.lesson_27_workmanager_notifications.dataSource;

import android.content.Context;

import com.example.lesson_27_workmanager_notifications.entity.AlarmEntity;

import java.util.List;

import androidx.room.Room;

public class AlarmsDataSource {

    private static String ALARMS_DATABASE = "AlarmsDatabase";

    private AlarmsDatabase mDatabase;

    public AlarmsDataSource(Context context){
        mDatabase = Room.databaseBuilder(context, AlarmsDatabase.class, ALARMS_DATABASE)
                .fallbackToDestructiveMigration()
                .build();
    }

    public void addAlarm(AlarmEntity alarmEntity){
        mDatabase.getAlarmsDB().addAlarm(alarmEntity);
    }

    public List<AlarmEntity> getAlarms(){
        return mDatabase.getAlarmsDB().getAlarms();
    }

    public void deleteAlarm(AlarmEntity alarmEntity){
        mDatabase.getAlarmsDB().deleteAlarm(alarmEntity);
    }
}
