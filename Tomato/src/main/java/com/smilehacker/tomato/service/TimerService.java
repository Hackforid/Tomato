package com.smilehacker.tomato.service;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;

import com.smilehacker.tomato.events.TimerEvent;

import de.greenrobot.event.EventBus;

/**
 * Created by kleist on 13-9-28.
 */
public class TimerService extends Service {

    private TomatoCountDownTimer mCountDownTimer;
    private EventBus eventBus;

    private int mTomatoTime = 1;
    private Boolean isTimerStart = false;

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        eventBus = EventBus.getDefault();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Boolean shouldTimerStart = intent.getBooleanExtra("start", false);
        if (shouldTimerStart) {
            startTimer();
        } else {
            cancelTimer();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelTimer();
    }

    private void startTimer() {
        cancelTimer();
        mCountDownTimer = new TomatoCountDownTimer(mTomatoTime * 60 * 1000, 1000);
        mCountDownTimer.start();
    }

    private void cancelTimer() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();

        }
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
            //Log.i(TAG, "minute:" + millisUntilFinished / 1000);
            postTime(millisUntilFinished);
        }

        @Override
        public void onFinish() {
            postTime(0);
        }

        public void postTime(long millisUntilFinished) {
            TimerEvent event = new TimerEvent(mMillisInFuture, mCountDownInterval, millisUntilFinished);
            eventBus.post(event);
        }

    }
}
