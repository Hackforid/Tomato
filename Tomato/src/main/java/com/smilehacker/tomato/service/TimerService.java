package com.smilehacker.tomato.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import com.smilehacker.tomato.events.TimerEvent;

import de.greenrobot.event.EventBus;

/**
 * Created by kleist on 13-9-28.
 */
public class TimerService extends Service {

    public final static String TAG = "TimerService";

    public final static int STATUS_INIT = 0;
    public final static int STATUS_WORK = 1;
    public final static int STATUS_REST = 2;
    public final static int STATUS_PAUSE_AFTER_WORK = 3;
    public final static int STATUS_PAUSE_AFTER_REST = 4;
    public final static int STATUS_CANCEL = 5;


    private TomatoCountDownTimer mCountDownTimer;
    private EventBus eventBus;
    private TimerBinder mBinder;

    private int mWorkTime = 1;
    private int mRestTime = 1;

    private int mTimerStatus = STATUS_INIT;



    @Override
    public void onCreate() {
        super.onCreate();
        eventBus = EventBus.getDefault();
        mBinder = new TimerBinder();
    }


    @Override
    public void onDestroy() {
        cancelTimer();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public int getTimerStatus() {
        return mTimerStatus;
    }

    public void startTimer() {

        switch (mTimerStatus) {

            case STATUS_INIT: {
                mTimerStatus = STATUS_WORK;
                mCountDownTimer = new TomatoCountDownTimer((long) (0.2 * 60 * 1000), 1000);
                Log.i(TAG, "start work");
                break;
            }

            case STATUS_PAUSE_AFTER_WORK: {
                mTimerStatus = STATUS_REST;
                mCountDownTimer = new TomatoCountDownTimer((long) (0.1 * 60 * 1000), 1000);
                Log.i(TAG, "start rest");
            }

            default:
                return;
        }

        mCountDownTimer.start();

    }

    public void cancelTimer() {
        if (mCountDownTimer != null) {
            mTimerStatus = STATUS_CANCEL;
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
    }

    private void notifyTime() {

    }

    private  class TomatoCountDownTimer extends CountDownTimer {
        public long mMillisInFuture;
        public long mCountDownInterval;

        public TomatoCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            mMillisInFuture = millisInFuture;
            mCountDownInterval = countDownInterval;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            postTime(millisUntilFinished);
        }

        @Override
        public void onFinish() {
            postTime(0);
            if (mTimerStatus == STATUS_WORK) {
                mTimerStatus = STATUS_PAUSE_AFTER_WORK;
            } else if (mTimerStatus == STATUS_REST) {
                mTimerStatus = STATUS_PAUSE_AFTER_REST;
            }
            notify();
        }

        public void postTime(long millisUntilFinished) {
            TimerEvent event = new TimerEvent(mMillisInFuture, mCountDownInterval, millisUntilFinished);
            eventBus.post(event);
        }

    }


    public class TimerBinder extends Binder {

        public TimerService getService() {
            return TimerService.this;
        }
    }
}
