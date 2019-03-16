package com.example.lesson_27_workmanager_notifications.dataSource;

import com.example.lesson_27_workmanager_notifications.entity.AlarmEntity;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface AlarmsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addAlarm(AlarmEntity alarm);

    @Query("select * from alarmentity")
    List<AlarmEntity> getAlarms();

    @Delete
    void deleteAlarm(AlarmEntity alarmEntity);
}
