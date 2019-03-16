package com.example.lesson_27_workmanager_notifications.alarmList;

import com.example.lesson_27_workmanager_notifications.entity.AlarmEntity;

import java.util.List;

public interface UpdateAlarmCallback {
    void updateData(List<AlarmEntity> data);
}
