package com.smilehacker.tomato.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smilehacker.tomato.R;
import com.smilehacker.tomato.events.TimerEvent;
import com.smilehacker.tomato.service.TimerService;
import com.smilehacker.tomato.view.TimeCountView;

import de.greenrobot.event.EventBus;

/**
 * Created by kleist on 13-9-27.
 */
public class TimerFragment extends Fragment {
    private final static String TAG = "TimerFragment";

    private Context mContext;
    private TimeCountView timeCountView;
    private RelativeLayout rlTimer;
    private TextView tvMinute;
    private TextView tvSecond;

    private EventBus mEventBus;

    private Boolean isTimerStart = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
        mEventBus = EventBus.getDefault();
        mEventBus.register(this, TimerEvent.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(com.smilehacker.tomato.R.layout.fragment_timer, container, false);

        timeCountView = (TimeCountView) view.findViewById(R.id.timer_time);
        rlTimer = (RelativeLayout) view.findViewById(R.id.timer_rl);
        tvMinute = (TextView) view.findViewById(R.id.timer_minute);
        tvSecond = (TextView) view.findViewById(R.id.timer_second);
        initView();
        return view;
    }

    private void initView() {
        rlTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isTimerStart) {
                    isTimerStart = true;
                    startTimer();
                } else {
                    isTimerStart = false;
                    cancalTimer();
                }
            }
        });
    }

    private void startTimer() {
        //cancalTimer();
        Intent intent = new Intent(mContext, TimerService.class);
        intent.putExtra("start", true);
        mContext.startService(intent);
    }

    private void cancalTimer() {
        Intent intent = new Intent(mContext, TimerService.class);
        //intent.putExtra("start", false);
        //mContext.startService(intent);
        mContext.stopService(intent);
    }

    public void onEventMainThread(TimerEvent event) {
        showTime(event.getmMillisUntilFinished());
        showTimer(event.getmMillisInFuture(), event.getmMillisUntilFinished());
    }

    private String formateTime(int time) {
        if (time < 10) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("0");
            stringBuffer.append(time);
            return stringBuffer.toString();
        } else {
            return Integer.toString(time);
        }
    }

    private void showTime(long millisUntilFinished) {
        int second = (int) (millisUntilFinished / 1000);
        int minute = 0;

        if (second >= 60) {
            minute = second / 60;
            second = second % 60;
        }

        tvMinute.setText(formateTime(minute));
        tvSecond.setText(formateTime(second));

    }

    private void showTimer(long millisInFuture, long millisUntilFinished) {
        Log.i(TAG, "total:" + millisInFuture + " util:" + millisUntilFinished);
        float degree = 360 - ((float) millisUntilFinished) / millisInFuture * 360;
        timeCountView.setDegree(degree);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mEventBus.unregister(this, TimerEvent.class);
        cancalTimer();
        Log.i(TAG, "on destory");
    }


}

