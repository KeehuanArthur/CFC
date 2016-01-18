package com.example.arthurlee.cfc;

import android.graphics.Bitmap;

import java.util.Date;

/**
 * Created by arthurlee on 8/21/15.
 */
public class Announcement
{
    private Bitmap mImage;
    private String mDescription;
    private String mTitle;
    private Date mDate;
    private String mTime;
    private String mLocation;

    public Bitmap getImage() {
        return mImage;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getTitle() {
        return mTitle;
    }

    public Date getDate() { return mDate; }

    public String getTime() {return mTime; }

    public String getLocation() {return mLocation; };

    public void setImage(Bitmap image) {
        mImage = image;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setDate(Date date) { mDate = date; }

    public void setTime(String time) { mTime = time; }

    public void setLocation(String location) { mLocation = location; }
}
