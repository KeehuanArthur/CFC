package com.example.arthurlee.cfc;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by arthurlee on 8/21/15.
 */
public class AnnouncementDownloader
{
    JSONArray jsonAnnouncementList;

    // JSONTags
    private static final String TAG_TITLE = "title";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_IMAGE_URL = "filename";
    private MainPager mMainPager;

    // XML stuff
    private static String urlString = "http://cfchome.org/mobile/?get=front-page-banners";
    XmlPullParserFactory xmlFactoryObject;



    public void getAnnouncements(MainPager mainPager)
    {
        //new  getAnnouncementsJSON().execute();
        new getAnnouncementsXML().execute();
        mMainPager = mainPager;
    }

    public class getAnnouncementsXML extends AsyncTask <Void, Void, Void>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void ... arg0)
        {
            try{
                Constants.no_internet_connection = false;
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);

                // Starts the query
                conn.connect();
                InputStream stream = conn.getInputStream();

                xmlFactoryObject = XmlPullParserFactory.newInstance();
                XmlPullParser myparser = xmlFactoryObject.newPullParser();

                myparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                myparser.setInput(stream, null);
                updateLocalAnnouncementList(myparser);

                stream.close();
                Log.d("AnnouncementDownloader","went through update");
            }
            catch (Exception e)
            {
                Constants.no_internet_connection = true;
                e.printStackTrace();
            }

            return null;
        }
        @Override
        protected void onPostExecute(Void result)
        {
            Constants.doneUpdatingAnnouncements = true;
            mMainPager.updateHomeView();
        }

        private void updateLocalAnnouncementList(XmlPullParser parser)
        {
            int event;
            String text = null;
            String img_src = null;
            String title = "Title";
            String description = "Description";

            try{
                event = parser.getEventType();

                while(event != XmlPullParser.END_DOCUMENT)
                {
                    String name = parser.getName();

                    switch (event) {
                        case XmlPullParser.START_TAG:
                            break;

                        case XmlPullParser.TEXT:
                            text = parser.getText();
                            break;

                        case XmlPullParser.END_TAG:
                            if (name.equals("image")) {
                                img_src = text;
                                Log.d("AnnouncementDown","img src: " + img_src);
                            }

                            Constants.announcementsTotal ++;
                            final Announcement a = new Announcement();
                            a.setImage(getBitmapFromURL(img_src));
                            Constants.announcementsList.add(a);
                            Log.d("AnnouncementDown", "added announcement");

                            break;
                    }
                    event = parser.next();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            mMainPager.updateHomeView();

        }

        public Bitmap getBitmapFromURL(String src) {
            try {
                URL url = new URL(src);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (IOException e) {
                // Log exception
                Log.d("AnnouncementDown", "failed download");
                return null;
            }
        }
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
