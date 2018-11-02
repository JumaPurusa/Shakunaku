package com.example.jay.shakunaku.Models;

public class UserAccountSettings {

    private String bio;
    private String display_name;
    //private long followers;
    //private long following;
    //private long posts;
    private String profile_photo;
    private String username;
    private String website;
    private String user_id;

    public UserAccountSettings(String bio, String display_name,
                                String profile_photo, String username, String website, String user_id) {
        this.bio = bio;
        this.display_name = display_name;
        this.profile_photo = profile_photo;
        this.username = username;
        this.website = website;
        this.user_id = user_id;
    }

    public UserAccountSettings(){

    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "UserAccountSettings{" +
                "bio='" + bio + '\'' +
                ", display_name='" + display_name + '\'' +
                ", profile_photo='" + profile_photo + '\'' +
                ", username='" + username + '\'' +
                ", website='" + website + '\'' +
                ", user_id='" + user_id + '\'' +
                '}';
    }
}
