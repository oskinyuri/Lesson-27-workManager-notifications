package com.example.lesson_27_workmanager_notifications.repository;

import android.content.Context;

import com.example.lesson_27_workmanager_notifications.alarmList.UpdateAlarmCallback;
import com.example.lesson_27_workmanager_notifications.entity.AlarmEntity;
import com.example.lesson_27_workmanager_notifications.repository.dataSource.AlarmsDataSource;

import java.util.List;

public class AlarmRepository {

    private Context mContext;
    private AlarmsDataSource mDataSource;

    //Это должно быть не так
    private UpdateAlarmCallback mUpdateCallback;

    public AlarmRepository(Context context){
        mContext = context;
        mDataSource = new AlarmsDataSource(mContext);
    }

    public void setUpdateCallback(UpdateAlarmCallback callback){
        mUpdateCallback = callback;
    }

    public void addAlarm(AlarmEntity alarmEntity) {
        mDataSource.addAlarm(alarmEntity);

        if (mUpdateCallback != null){
            mUpdateCallback.updateData(mDataSource.getAlarms());
        }
    }

    public List<AlarmEntity> getAlarms() {
        return mDataSource.getAlarms();
    }

    public void deleteAlarm(AlarmEntity alarmEntity) {
        mDataSource.deleteAlarm(alarmEntity);

        if (mUpdateCallback != null){
            mUpdateCallback.updateData(mDataSource.getAlarms());
        }
    }

    public AlarmEntity getAlarmViaWorkerID(String id) {
        return mDataSource.getAlarmViaWorkerID(id);
    }



}
