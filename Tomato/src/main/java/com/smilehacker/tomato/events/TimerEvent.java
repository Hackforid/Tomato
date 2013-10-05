package com.smilehacker.tomato.events;

/**
 * Created by kleist on 13-9-29.
 */
public class TimerEvent {
    private long mMillisUntilFinished;
    private long mCountDownInterval;
    private long mMillisInFuture;

    public TimerEvent(long millisInFuture, long countDownInterval, long millisUntilFinished) {
        mMillisUntilFinished = millisUntilFinished;
        mCountDownInterval = countDownInterval;
        mMillisInFuture = millisInFuture;
    }

    public long getmCountDownInterval() {
        return mCountDownInterval;
    }

    public long getmMillisInFuture() {
        return mMillisInFuture;
    }

    public long getmMillisUntilFinished() {
        return mMillisUntilFinished;
    }
}
