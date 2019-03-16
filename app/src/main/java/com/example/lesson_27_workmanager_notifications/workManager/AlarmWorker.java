package com.example.lesson_27_workmanager_notifications.workManager;

import android.content.Context;
import android.util.Log;

import com.example.lesson_27_workmanager_notifications.AlarmApp;
import com.example.lesson_27_workmanager_notifications.entity.AlarmEntity;
import com.example.lesson_27_workmanager_notifications.repository.AlarmRepository;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class AlarmWorker extends Worker {
    private Context mContext;
    private AlarmRepository mAlarmRepository;

    public AlarmWorker(@NonNull Context context,
                       @NonNull WorkerParameters params) {
        super(context, params);
        mContext = context;
        mAlarmRepository = AlarmApp.getInstance().getAlarmRepository();
    }

    @Override
    public Result doWork(){
        AlarmEntity alarmEntity = mAlarmRepository.getAlarm(getId().toString());
        alarmEntity.setActive(false);
        alarmEntity.setWorkerID("");
        mAlarmRepository.addAlarm(alarmEntity);

        Log.v("AlarmApp", "ALARM Bip-Bip");
        Log.v("AlarmApp", "ALARM's worker id: " + alarmEntity.getWorkerID());

        return Result.success();
    }

}
