package com.example.jay.shakunaku.Profile;

public class Action {

    private int position;
    private String actionImage;
    private String actionText;

    public static Action[] actions = {
            new Action(0,"gallery", "Select from Gallery"),
            new Action(1,"photo_camera", "Take a Photo"),
            new Action(2,"delete","Remove Photo")
    };

    public Action(int position, String actionImage, String actionText) {
        this.position = position;
        this.actionImage = actionImage;
        this.actionText = actionText;
    }

    public int getPosition() {
        return position;
    }

    public String getActionImage() {
        return actionImage;
    }

    public void setActionImage(String actionImage) {
        this.actionImage = actionImage;
    }

    public String getActionText() {
        return actionText;
    }

    public void setActionText(String actionText) {
        this.actionText = actionText;
    }
}
