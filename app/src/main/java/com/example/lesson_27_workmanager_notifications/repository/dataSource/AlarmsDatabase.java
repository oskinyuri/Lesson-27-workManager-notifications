package com.example.lesson_27_workmanager_notifications.repository.dataSource;

import com.example.lesson_27_workmanager_notifications.entity.AlarmEntity;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = AlarmEntity.class, version = 1, exportSchema = false)
public abstract class AlarmsDatabase extends RoomDatabase {
    public abstract AlarmsDao getAlarmsDB();
}
