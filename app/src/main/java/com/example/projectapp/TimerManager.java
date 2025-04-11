package com.example.projectapp;

import android.os.CountDownTimer;

public class TimerManager {
    private static TimerManager instance;

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;
    private boolean isRunning;
    private boolean isWorkSession;
    private long sessionDuration;

    public interface TimerListener {
        void onTick(long millisUntilFinished);
        void onFinish();
    }

    private TimerListener listener;

    private TimerManager() {}

    public static TimerManager getInstance() {
        if (instance == null) {
            instance = new TimerManager();
        }
        return instance;
    }

    public void setInitialValues(long initialTime, boolean workSession) {
        timeLeftInMillis = initialTime;
        isWorkSession = workSession;
        sessionDuration = initialTime;
    }

    public void startTimer(long interval, TimerListener listener) {
        this.listener = listener;
        if (countDownTimer != null) countDownTimer.cancel();

        countDownTimer = new CountDownTimer(timeLeftInMillis, interval) {
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                if (listener != null) listener.onTick(millisUntilFinished);
            }

            public void onFinish() {
                isRunning = false;
                if (listener != null) listener.onFinish();
            }
        }.start();

        isRunning = true;
    }

    public void stopTimer() {
        if (countDownTimer != null) countDownTimer.cancel();
        isRunning = false;
    }

    public long getTimeLeftInMillis() {
        return timeLeftInMillis;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public boolean isWorkSession() {
        return isWorkSession;
    }

    public void setWorkSession(boolean workSession) {
        isWorkSession = workSession;
    }

    public long getSessionDuration() {
        return sessionDuration;
    }

    public void setSessionDuration(long sessionDuration) {
        this.sessionDuration = sessionDuration;
    }
}
