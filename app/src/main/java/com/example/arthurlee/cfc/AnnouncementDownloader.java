package com.example.arthurlee.cfc;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

/**
 * Created by arthurlee on 8/21/15.
 */
public class AnnouncementDownloader
{
    JSONArray jsonAnnouncementList;

    //JSONTags
    private static final String TAG_TITLE = "title";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_IMAGE_URL = "filename";
    private MainPager mMainPager;

    public void getAnnouncements(MainPager mainPager)
    {
        new  getAnnouncementsJSON().execute();
        //mainPager.updateHomeView();
        mMainPager = mainPager;
    }


    public class getAnnouncementsJSON extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void ... arg0)
        {
            ServiceHandler sh = new ServiceHandler();

            String jsonStr = sh.makeServiceCall(Constants.announcementsURL, ServiceHandler.GET);

            if(jsonStr != null)
            {
                try
                {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    jsonAnnouncementList = jsonObj.getJSONArray("announcements");

                    for( int i = 0; i < jsonAnnouncementList.length(); i++ )
                    {
                        JSONObject js = jsonAnnouncementList.getJSONObject(i);

                        final Announcement announcement = new Announcement();

                        announcement.setTitle(js.getString(TAG_TITLE));
                        announcement.setDescription(js.getString(TAG_DESCRIPTION));

                        String imageURL = js.getString(TAG_IMAGE_URL);
                        imageURL = Constants.announcementImagesURL + imageURL;

                        Bitmap bitmapImage = null;

                        try
                        {
                            InputStream input = new java.net.URL(imageURL).openStream();
                            bitmapImage = BitmapFactory.decodeStream(input);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        if( bitmapImage != null )
                            Log.d("bitmap parse", "success ");

                        announcement.setImage(bitmapImage);

                        Constants.announcementsList.add(Constants.announcementsList.size(), announcement);
                        Constants.numbAnnouncements ++;
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }



            else
            {
                Log.d("AnnouncementsDownloader", "connection error");
            }


            return null;
        }

        @Override
        protected void onPostExecute (Void result)
        {
            Constants.doneUpdatingAnnouncements = true;
            mMainPager.updateHomeView();
        }

    }

}
