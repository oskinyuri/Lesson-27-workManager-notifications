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

    public AlarmWorker(@NonNull Context context,
                       @NonNull WorkerParameters params) {
        super(context, params);
        mContext = context;
    }

    @Override
    public Result doWork() {

        Log.v("AlarmApp", "done worker id: " + getId().toString());

        Intent intent = new Intent(mContext, AlarmForeGroundService.class);
        intent.putExtra(AlarmForeGroundService.EXTRA_WORKER_ID, getId().toString());
        intent.setAction(AlarmForeGroundService.ACTION_START_FOREGROUND_SERVICE);
        mContext.startService(intent);


        return Result.success();
    }

}
