package com.example.projectapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {

    ImageButton btn_back, btn_clock, btn_playPause;
    boolean isPlaying = true;  // Music starts via MusicService on launch
    int currentSongIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Start background music service
        Intent musicIntent = new Intent(MainActivity2.this, MusicService.class);
        musicIntent.putExtra("songIndex", currentSongIndex);  // Pass current song index on activity launch
        startService(musicIntent);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);

        btn_back = findViewById(R.id.btnBack);
        btn_clock = findViewById(R.id.btnclock);
        btn_playPause = findViewById(R.id.btnPlay);

        ViewPager2 musicCarousel = findViewById(R.id.musicCarousel);

        List<Song> songList = new ArrayList<>();
        songList.add(new Song(R.drawable.nature, "Nature Sounds"));
        songList.add(new Song(R.drawable.lofiimg, "Lofi Beats"));
        songList.add(new Song(R.drawable.beach, "Beach vibes"));

        // Back button returns to MainActivity
        btn_back.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity2.this, MainActivity.class);
            startActivity(i);
        });

        // Clock button goes to MainActivity3
        btn_clock.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity2.this, MainActivity3.class);
            startActivity(i);
        });

        MusicAdapter adapter = new MusicAdapter(songList);
        musicCarousel.setAdapter(adapter);

        musicCarousel.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentSongIndex = position;

                // Notify MusicService to change song
                Intent intent = new Intent(MainActivity2.this, MusicService.class);
                intent.putExtra("action", "changeSong"); // Action to change the song
                intent.putExtra("songIndex", currentSongIndex); // Pass the current song index
                startService(intent);
            }
        });

        // Play/Pause music via service
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

        // Sync play/pause icon with actual music status
        if (MusicService.isMusicPlaying) {
            btn_playPause.setImageResource(R.drawable.p_pause);
            isPlaying = true;
        } else {
            btn_playPause.setImageResource(R.drawable.p_play);
            isPlaying = false;
        }
    }
}
