package com.example.projectapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.media.AudioManager;

public class MusicService extends Service {
    public static boolean isMusicPlaying = false;
    private MediaPlayer mediaPlayer;
    private int[] songResIds = {R.raw.nature_sounds, R.raw.lofi_beats, R.raw.beach_vibes}; // Your song resource IDs
    private int currentSongIndex = 0;  // Default song index (first song)

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Only create a new MediaPlayer if it's not already playing
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, songResIds[currentSongIndex]);
            mediaPlayer.setLooping(true);
        }

        String action = intent.getStringExtra("action");
        int songIndex = intent.getIntExtra("songIndex", currentSongIndex); // Get song index from the intent

        // Handle Play/Pause action
        if ("pause".equals(action)) {
            if (mediaPlayer.isPlaying()) mediaPlayer.pause();
            isMusicPlaying = false;
        } else if ("play".equals(action)) {
            if (!mediaPlayer.isPlaying()) mediaPlayer.start();
            isMusicPlaying = true;
        } else if ("changeSong".equals(action)) {
            changeSong(songIndex);
        }

        return START_STICKY;
    }

    private void changeSong(int newSongIndex) {
        if (currentSongIndex != newSongIndex) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.reset();
            }
            mediaPlayer = MediaPlayer.create(this, songResIds[newSongIndex]);
            mediaPlayer.setLooping(true);
            currentSongIndex = newSongIndex; // Update the current song index
            mediaPlayer.start();
            isMusicPlaying = true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            isMusicPlaying = false;
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
