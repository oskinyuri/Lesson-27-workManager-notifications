package com.example.lesson_27_workmanager_notifications.alarmList;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.text.format.DateFormat;
import android.widget.AdapterView;
import android.widget.TimePicker;

import com.example.lesson_27_workmanager_notifications.dataSource.AlarmsDataSource;
import com.example.lesson_27_workmanager_notifications.entity.AlarmEntity;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AlarmPresenter {

    private AlarmsDataSource mDataSource;

    private Context mContext;
    private AlarmView mView;
    private List<AlarmEntity> mData;

    private TimePickerDialog.OnTimeSetListener mAddAlarmListener;
    private TimePickerDialog.OnTimeSetListener mChangeAlarmListener;

    private ExecutorService mExecutorService;

    private Handler mHandler;

    private AlarmEntity mCurrentAllarm;

    public AlarmPresenter(Context context) {
        mContext = context;
        mHandler = new Handler(Looper.getMainLooper());
        mExecutorService = Executors.newCachedThreadPool();
        mDataSource = new AlarmsDataSource(mContext.getApplicationContext());
        initListener();
    }

    public void onAttach(AlarmView view) {
        mView = view;

        if (mView == null)
            return;

        updateData();
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
                mDataSource.addAlarm(alarmEntity);
                updateData();
            });

            //TODO изменить данные
            //TODO create worker
            //TODO обратиться к базе данных за новыми данными
        };

        mChangeAlarmListener = (view, hourOfDay, minute) -> {
            //TODO изменить данные
            //TODO изменить воркер
            //TODO обратиться к базе данных за новыми данными


            mCurrentAllarm.setHour(hourOfDay);
            mCurrentAllarm.setMinute(minute);
            mCurrentAllarm.setActive(true);

            mExecutorService.execute(() -> {
                mDataSource.addAlarm(mCurrentAllarm);
                updateData();
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

    public void changeAlarm(int position){
        mCurrentAllarm = mData.get(position);
        getTimePicker(mCurrentAllarm.getHour(), mCurrentAllarm.getMinute(), mChangeAlarmListener)
                .show();
    }

    public void removeAlarm(int position) {
        mExecutorService.execute(() -> {
            mDataSource.deleteAlarm(mData.get(position));
            updateData();
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

    private void updateData(){
        mExecutorService.execute(() -> {
            mData = mDataSource.getAlarms();
            sortData();
            mHandler.post(this::setData);
        });
    }

    /**
     * Сортировка будильников по времени
     */
    private void sortData() {
        Collections.sort(mData, (o1, o2) -> {
            if (o1.getHour() < o2.getHour())
                return -1;
            else return Integer.compare(o1.getMinute(), o2.getMinute());
        });
    }

    private void setData(){
        if (mView == null)
            return;
        mView.setData(mData);
        mView.setViewEnabled(true);
    }
}
