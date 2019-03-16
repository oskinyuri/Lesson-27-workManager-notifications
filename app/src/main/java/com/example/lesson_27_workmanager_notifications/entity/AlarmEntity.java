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

    public void setActive(boolean active) {
        mActive = active;
    }
}
