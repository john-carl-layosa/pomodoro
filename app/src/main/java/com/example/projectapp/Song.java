package com.example.projectapp;

public class Song {
    // Declare private fields for storing the image resource ID and title of the song
    private int imageResId;
    private String title;

    // Constructor to initialize the Song object with an image resource ID and title
    public Song(int imageResId, String title) {
        this.imageResId = imageResId;  // Set the image resource ID
        this.title = title;            // Set the title of the song
    }

    // Getter method to retrieve the image resource ID
    public int getImageResId() {
        return imageResId;  // Return the image resource ID
    }

    // Getter method to retrieve the title of the song
    public String getTitle() {
        return title;  // Return the title of the song
    }
}
