package com.example.jay.shakunaku.Models;

public class PostView {

    private UserSettings settings;
    private Photos photos;

    public PostView(UserSettings settings, Photos photos) {
        this.settings = settings;
        this.photos = photos;
    }

    public UserSettings getSettings() {
        return settings;
    }

    public void setSettings(UserSettings settings) {
        this.settings = settings;
    }

    public Photos getPhotos() {
        return photos;
    }

    public void setPhotos(Photos photos) {
        this.photos = photos;
    }

    @Override
    public String toString() {
        return "PostView{" +
                "settings=" + settings +
                ", photos=" + photos +
                '}';
    }
}
