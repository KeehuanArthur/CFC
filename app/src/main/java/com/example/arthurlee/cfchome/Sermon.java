package com.example.arthurlee.cfchome;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by arthurlee on 7/3/15.
 */
public class Sermon
{
    //-------------------------------------------------------
    //variables
    private String title;
    private Date mDate;
    private String mSDate; //this is a temporary string version of date
    private String Pastor;
    private UUID mId;
    private int mLength; //this is in ms
    private String mp3url;  //http://s3.amazonaws.com/awctestbucket1/<mp3url>
    private String mEvent;
    private String mSeries;
    private String mScripture;
    //note make Pastor into its own class sometime in the future

    //------------------------------------------------------
    //constructor
    public Sermon()
    {
        //Generate unique identifier
        mId = UUID.randomUUID();
        mDate = new Date();
        //note: new Date() automatically sets date to today's date
    }


    //--------------------------------------------------------
    //getters and setters
    public String getTitle() {
        return title;
    }

    public Date getDate() {
        return mDate;
    }

    public String getPastor() {
        return Pastor;
    }

    public UUID getId() {
        return mId;
    }

    public String getSDate() {

        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");

        return dateFormat.format(mDate);

        //return mSDate;
    }

    public void setSDate(String SDate) {
        mSDate = SDate;

    }

    public String getSeries() { return  mSeries; }

    public String getMp3url() {
        return mp3url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public void setPastor(String pastor) {
        Pastor = pastor;
    }

    public void setMp3url(String mp3url) {
        this.mp3url = mp3url;
    }

    public void setLength(int length) {
        mLength = length;
    }

    public int getLength() {
        return mLength;
    }

    public String getEvent() {
        return mEvent;
    }

    public String getScripture() {
        return mScripture;
    }

    public void setEvent(String event) {
        mEvent = event;
    }

    public void setScripture(String scripture) {
        mScripture = scripture;
    }

    public void setSeries( String series ) { mSeries = series; }
}
