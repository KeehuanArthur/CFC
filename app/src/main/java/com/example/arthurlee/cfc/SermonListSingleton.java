package com.example.arthurlee.cfc;

import android.content.Context;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by arthurlee on 7/3/15.
 */
public class SermonListSingleton
{


//------------------------------------------------------------------------------
//private variables
    private ArrayList<Sermon> mSermons;

    private static SermonListSingleton sSermonListSingleton;
    //note: s means static variable
    private static Context mAppContext;

    //-------------------------------------------------------------------------------
//Singleton Constructor stuff

    private SermonListSingleton(Context appContext)
    {
        mAppContext = appContext;
        mSermons = new ArrayList<Sermon>();

        //this returns ArrayList of sermons which is created in the RSSFetcher
        //mSermons = RSSFetcher.fetchRSS();


        /*
        //make up some sermons
        for(int i = 0; i < 100; i++)
        {
            Sermon c = new Sermon();
            c.setTitle("Sermon#" + i);
            mSermons.add(c);
        }
        */

    }

    public static SermonListSingleton get(Context c)
    {
        mAppContext = c;
        if(sSermonListSingleton == null)
        {
            sSermonListSingleton = new SermonListSingleton(c.getApplicationContext());
        }

        return sSermonListSingleton;
    }

//-------------------------------------------------------------------------
//Functions

    public ArrayList<Sermon> getSermons()
    {
        return mSermons;
    }

    //maybe change this to binary search eventually
    public Sermon getSermon(UUID id)
    {
        for (Sermon c : mSermons)
        {
            if(c.getId().equals(id))
            {
                return c;
            }
        }
        return null;
    }

    public void addSermon(Sermon s)
    {
        mSermons.add(s);
    }

}
