package com.smilehacker.tomato.events;

/**
 * Created by kleist on 13-10-22.
 */
public class TimerActionEvent {
    public final static int ACTION_START = 1;
    public final static int ACTION_CANCEL = 2;

    public int action;

    public TimerActionEvent(int action) {
        this.action = action;
    }
}
