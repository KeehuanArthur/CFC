package com.example.arthurlee.cfc;

import java.util.ArrayList;

/**
 * Created by arthurlee on 8/15/15.
 * These are global variables. Don't hate
 */
public class Constants
{
    public static String nowPlayingTitle;
    public static String nowPlayingPastor;
    public static String nowPlayingPassage;
    public static String nowPlayingDate;
    public static String nowPlayingUrl;
    //note: newest is smallest

    public static String categoryName;
    public static String searchFor;
    public static boolean doneUpdatingSermons;

    //Array Lists
    public static ArrayList<Sermon> fullSermonList = new ArrayList<Sermon>();
    public static ArrayList<Announcement> announcementsList = new ArrayList<>();
    public static ArrayList<String>eventList = new ArrayList<>();

    // Sermon Category numbers
    public static int YEAR = 0;
    public static int SPEAKER = 1;
    public static int EVENT = 2;
    public static int SERIES = 3;

    //URLS
    public static String sermonsURL = "http://cfchome.org/sermons-json/";
    public static String announcementsURL = "http://s3.amazonaws.com/awctestbucket1/announcements.json";
    public static String announcementImagesURL = "http://s3.amazonaws.com/awctestbucket1/";
}
