package com.example.lesson_27_workmanager_notifications;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

import com.example.lesson_27_workmanager_notifications.entity.AlarmEntity;
import com.example.lesson_27_workmanager_notifications.repository.AlarmRepository;
import com.example.lesson_27_workmanager_notifications.utils.NotificationUtils;
import com.example.lesson_27_workmanager_notifications.workManager.AlarmWorkerManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class AlarmForeGroundService extends IntentService {

    private static final String TAG_FOREGROUND_SERVICE = "FOREGROUND_SERVICE";

    public static final String EXTRA_WORKER_ID = "EXTRA_WORKER_ID";

    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";

    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";

    public static final String ACTION_SNOOZE = "ACTION_SNOOZE";

    public static final String ACTION_TURN_OFF = "ACTION_TURN_OFF";

    private long[] mVibrationPattern = new long[]{0, 1000, 500};

    private AlarmRepository mAlarmRepository;
    private AlarmEntity mAlarmEntity;
    private AlarmWorkerManager mWorkerManager;
    private ExecutorService mExecutorService;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public AlarmForeGroundService(String name) {
        super(name);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent != null)
        {
            String action = intent.getAction();



            switch (action)
            {
                case ACTION_START_FOREGROUND_SERVICE:
                    startForegroundService();

                    String workerId = intent.getStringExtra(EXTRA_WORKER_ID);
                    getAlarm(workerId);

                    startRingtone();
                    break;
                case ACTION_SNOOZE:
                    stopRingtone();
                    createSnoozeWorker();
                    stopForegroundService();
                    break;
                case ACTION_TURN_OFF:
                    stopRingtone();
                    stopForegroundService();
                    break;
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAlarmRepository = AlarmApp.getInstance().getAlarmRepository();
        mWorkerManager = new AlarmWorkerManager();
        mExecutorService = Executors.newCachedThreadPool();
        Log.d(TAG_FOREGROUND_SERVICE, "My foreground service onCreate().");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null)
        {
            String action = intent.getAction();



            switch (action)
            {
                case ACTION_START_FOREGROUND_SERVICE:
                    startForegroundService();

                    String workerId = intent.getStringExtra(EXTRA_WORKER_ID);
                    getAlarm(workerId);

                    startRingtone();
                    break;
                case ACTION_SNOOZE:
                    stopRingtone();
                    createSnoozeWorker();
                    stopForegroundService();
                    break;
                case ACTION_TURN_OFF:
                    stopRingtone();
                    stopForegroundService();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void createSnoozeWorker() {
        mAlarmEntity.setWorkerID(mWorkerManager.createSnoozeWorker());
        mAlarmRepository.addAlarm(mAlarmEntity);
    }

    private void stopRingtone() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator.hasVibrator())
            vibrator.cancel();
    }

    private void startRingtone() {

        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator.hasVibrator())
                vibrator.vibrate(mVibrationPattern, 1);
    }

    private void getAlarm(String workerId){
        mExecutorService.execute(() -> {
            if (workerId != null){
                mAlarmEntity = mAlarmRepository.getAlarmViaWorkerID(workerId);
            }
        });
    }

    /* Used to build and start foreground service. */
    private void startForegroundService()
    {
        Log.d(TAG_FOREGROUND_SERVICE, "Start foreground service.");

        // Create notification default intent.
        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // Create notification builder.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NotificationUtils.ALARM_CHANNEL_ID);

        builder.setWhen(System.currentTimeMillis());
        builder.setSmallIcon(R.drawable.ic_alarm_black_24dp);

        // Make the notification max priority.
        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        // Make head-up notification.
        builder.setFullScreenIntent(pendingIntent, true);

        // Add Snooze button intent in notification.
        Intent snoozeIntent = new Intent(this, AlarmForeGroundService.class);
        snoozeIntent.setAction(ACTION_SNOOZE);
        PendingIntent pendingPlayIntent = PendingIntent.getService(this, 0, snoozeIntent, 0);
        NotificationCompat.Action snoozeAction = new NotificationCompat.Action(R.drawable.ic_alarm_add_black_24dp, "Snooze", pendingPlayIntent);
        builder.addAction(snoozeAction);

        // Add Turn off button intent in notification.
        Intent turnoffIntent = new Intent(this, AlarmForeGroundService.class);
        turnoffIntent.setAction(ACTION_TURN_OFF);
        PendingIntent pendingPrevIntent = PendingIntent.getService(this, 0, turnoffIntent, 0);
        NotificationCompat.Action turnoffAction = new NotificationCompat.Action(R.drawable.ic_alarm_off_black_24dp, "Turn off", pendingPrevIntent);
        builder.addAction(turnoffAction);

        // Build the notification.
        Notification notification = builder.build();

        // Start foreground service.
        startForeground(1, notification);
    }

    private void stopForegroundService()
    {
        Log.d(TAG_FOREGROUND_SERVICE, "Stop foreground service.");

        // Stop foreground service and remove the notification.
        stopForeground(true);

        // Stop the foreground service.
        stopSelf();
    }


}
