package com.example.jay.shakunaku.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Photos implements Parcelable{

    //private String caption;
    private String photo_caption;
    private String date_created;
    private String image_path;
    private String photo_id;
    private String user_id;
    private List<Like> Likes;
    private List<Comment> Comments;
    //private String tags;

    public Photos(){

    }

    public Photos(String photo_caption, String date_created, String image_path,
                  String photo_id, String user_id, List<Like> likes, List<Comment> comments) {
        this.photo_caption = photo_caption;
        this.date_created = date_created;
        this.image_path = image_path;
        this.photo_id = photo_id;
        this.user_id = user_id;
        this.Likes = likes;
        this.Comments = comments;
    }

    protected Photos(Parcel in) {
        photo_caption = in.readString();
        date_created = in.readString();
        image_path = in.readString();
        photo_id = in.readString();
        user_id = in.readString();

    }

    public static final Creator<Photos> CREATOR = new Creator<Photos>() {
        @Override
        public Photos createFromParcel(Parcel in) {
            return new Photos(in);
        }

        @Override
        public Photos[] newArray(int size) {
            return new Photos[size];
        }
    };

    public String getPhoto_caption() {
        return photo_caption;
    }

    public void setPhoto_caption(String photo_caption) {
        this.photo_caption = photo_caption;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(String photo_id) {
        this.photo_id = photo_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public List<Like> getLikes() {
        return Likes;
    }

    public void setLikes(List<Like> likes) {
        Likes = likes;
    }

    public List<Comment> getComments() {
        return Comments;
    }

    public void setComments(List<Comment> comments) {
        Comments = comments;
    }

    @Override
    public String toString() {
        return "Photos{" +
                "photo_caption='" + photo_caption + '\'' +
                ", date_created='" + date_created + '\'' +
                ", image_path='" + image_path + '\'' +
                ", photo_id='" + photo_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", Likes=" + Likes +
                ", Comments=" + Comments +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(photo_caption);
        dest.writeString(date_created);
        dest.writeString(image_path);
        dest.writeString(photo_id);
        dest.writeString(user_id);

    }

}
