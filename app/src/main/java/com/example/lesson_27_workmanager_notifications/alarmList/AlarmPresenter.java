package com.example.lesson_27_workmanager_notifications.alarmList;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.format.DateFormat;
import android.util.Log;

import com.example.lesson_27_workmanager_notifications.AlarmApp;
import com.example.lesson_27_workmanager_notifications.entity.AlarmEntity;
import com.example.lesson_27_workmanager_notifications.repository.AlarmRepository;
import com.example.lesson_27_workmanager_notifications.workManager.AlarmWorker;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class AlarmPresenter implements UpdateAlarmCallback {

    private AlarmRepository mAlarmRepository;
    private Context mContext;
    private AlarmView mView;
    private List<AlarmEntity> mData;

    private TimePickerDialog.OnTimeSetListener mAddAlarmListener;
    private TimePickerDialog.OnTimeSetListener mChangeAlarmListener;

    private ExecutorService mExecutorService;

    private Handler mHandler;

    private AlarmEntity mCurrentAlarm;

    public AlarmPresenter(Context context) {
        mContext = context;
        mHandler = new Handler(Looper.getMainLooper());
        mExecutorService = Executors.newCachedThreadPool();

        mAlarmRepository = AlarmApp.getInstance().getAlarmRepository();
        mAlarmRepository.setUpdateCallback(this);

        initListener();
    }

    public void onAttach(AlarmView view) {
        mView = view;

        if (mView == null)
            return;

        loadData();
    }

    private void loadData() {
        mExecutorService.execute(() -> {
            mData = mAlarmRepository.getAlarms();
            mHandler.post(this::setData);
        });
    }

    public void onDetach() {
        mView = null;
    }

    private void initListener() {
        mAddAlarmListener = (view, hourOfDay, minute) -> {
            AlarmEntity alarmEntity = new AlarmEntity();
            alarmEntity.setHour(hourOfDay);
            alarmEntity.setMinute(minute);
            alarmEntity.setActive(true);

            mExecutorService.execute(() -> {

                String workerId = createWorker(alarmEntity);
                alarmEntity.setWorkerID(workerId);
                mAlarmRepository.addAlarm(alarmEntity);
                Log.v("AlarmApp", "Create alarm: Alarm id         : " + UUID.fromString(alarmEntity.getId()));
                Log.v("AlarmApp", "Create alarm: Alarm's worker id: " + UUID.fromString(alarmEntity.getWorkerID()));
            });
        };

        mChangeAlarmListener = (view, hourOfDay, minute) -> {
            mCurrentAlarm.setHour(hourOfDay);
            mCurrentAlarm.setMinute(minute);
            mCurrentAlarm.setActive(true);

            mExecutorService.execute(() -> {
                deleteWorker(mCurrentAlarm);
                String workerId = createWorker(mCurrentAlarm);
                mCurrentAlarm.setWorkerID(workerId);
                mAlarmRepository.addAlarm(mCurrentAlarm);
                Log.v("AlarmApp", "Change alarm: Alarm's id       : " + UUID.fromString(mCurrentAlarm.getId()));
                Log.v("AlarmApp", "Change alarm: Alarm's worker id: " + UUID.fromString(mCurrentAlarm.getWorkerID()));
            });
        };
    }

    public void addAlarm() {
        Calendar calendar = Calendar.getInstance();

        if (mView == null)
            return;

        getTimePicker(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), mAddAlarmListener)
                .show();
    }

    public void changeAlarm(int position) {
        mCurrentAlarm = mData.get(position);
        getTimePicker(mCurrentAlarm.getHour(), mCurrentAlarm.getMinute(), mChangeAlarmListener)
                .show();
    }

    public void removeAlarm(int position) {
        mExecutorService.execute(() -> {
            Log.v("AlarmApp", "Delete alarm: Alarm's id: " + mData.get(position).getId());
            deleteWorker(mData.get(position));
            mAlarmRepository.deleteAlarm(mData.get(position));
        });
    }

    public void switchAlarm(int adapterPosition, boolean state) {
        AlarmEntity alarmEntity = mData.get(adapterPosition);
        alarmEntity.setActive(state);

        mExecutorService.execute(() -> {
            mAlarmRepository.addAlarm(alarmEntity);

            if (state) {
                String workerId = createWorker(alarmEntity);
                alarmEntity.setWorkerID(workerId);
                mAlarmRepository.addAlarm(alarmEntity);
            } else {
                deleteWorker(alarmEntity);
                alarmEntity.setWorkerID("");
                mAlarmRepository.addAlarm(alarmEntity);
            }

            Log.v("AlarmApp", "Change alarm's state: " + alarmEntity.isActive());
            Log.v("AlarmApp", "Change alarm's id: " + alarmEntity.getId());
            Log.v("AlarmApp", "Change alarm's worker id: " + alarmEntity.getWorkerID());
        });
    }

    private TimePickerDialog getTimePicker(int hour, int minute, TimePickerDialog.OnTimeSetListener listener) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                mContext,
                listener,
                hour,
                minute,
                DateFormat.is24HourFormat(mContext));
        timePickerDialog.setOnDismissListener(dialog -> mView.setViewEnabled(true));
        return timePickerDialog;
    }

    @Override
    public void updateData(List<AlarmEntity> data) {
        mData = sortData(data);
        mHandler.post(this::setData);
    }

    /**
     * Сортировка будильников по времени
     */
    private List<AlarmEntity> sortData(List<AlarmEntity> data) {
        Collections.sort(data, (o1, o2) -> {
            if (o1.getHour() < o2.getHour())
                return -1;
            else return Integer.compare(o1.getMinute(), o2.getMinute());
        });
        return data;
    }

    private void setData() {
        if (mView == null)
            return;
        mView.setData(mData);
        mView.setViewEnabled(true);
    }

    //TODO грязный код, переделать

    /**
     * Создает задачи будильника
     *
     * @param alarmEntity Экземпляр будильника
     * @return String UUID созданной задачи
     */
    private String createWorker(AlarmEntity alarmEntity) {

        long delayTimeInSeconds = getDelayTimeInSeconds(alarmEntity);

        OneTimeWorkRequest alarmRequest = new OneTimeWorkRequest.Builder(AlarmWorker.class)
                .setInitialDelay(delayTimeInSeconds, TimeUnit.SECONDS)
                .build();
        WorkManager.getInstance().enqueue(alarmRequest);

        return alarmRequest.getId().toString();
    }

    private long getDelayTimeInSeconds(AlarmEntity alarmEntity) {
        int delayMinute;
        int delayHour;
        long delayTimeInSeconds;

        if ((alarmEntity.getHour() >= Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
                && (alarmEntity.getMinute() >= Calendar.getInstance().get(Calendar.MINUTE))) {

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

    private void deleteWorker(AlarmEntity alarmEntity) {

        if (alarmEntity.getWorkerID().equals(""))
            return;

        WorkManager workManager = WorkManager.getInstance();
        UUID workerUUID = UUID.fromString(alarmEntity.getWorkerID());

        Log.v("AlarmApp", "Delete alarm's worker: Alarm's worker id: " + alarmEntity.getWorkerID());

        if (!workManager.getWorkInfoById(workerUUID).isCancelled()) {
            workManager.cancelWorkById(workerUUID);
        }
    }
}
