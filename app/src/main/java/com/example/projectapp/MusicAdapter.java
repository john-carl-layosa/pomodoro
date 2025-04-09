package com.example.projectapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectapp.R;
import com.example.projectapp.Song;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {

    // List to hold the songs that will be displayed in the RecyclerView (or ViewPager2)
    private List<Song> songList;

    // Constructor to initialize the adapter with the list of songs
    public MusicAdapter(List<Song> songList) {
        this.songList = songList;  // Set the song list passed in the constructor
    }

    // This method is called when a new ViewHolder is needed. It inflates the item layout and creates a ViewHolder.
    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item_music layout, which defines how each song item will be displayed
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_music, parent, false);

        // Return a new instance of MusicViewHolder, passing the inflated view
        return new MusicViewHolder(view);
    }

    // This method binds data from the song list to the ViewHolder (each song's image and title)
    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        // Get the song at the current position
        Song song = songList.get(position);

        // Set the song's image using its image resource ID
        holder.musicImage.setImageResource(song.getImageResId());

        // Set the song's title in the TextView
        holder.musicTitle.setText(song.getTitle());
    }

    // This method returns the total number of items (songs) in the list
    @Override
    public int getItemCount() {
        return songList.size();  // Return the size of the song list
    }

    // ViewHolder class to represent the UI elements for each item (song)
    static class MusicViewHolder extends RecyclerView.ViewHolder {
        // Declare ImageView for the song's image and TextView for the song's title
        ImageView musicImage;
        TextView musicTitle;

        // Constructor to initialize the UI components of each item
        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            // Find the ImageView and TextView by their IDs
            musicImage = itemView.findViewById(R.id.musicImage);
            musicTitle = itemView.findViewById(R.id.musicTitle);
        }
    }
}
