package com.example.lesson_27_workmanager_notifications.entity;

import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class AlarmEntity {

    //TODO почему не работает с private, почему если private требует сетер?

    @NonNull
    @PrimaryKey
    @ColumnInfo (name = "id")
    public String mId;
    @ColumnInfo (name = "hour")
    private int mHour;

    @ColumnInfo (name = "minute")
    private int mMinute;

    @ColumnInfo (name = "active_state")
    private boolean mActive;

    @ColumnInfo (name = "worker_id")
    private String mWorkerID;

    @ColumnInfo (name = "snoozed_state")
    private boolean mSnoozed = false;


    public AlarmEntity(){
        mId = UUID.randomUUID().toString();
    }

    @NonNull
    public String getId() {
        return mId;
    }

    public int getHour() {
        return mHour;
    }

    public void setHour(int hour) {
        mHour = hour;
    }

    public int getMinute() {
        return mMinute;
    }

    public void setMinute(int minute) {
        mMinute = minute;
    }

    public boolean isActive() {
        return mActive;
    }

    /**
     * Если будильник перестает быть активным, он также не может быть отложенным
     * @param active состояние будильника
     */
    public void setActive(boolean active) {
        mActive = active;

        if (!active)
            mSnoozed = false;
    }

    public String getWorkerID() {
        return mWorkerID;
    }

    public void setWorkerID(String workerID) {
        mWorkerID = workerID;
    }

    public boolean isSnoozed() {
        return mSnoozed;
    }

    public void setSnoozed(boolean snoozed) {
        mSnoozed = snoozed;
    }
}
