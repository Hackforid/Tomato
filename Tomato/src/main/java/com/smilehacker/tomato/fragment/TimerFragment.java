package com.smilehacker.tomato.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
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
    private ServiceConnection mServiceConnection;
    private TimerService mService;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();

        mEventBus = EventBus.getDefault();
        mEventBus.register(this, TimerEvent.class);

        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                TimerService.TimerBinder mBinder = (TimerService.TimerBinder) service;
                mService = mBinder.getService();
                mService.startTimer();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }
        };
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
                clickTimer();
            }
        });
    }

    private void clickTimer() {
        int status = getTimerStatus();

        switch (status) {
            case TimerService.STATUS_INIT:
            case TimerService.STATUS_PAUSE_AFTER_WORK: {
                startTimer();
                break;
            }

            case TimerService.STATUS_WORK:
            case TimerService.STATUS_REST: {
                cancalTimer();
                break;
            }

            default:
                break;
        }
    }

    private void startTimer() {
        int status = getTimerStatus();
        if (status == TimerService.STATUS_INIT) {
            Intent intent = new Intent(mContext, TimerService.class);
            mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        } else if (status == TimerService.STATUS_PAUSE_AFTER_WORK) {
            mService.startTimer();
        }
    }

    private void cancalTimer() {
        if (mService != null) {
            mService.cancelTimer();
            mContext.unbindService(mServiceConnection);
        }
    }

    private int getTimerStatus() {
        if (mService != null) {
            return mService.getTimerStatus();
        } else {
            return TimerService.STATUS_INIT;
        }
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
        //Log.i(TAG, "total:" + millisInFuture + " util:" + millisUntilFinished);
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

