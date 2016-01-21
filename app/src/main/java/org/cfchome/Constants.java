package org.cfchome;

import java.util.ArrayList;

/**
 * Created by arthurlee on 8/15/15.
 *
 * Global Varaibles
 */
public class Constants
{
    // Sermon Player vars
    public static String nowPlayingTitle;
    public static String nowPlayingPastor;
    public static String nowPlayingPassage;
    public static String nowPlayingDate;
    public static String nowPlayingUrl;
    public static boolean sermonPlayerPaused = false;
    //note: newest is smallest

    public static String categoryName;
    public static String searchFor;
    public static boolean doneUpdatingAnnouncements;
    public static int numbAnnouncements = 0;

    // Array Lists
    public static ArrayList<Sermon> fullSermonList = new ArrayList<Sermon>();
    public static ArrayList<Announcement> announcementsList = new ArrayList<>();
    public static ArrayList<String>eventList = new ArrayList<>();
    public static ArrayList<String>pastoral_staff = new ArrayList<>();  // this is set up in mainpager
    public static ArrayList<String>series_list = new ArrayList<>();

    // Sermon Category numbers
    public static int YEAR = 0;
    public static int SPEAKER = 1;
    public static int EVENT = 2;
    public static int SERIES = 3;

    // URLS
    public static String sermonsURL = "http://cfchome.org/sermons-json/";
    public static String jsonSermonUrl = "http://cfchome.org/mobile/?get=sermons-json";
    public static String announcementsURL = "http://cfchome.org/mobile/?get=special-events-json";
    public static String announcementImagesURL = "http://s3.amazonaws.com/awctestbucket1/";


    // Buffering and Synchronization
    public static boolean viewable;                 // if app is viewable or in background
    public static boolean homefragment_visible;         // if homeview is being shown
    public static boolean pending_homeview_update;  // if there was update while app was in background
    public static boolean sermon_buffering;         // if sermon is still buffering
    public static int curAnnouncement = 0;
    public static int announcementsTotal = 0;
    public static boolean no_internet_connection;
    public static boolean sermon_force_restart = false;
    public static boolean sermon_active = false;    // if there is a sermon playing or on pause
    public static boolean failed_update = true;     // if the apps internal json libraries were updated when
                                                    // the app was launched
    public static int number_of_updates = 0;
}
