package com.example.arthurlee.cfc;

import android.app.Activity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by arthurlee on 8/22/15.
 */
public class LocalJSONManager
{
    Activity mCallingActivity;

    public LocalJSONManager(Activity a)
    {
        mCallingActivity = a;
    }


    private String loadJSONFromAsset()
    {
        String jsonString = null;
        try
        {
            InputStream is = mCallingActivity.getAssets().open("sermonListReduced.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, "UTF-8");

        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
        return jsonString;
    }

    public void parseLocalJSON()
    {
        try
        {
            Log.d("Local JSON", "trying to parse");
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray localSermonList = obj.getJSONArray("sermons");

            for( int i = 0; i < localSermonList.length(); i++ )
            {
                JSONObject jSermon = localSermonList.getJSONObject(i);

                final Sermon s = new Sermon();
                s.setTitle(jSermon.getString("title"));
                s.setPastor(jSermon.getString("speaker"));
                s.setSDate(jSermon.getString("date"));
                s.setMp3url(jSermon.getString("link"));
                s.setEvent(jSermon.getString("event"));
                s.setScripture(jSermon.getString("passage"));

                //update global event list
                if(!Constants.eventList.contains(s.getEvent()))
                {
                    Constants.eventList.add(Constants.eventList.size(), s.getEvent());
                }

                try
                {
                    DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
                    Date parsedDate = dateFormat.parse(jSermon.getString("date"));
                    s.setDate(parsedDate);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }


                //Log.d("LocalJSON","local sermon added " + s.getTitle());
                Constants.fullSermonList.add(Constants.fullSermonList.size(), s);
            }

        }
        catch (JSONException e)
        {

            e.printStackTrace();
        }

    }

}
