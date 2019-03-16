package com.example.lesson_27_workmanager_notifications.alarmList;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.lesson_27_workmanager_notifications.R;
import com.example.lesson_27_workmanager_notifications.entity.AlarmEntity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements AlarmView , AlarmsAdapter.AlarmHolder.OnHolderClickListener {

    private FloatingActionButton mFab;
    private TextView mNoAlarmTextView;

    private AlarmPresenter mPresenter;

    //test
    private List<AlarmEntity> mData;
    private AlarmsAdapter mAdapter;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPresenter = new AlarmPresenter(this);

        mNoAlarmTextView = findViewById(R.id.no_alarms_text_view);

        mFab = findViewById(R.id.main_add_fab);
        mFab.setOnClickListener(view -> {
            mPresenter.addAlarm();
            setViewEnabled(false);
        });


        //init Recycler
        mRecyclerView = findViewById(R.id.main_alarms_recycler);
        mAdapter = new AlarmsAdapter(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);

        setViewEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onAttach(this);
        setViewEnabled(true);

    }

    @Override
    protected void onPause() {
        setViewEnabled(false);
        mPresenter.onDetach();
        super.onPause();
    }

    @Override
    public void setData(List<AlarmEntity> data) {
        mData = data;

        if (!data.isEmpty())
            mNoAlarmTextView.setVisibility(View.GONE);
        else
            mNoAlarmTextView.setVisibility(View.VISIBLE);

        mAdapter.setData(mData);
    }

    @Override
    public void setViewEnabled(boolean state) {
        mFab.setEnabled(state);
        mRecyclerView.setEnabled(state);
    }

    @Override
    public void onHolderClick(AlarmsAdapter.AlarmHolder alarmHolder, View view) {
        setViewEnabled(false);
        switch (view.getId()) {
            case R.id.item_delete_image_btn:
                mPresenter.removeAlarm(alarmHolder.getAdapterPosition());
                return;
            case R.id.item_alarm_time_text_view:
                mPresenter.changeAlarm(alarmHolder.getAdapterPosition());
                return;
            case R.id.item_alarm_active_switch:
                mPresenter.switchAlarm(alarmHolder.getAdapterPosition(), alarmHolder.activeSwitch.isChecked());
        }
    }
}

