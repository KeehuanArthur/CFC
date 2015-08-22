package com.example.arthurlee.cfc;

import android.graphics.Bitmap;

/**
 * Created by arthurlee on 8/21/15.
 */
public class Announcement
{
    private Bitmap mImage;
    private String mDescription;
    private String mTitle;

    public Bitmap getImage() {
        return mImage;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setImage(Bitmap image) {
        mImage = image;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public void setTitle(String title) {
        mTitle = title;
    }
}
