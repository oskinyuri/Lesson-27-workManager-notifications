package com.example.lesson_27_workmanager_notifications.workManager;

import android.util.Log;

import com.example.lesson_27_workmanager_notifications.entity.AlarmEntity;

import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class AlarmWorkerManager {

    /**
     * Создает задачи будильника
     *
     * @param alarmEntity Экземпляр будильника
     * @return String UUID созданной задачи
     */
    public String createAlarmWorker(AlarmEntity alarmEntity){
        long delayTimeInSeconds = getDelayTimeInSeconds(alarmEntity);

        OneTimeWorkRequest alarmRequest = new OneTimeWorkRequest.Builder(AlarmWorker.class)
                .setInitialDelay(delayTimeInSeconds, TimeUnit.SECONDS)
                .build();
        WorkManager.getInstance().enqueue(alarmRequest);

        return alarmRequest.getId().toString();
    }

    public String createSnoozeWorker(){

        //TODO 5 minute
        long delayTimeInSeconds = 1 * 60;

        OneTimeWorkRequest alarmRequest = new OneTimeWorkRequest.Builder(AlarmWorker.class)
                .setInitialDelay(delayTimeInSeconds, TimeUnit.SECONDS)
                .build();
        WorkManager.getInstance().enqueue(alarmRequest);

        return alarmRequest.getId().toString();
    }

    public void deleteWorker(AlarmEntity alarmEntity) {

        if (alarmEntity.getWorkerID().equals(""))
            return;

        WorkManager workManager = WorkManager.getInstance();
        UUID workerUUID = UUID.fromString(alarmEntity.getWorkerID());

        Log.v("AlarmApp", "Delete alarm's worker: Alarm's worker id: " + alarmEntity.getWorkerID());

        if (!workManager.getWorkInfoById(workerUUID).isCancelled()) {
            workManager.cancelWorkById(workerUUID);
        }
    }

    //TODO грязный код, переделать
    private long getDelayTimeInSeconds(AlarmEntity alarmEntity) {
        int delayMinute;
        int delayHour;
        long delayTimeInSeconds;

        if ((alarmEntity.getHour() >= Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
                && (alarmEntity.getMinute() > Calendar.getInstance().get(Calendar.MINUTE))) {

            delayMinute = alarmEntity.getMinute() - Calendar.getInstance().get(Calendar.MINUTE);
            delayHour = alarmEntity.getHour() - Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            delayTimeInSeconds = (delayHour * 60 + delayMinute) * 60 - Calendar.getInstance().get(Calendar.SECOND);

        } else {

            delayMinute = (60 - Calendar.getInstance().get(Calendar.MINUTE)) + alarmEntity.getMinute();
            delayHour = (24 - Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) + alarmEntity.getHour();
            delayTimeInSeconds = (delayHour * 60 + delayMinute) * 60 - Calendar.getInstance().get(Calendar.SECOND);
        }
        return delayTimeInSeconds;
    }

}
