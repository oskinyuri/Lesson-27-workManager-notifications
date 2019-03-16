package com.example.lesson_27_workmanager_notifications.alarmList;

import com.example.lesson_27_workmanager_notifications.entity.AlarmEntity;

import java.util.List;

public interface AlarmView {

    void setData(List<AlarmEntity> data);

    void setViewEnabled(boolean state);

}
