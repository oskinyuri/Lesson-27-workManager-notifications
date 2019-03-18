package com.example.lesson_27_workmanager_notifications.workManager;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.lesson_27_workmanager_notifications.AlarmApp;
import com.example.lesson_27_workmanager_notifications.AlarmForeGroundService;
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
    public Result doWork() {


        Intent intent = new Intent(mContext, AlarmForeGroundService.class);
        intent.putExtra(AlarmForeGroundService.EXTRA_WORKER_ID, getId().toString());
        intent.setAction(AlarmForeGroundService.ACTION_START_FOREGROUND_SERVICE);
        mContext.startService(intent);

        //TODO not delete
        /*AlarmEntity alarmEntity = mAlarmRepository.getAlarmViaWorkerID(getId().toString());
        alarmEntity.setActive(false);

        //Может быть не стирать, так как оно же и id уведомления
        alarmEntity.setWorkerID("");
        mAlarmRepository.addAlarm(alarmEntity);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, NotificationUtils.ALARM_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_alarm_black_24dp)
                .setContentTitle("ALARM")
                .setContentText("Bip-Bip")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
        notificationManager.notify(getId().hashCode(), builder.build());*/

        /*Log.v("AlarmApp", "ALARM Bip-Bip");
        Log.v("AlarmApp", "ALARM's worker id: " + alarmEntity.getWorkerID());*/

        Log.v("AlarmApp", "done worker id: " + getId().toString());

        return Result.success();
    }

}
