package com.example.projectapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {

    // Declare variables for the buttons and the MediaPlayer instance
    ImageButton btn_back, btn_clock, btn_playPause;
    MediaPlayer mediaPlayer;
    boolean isPlaying = false;  // Track whether music is playing
    int currentSongIndex = 0;   // Track the currently playing song

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge display for a more immersive experience
        EdgeToEdge.enable(this);

        // Set the content view to the corresponding layout resource
        setContentView(R.layout.activity_main2);

        // Initialize the buttons
        btn_back = findViewById(R.id.btnBack);
        btn_clock = findViewById(R.id.btnclock);
        btn_playPause = findViewById(R.id.btnPlay);  // Button for play/pause functionality

        // Initialize ViewPager2 to display the music carousel
        ViewPager2 musicCarousel = findViewById(R.id.musicCarousel);

        // Create a list of songs with dummy data (image and title)
        List<Song> songList = new ArrayList<>();
        songList.add(new Song(R.drawable.nature, "Nature Sounds"));
        songList.add(new Song(R.drawable.lofiimg, "Lofi Beats"));
        songList.add(new Song(R.drawable.beach, "Beach vibes"));

        // Set an onClick listener for the back button, which will navigate to MainActivity
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to MainActivity
                Intent i = new Intent(MainActivity2.this, MainActivity.class);
                startActivity(i);
            }
        });

        // Set an onClick listener for the clock button (could be for a Pomodoro timer activity)
        btn_clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to MainActivity3
                Intent i = new Intent(MainActivity2.this, MainActivity3.class);
                startActivity(i);
            }
        });

        // Create an instance of MusicAdapter and set it to the ViewPager2 to display the songs
        MusicAdapter adapter = new MusicAdapter(songList);
        musicCarousel.setAdapter(adapter);

        // Set an onPageChangeCallback to listen for when the user swipes through the songs
        musicCarousel.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // Update the current song index when a new song is selected
                currentSongIndex = position;
                if (isPlaying) {
                    playMusic(currentSongIndex);  // Resume playing the current song
                }
            }
        });

        // Set an onClick listener for the play/pause button
        btn_playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    pauseMusic();  // Pause the music if it's currently playing
                } else {
                    playMusic(currentSongIndex);  // Play music from the selected song
                }
            }
        });
    }

    // Method to play the selected music
    private void playMusic(int songIndex) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();  // Stop any currently playing music
            mediaPlayer.release();  // Release the current MediaPlayer resources
        }

        // Create a new MediaPlayer instance and set the music file based on the song index
        switch (songIndex) {
            case 0:
                mediaPlayer = MediaPlayer.create(this, R.raw.lofi_beats); // Replace with your raw music file
                break;
            case 1:
                mediaPlayer = MediaPlayer.create(this, R.raw.lofi_beats); // Replace with your raw music file
                break;
            case 2:
                mediaPlayer = MediaPlayer.create(this, R.raw.lofi_beats); // Replace with your raw music file
                break;
        }

        mediaPlayer.setLooping(true);  // Loop the music
        mediaPlayer.start();  // Start playing the music

        // Change the play/pause button icon to "pause" when the music is playing
        isPlaying = true;
        btn_playPause.setImageResource(R.drawable.p_pause);  // Update to your pause icon
    }

    // Method to pause the music
    private void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();  // Pause the music
            isPlaying = false;
            btn_playPause.setImageResource(R.drawable.p_play);  // Update to your play icon
        }
    }

    // Handle app lifecycle to stop and release resources when the activity is paused or destroyed
    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();  // Pause music when the app is minimized
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null && !mediaPlayer.isPlaying() && isPlaying) {
            mediaPlayer.start();  // Resume music when the app comes back to the foreground
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();  // Stop the music when the activity is destroyed
            mediaPlayer.release();  // Release resources
        }
    }
}
