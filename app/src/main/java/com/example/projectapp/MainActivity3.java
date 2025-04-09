package com.example.projectapp;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity3 extends AppCompatActivity {

    // UI elements - TextViews, Buttons, ProgressBar
    private TextView timerTextView, sessionLabel, progressLabel;
    private Button addFiveButton, subtractFiveButton, startStopButton;
    private ProgressBar circularProgressBar;

    // Timer and session tracking
    private CountDownTimer countDownTimer;
    private boolean isRunning = false;  // Determines if the timer is running
    private boolean isWorkSession = true;  // Indicates if the current session is work or break

    // Duration for focus time and break time (in milliseconds)
    private long focusTimeDuration = 25 * 60 * 1000;  // Default focus time: 25 minutes
    private long breakTimeDuration = 5 * 60 * 1000;   // Default break time: 5 minutes

    // Track the time left for the current session
    private long timeLeftInMillis = focusTimeDuration;  // Start with focus time

    private final long SHORT_BREAK_DURATION = 5 * 60 * 1000; // Duration for a short break (5 minutes)
    private final long LONG_BREAK_DURATION = 15 * 60 * 1000; // Duration for a long break (15 minutes)

    // Sound and vibration for session completion
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    // Session tracking variables
    private int totalWorkSessions = 4; // Total number of work sessions in the Pomodoro cycle before a long break
    private int currentSessionIndex = 0; // Tracks the current session index (used for session switching)

    ImageButton btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main3);

        btn_back = findViewById(R.id.btnBack);


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity3.this, MainActivity2.class);
                startActivity(i);
            }
        });

        // Binding UI elements to the views in the layout file
        timerTextView = findViewById(R.id.timerTextView);
        sessionLabel = findViewById(R.id.sessionLabel);
        addFiveButton = findViewById(R.id.addFiveButton);
        subtractFiveButton = findViewById(R.id.subtractFiveButton);
        startStopButton = findViewById(R.id.startStopButton);
        progressLabel = findViewById(R.id.progressLabel);
        circularProgressBar = findViewById(R.id.circularProgressBar);

        circularProgressBar.setProgress(0); // Initialize progress bar at 0%

        // Set up sound and vibration for the alarm
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm);

        // Initial UI setup to display the timer and session labels
        updateTimerText();
        updateSessionLabel();
        updateProgressLabel();
        updateProgressBar();  // Update the progress bar at startup

        // Button listeners for adding or subtracting time
        addFiveButton.setOnClickListener(v -> {
            // Add 5 minutes to the current session duration
            if (isWorkSession) {
                focusTimeDuration += 5 * 60 * 1000;
                timeLeftInMillis = focusTimeDuration; // Set the new focus time duration
            } else {
                breakTimeDuration += 5 * 60 * 1000;
                timeLeftInMillis = breakTimeDuration; // Set the new break time duration
            }
            updateTimerText();  // Update the timer display
            updateProgressLabel();  // Update progress label
        });

        subtractFiveButton.setOnClickListener(v -> {
            // Subtract 5 minutes from the current session duration (but ensure it doesn't go below 1 minute)
            if (isWorkSession) {
                if (focusTimeDuration > 5 * 60 * 1000) {
                    focusTimeDuration -= 5 * 60 * 1000;
                    timeLeftInMillis = focusTimeDuration; // Set the new focus time duration
                }
            } else {
                if (breakTimeDuration > 5 * 60 * 1000) {
                    breakTimeDuration -= 5 * 60 * 1000;
                    timeLeftInMillis = breakTimeDuration; // Set the new break time duration
                }
            }
            updateTimerText();  // Update the timer display
            updateProgressLabel();  // Update progress label
        });

        // Start/Stop button listener to toggle timer state
        startStopButton.setOnClickListener(v -> {
            if (isRunning) {
                stopTimer();  // Stop the timer if it's currently running
            } else {
                startTimer();  // Start the timer if it's not running
            }
        });
    }

    // Start the countdown timer
    private void startTimer() {
        // Disable the time adjustment buttons while the timer is running
        addFiveButton.setEnabled(false);
        subtractFiveButton.setEnabled(false);

        // Reset the progress bar when the timer starts
        circularProgressBar.setProgress(0);

        // Initialize and start the countdown timer
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;  // Update the remaining time
                updateTimerText();  // Update the timer text every second
                updateProgressBar();  // Update the progress bar only when the timer is running
            }

            @Override
            public void onFinish() {
                isRunning = false;
                startStopButton.setText("Start");  // Change button text to "Start" when the timer finishes
                showAlarmDialog();  // Show alarm dialog when the session finishes

                // Switch session type (work/break)
                if (isWorkSession) {
                    isWorkSession = false;  // Switch to break
                    timeLeftInMillis = (currentSessionIndex % totalWorkSessions == 0) ? LONG_BREAK_DURATION : breakTimeDuration;  // Long break after every 4 sessions
                } else {
                    isWorkSession = true;  // Switch to work session
                    timeLeftInMillis = focusTimeDuration;  // Reset work session duration
                }

                updateSessionLabel();  // Update the session label (Work or Break)
                updateTimerText();  // Update the timer display
            }
        }.start();

        isRunning = true;
        startStopButton.setText("Stop");  // Change button text to "Stop"
    }

    // Stop the countdown timer
    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();  // Cancel the countdown timer
        }
        isRunning = false;
        startStopButton.setText("Start");  // Change button text to "Start"

        // Enable the time adjustment buttons when the timer is stopped
        addFiveButton.setEnabled(true);
        subtractFiveButton.setEnabled(true);
    }

    private void showAlarmDialog() {
        if (mediaPlayer != null) mediaPlayer.start();  // Play the alarm sound

        // Vibrate if the device supports vibration
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));  // Vibrate for 1 second
            }
        }

        // Show an alert dialog when the session ends
        new AlertDialog.Builder(this)
                .setTitle("Time's up!")
                .setMessage(isWorkSession ? "Take a short break!" : "Back to work!")  // Message based on session type
                .setCancelable(false)
                .setPositiveButton("Stop Alarm", (dialog, which) -> {
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();  // Stop the alarm sound
                        mediaPlayer.prepareAsync();
                    }
                    dialog.dismiss();  // Dismiss the dialog and move to the next session
                    startNextSession();  // Proceed to the next session immediately after the dialog
                })
                .show();
    }

    // Starts the next session after the current session ends
    private void startNextSession() {
        currentSessionIndex++;

        if (isWorkSession && currentSessionIndex % totalWorkSessions == 0) {
            // Start long break after 4 work sessions
            isWorkSession = false;
            timeLeftInMillis = LONG_BREAK_DURATION;
        } else {
            // Continue with work or break session
            isWorkSession = !isWorkSession;
            timeLeftInMillis = isWorkSession ? focusTimeDuration : breakTimeDuration;
        }

        updateSessionLabel();  // Update session label (Pomodoro or Break)
        updateTimerText();  // Update the timer display
        updateProgressLabel();  // Update progress label
        updateProgressBar();  // Update progress bar
    }

    // Updates the timer display
    private void updateTimerText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        timerTextView.setText(String.format("%02d:%02d", minutes, seconds));  // Format time as MM:SS
    }

    // Updates the session label (e.g., "Pomodoro" or "Break Time")
    private void updateSessionLabel() {
        sessionLabel.setText(isWorkSession ? "Pomodoro" : "Break Time");
    }

    // Updates the progress label (e.g., "Progress: 2/4")
    private void updateProgressLabel() {
        progressLabel.setText("Progress: " + currentSessionIndex + "/" + totalWorkSessions);
    }

    // Updates the circular progress bar
    private void updateProgressBar() {
        if (isRunning) {  // Update progress only when the timer is running
            long timeElapsed = focusTimeDuration - timeLeftInMillis; // Calculate elapsed time
            int progress = (int) ((timeElapsed * 100) / focusTimeDuration);  // Calculate progress percentage

            progress = Math.max(0, Math.min(100, progress));  // Ensure progress is within 0–100%

            circularProgressBar.setProgress(progress);  // Update progress bar
        }
    }

    // Release resources used by the media player when the activity is destroyed
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();  // Release media player resources
        }
    }
}
