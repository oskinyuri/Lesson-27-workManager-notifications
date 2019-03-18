package com.example.lesson_27_workmanager_notifications.alarmList;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.lesson_27_workmanager_notifications.R;
import com.example.lesson_27_workmanager_notifications.entity.AlarmEntity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkManager;

public class AlarmsAdapter extends RecyclerView.Adapter<AlarmsAdapter.AlarmHolder> {

    private List<AlarmEntity> mData;

    //test
    private AlarmHolder.OnHolderClickListener mOnHolderClickListener;

    public AlarmsAdapter(AlarmHolder.OnHolderClickListener holderClickListener) {
        mData = new ArrayList<>();
        mOnHolderClickListener = holderClickListener;
    }

    public void setData(List<AlarmEntity> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AlarmHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alarm, parent, false);
        AlarmHolder holder = new AlarmHolder(view, mOnHolderClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmHolder holder, int position) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, mData.get(position).getHour());
        calendar.set(Calendar.MINUTE, mData.get(position).getMinute());
        java.text.DateFormat dateFormat = DateFormat.getTimeFormat(holder.itemView.getContext());
        holder.alarmTime.setText(dateFormat.format(calendar.getTime().getTime()));

        Log.v("AlarmApp", "In adapterALARM's worker id: " + mData.get(position).getWorkerID());
        boolean active = false;
        try {
            active = !WorkManager.getInstance().getWorkInfoById(UUID.fromString(mData.get(position).getWorkerID())).isDone();
        } catch (Exception e){
            e.printStackTrace();
        }

        holder.activeSwitch.setChecked(active);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class AlarmHolder extends RecyclerView.ViewHolder {

        public TextView alarmTime;
        public Switch activeSwitch;
        public ImageButton deleteBtn;

        public AlarmHolder(@NonNull View itemView, OnHolderClickListener listener) {
            super(itemView);
            alarmTime = itemView.findViewById(R.id.item_alarm_time_text_view);
            activeSwitch = itemView.findViewById(R.id.item_alarm_active_switch);
            deleteBtn = itemView.findViewById(R.id.item_delete_image_btn);

            deleteBtn.setOnClickListener(v -> listener.onHolderClick(AlarmHolder.this, deleteBtn));
            alarmTime.setOnClickListener(v -> listener.onHolderClick(AlarmHolder.this, alarmTime));
            activeSwitch.setOnClickListener(v -> listener.onHolderClick(AlarmHolder.this, activeSwitch));
        }

        public interface OnHolderClickListener {
            void onHolderClick(AlarmHolder alarmHolder, View view);
        }
    }

}
