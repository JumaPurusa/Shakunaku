package com.example.jay.shakunaku.Models;

import java.util.List;

public class Comment {

    private String comment;
    private String user_id;
    private List<Like> Likes;
    private String date_created;

    public Comment(){

    }

    public Comment(String comment, String user_id, List<Like> likes, String date_created) {
        this.comment = comment;
        this.user_id = user_id;
        Likes = likes;
        this.date_created = date_created;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "comment='" + comment + '\'' +
                ", user_id='" + user_id + '\'' +
                ", Likes=" + Likes +
                ", date_created='" + date_created + '\'' +
                '}';
    }

    public List<Like> getLikes() {
        return Likes;
    }

    public void setLikes(List<Like> likes) {
        Likes = likes;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }
}
