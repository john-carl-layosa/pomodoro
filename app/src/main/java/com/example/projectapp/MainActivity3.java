package com.example.projectapp;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity3 extends AppCompatActivity {

    private TextView timerTextView;
    private Button addFiveButton, subtractFiveButton, startStopButton;
    private ProgressBar circularProgressBar;
    private ImageButton btn_back;

    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    private final long focusTimeDuration = 25 * 60 * 1000;
    private final long breakTimeDuration = 5 * 60 * 1000;
    private final long SHORT_BREAK_DURATION = 5 * 60 * 1000;
    private final long LONG_BREAK_DURATION = 15 * 60 * 1000;

    private int totalWorkSessions = 4;
    private int currentSessionIndex = 0;

    private boolean isRunning = false;
    private long timeLeftInMillis;
    private boolean isWorkSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main3);

        timerTextView = findViewById(R.id.timerTextView);
        addFiveButton = findViewById(R.id.addFiveButton);
        subtractFiveButton = findViewById(R.id.subtractFiveButton);
        startStopButton = findViewById(R.id.startStopButton);
        circularProgressBar = findViewById(R.id.circularProgressBar);
        btn_back = findViewById(R.id.btnBack);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm);

        TimerManager timerManager = TimerManager.getInstance();

        if (timerManager.getTimeLeftInMillis() == 0) {
            timerManager.setInitialValues(focusTimeDuration, true);
            timerManager.setSessionDuration(focusTimeDuration);
        }

        timeLeftInMillis = timerManager.getTimeLeftInMillis();
        isRunning = timerManager.isRunning();
        isWorkSession = timerManager.isWorkSession();

        updateTimerText();
        updateProgressBar();

        btn_back.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity3.this, MainActivity2.class));
        });

        addFiveButton.setOnClickListener(v -> {
            timeLeftInMillis += 5 * 60 * 1000;
            TimerManager.getInstance().setInitialValues(timeLeftInMillis, isWorkSession);
            updateTimerText();
        });

        subtractFiveButton.setOnClickListener(v -> {
            timeLeftInMillis -= 5 * 60 * 1000;
            TimerManager.getInstance().setInitialValues(timeLeftInMillis, isWorkSession);
            updateTimerText();
        });

        startStopButton.setOnClickListener(v -> {
            if (isRunning) {
                stopTimer();
            } else {
                startTimer();
            }
        });

        // Reattach if running
        if (isRunning) {
            startTimer();
        }
    }

    private void startTimer() {
        addFiveButton.setEnabled(false);
        subtractFiveButton.setEnabled(false);

        TimerManager.getInstance().startTimer(1000, new TimerManager.TimerListener() {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
                updateProgressBar();
            }

            @Override
            public void onFinish() {
                isRunning = false;
                startStopButton.setText("Start");
                showAlarmDialog();
                updateTimerText();
            }
        });

        isRunning = true;
        startStopButton.setText("Stop");
    }

    private void stopTimer() {
        TimerManager.getInstance().stopTimer();
        isRunning = false;
        startStopButton.setText("Start");
        addFiveButton.setEnabled(true);
        subtractFiveButton.setEnabled(true);
    }

    private void showAlarmDialog() {
        if (mediaPlayer != null) mediaPlayer.start();

        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
            }
        }

        new AlertDialog.Builder(this)
                .setTitle("Time's up!")
                .setMessage("Take a short break!")
                .setCancelable(false)
                .setPositiveButton("Stop Alarm", (dialog, which) -> {
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        mediaPlayer.prepareAsync();
                    }
                    dialog.dismiss();
                    startNextSession();
                })
                .show();
    }

    private void startNextSession() {
        currentSessionIndex++;

        if (isWorkSession && currentSessionIndex % totalWorkSessions == 0) {
            isWorkSession = false;
            timeLeftInMillis = LONG_BREAK_DURATION;
        } else {
            isWorkSession = !isWorkSession;
            timeLeftInMillis = isWorkSession ? focusTimeDuration : breakTimeDuration;
        }

        TimerManager.getInstance().setInitialValues(timeLeftInMillis, isWorkSession);
        TimerManager.getInstance().setSessionDuration(timeLeftInMillis);

        updateTimerText();
        updateProgressBar();
    }

    private void updateTimerText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        timerTextView.setText(String.format("%02d:%02d", minutes, seconds));
    }

    private void updateProgressBar() {
        if (isRunning) {
            long sessionDuration = TimerManager.getInstance().getSessionDuration();
            long timeElapsed = sessionDuration - timeLeftInMillis;
            int progress = (int) ((timeElapsed * 100) / sessionDuration);
            circularProgressBar.setProgress(Math.max(0, Math.min(100, progress)));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}
