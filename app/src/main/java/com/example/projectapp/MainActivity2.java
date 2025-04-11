package com.example.projectapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {

    ImageButton btn_back, btn_clock, btn_playPause;
    boolean isPlaying = true;
    int currentSongIndex = 0;
    boolean isFirstPageChange = true; // Prevent song change on initial load

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load saved song index from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MusicPrefs", MODE_PRIVATE);
        currentSongIndex = prefs.getInt("currentSongIndex", 0);

        // Start background music service with correct song
        Intent musicIntent = new Intent(MainActivity2.this, MusicService.class);
        musicIntent.putExtra("songIndex", currentSongIndex);
        startService(musicIntent);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);

        btn_back = findViewById(R.id.btnBack);
        btn_clock = findViewById(R.id.btnclock);
        btn_playPause = findViewById(R.id.btnPlay);

        ViewPager2 musicCarousel = findViewById(R.id.musicCarousel);

        // Song list
        List<Song> songList = new ArrayList<>();
        songList.add(new Song(R.drawable.nature, "Nature Sounds"));
        songList.add(new Song(R.drawable.lofiimg, "Lofi Beats"));
        songList.add(new Song(R.drawable.beach, "Beach Vibes"));

        // Adapter
        MusicAdapter adapter = new MusicAdapter(songList);
        musicCarousel.setAdapter(adapter);

        // Set carousel to saved song position
        musicCarousel.setCurrentItem(currentSongIndex, false);

        // Carousel listener
        musicCarousel.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                // Prevent change on initial load
                if (isFirstPageChange) {
                    isFirstPageChange = false;
                    return;
                }

                currentSongIndex = position;

                // Save selected song
                SharedPreferences prefs = getSharedPreferences("MusicPrefs", MODE_PRIVATE);
                prefs.edit().putInt("currentSongIndex", currentSongIndex).apply();

                // Change song in MusicService
                Intent intent = new Intent(MainActivity2.this, MusicService.class);
                intent.putExtra("action", "changeSong");
                intent.putExtra("songIndex", currentSongIndex);
                startService(intent);
            }
        });

        // Back to MainActivity
        btn_back.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity2.this, MainActivity.class);
            startActivity(i);
        });

        // Go to Timer screen (MainActivity3)
        btn_clock.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity2.this, MainActivity3.class);
            startActivity(i);
        });

        // Play / Pause
        btn_playPause.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity2.this, MusicService.class);
            if (isPlaying) {
                intent.putExtra("action", "pause");
                btn_playPause.setImageResource(R.drawable.p_play);
            } else {
                intent.putExtra("action", "play");
                btn_playPause.setImageResource(R.drawable.p_pause);
            }
            startService(intent);
            isPlaying = !isPlaying;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Sync play/pause icon
        if (MusicService.isMusicPlaying) {
            btn_playPause.setImageResource(R.drawable.p_pause);
            isPlaying = true;
        } else {
            btn_playPause.setImageResource(R.drawable.p_play);
            isPlaying = false;
        }
    }
}
