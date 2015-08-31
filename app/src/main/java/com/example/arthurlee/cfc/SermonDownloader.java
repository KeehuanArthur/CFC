package com.example.arthurlee.cfc;

import android.os.AsyncTask;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by arthurlee on 8/16/15.
 *
 * These functions will store the sermons array list in global variable in Constants.java
 */
public class SermonDownloader
{
    private static String urlString = "http://cfchome.org/feed/sermons/";
    private String jsonURL = "http://s3.amazonaws.com/awctestbucket1/sermonInfo_version3.json";

    private String smallDate;
    XmlPullParserFactory xmlFactoryObject;

    public void getSermons()
    {
        new getSermonsXML().execute();
    }

    public void checkForNewSermons() { new checkForUpdates().execute(); };



    //-------------------------------------------------------------------------------------------
    //Sermon Updater run this stuff after parsing the local list to add to the list

    public class checkForUpdates extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0)
        {
            try {
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

                updateLocalSermonList(myparser);
                stream.close();
                Log.d("SermonDownloader","went through update");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private void updateLocalSermonList(XmlPullParser parser)
        {
            int event;
            String text = null;
            String sermonTitle = "none";
            String curNewest = Constants.fullSermonList.get(0).getTitle();
            ArrayList<Sermon> tempList = new ArrayList<>();

            try
            {
                event = parser.getEventType();

                //look for title and create new final sermon
                while (!curNewest.equals(sermonTitle)) {
                    String name = parser.getName();

                    switch (event) {
                        case XmlPullParser.START_TAG:
                            break;

                        case XmlPullParser.TEXT:
                            text = parser.getText();
                            break;

                        case XmlPullParser.END_TAG:
                            if (name.equals("title")) {

                                if(text.equals("Covenant Fellowship"))
                                    break;

                                final Sermon s = new Sermon();
                                s.setTitle(text);
                                sermonTitle = text;
                                boolean sermonFinished = false;
                                event = parser.next();

                                //look for other members of sermon
                                while (!sermonFinished) {
                                    name = parser.getName();
                                    switch (event) {
                                        case XmlPullParser.START_TAG:
                                            if (name.equals("enclosure")) {
                                                s.setMp3url(parser.getAttributeValue(null, "url"));
                                            }
                                            break;

                                        case XmlPullParser.TEXT:
                                            text = parser.getText();
                                            break;

                                        case XmlPullParser.END_TAG:
                                            if (name.equals("pubDate"))
                                            {
                                                s.setDate(parseDate(text));
                                            }
                                            else if (name.equals("itunes:author"))
                                            {
                                                s.setPastor(text);
                                            }
                                            else if (name.equals("itunes:series"))
                                            {
                                                s.setEvent(text);
                                            }
                                            else if (name.equals("itunes:passage"))
                                            {
                                                s.setScripture(text);
                                                sermonFinished = true;
                                            }
                                            break;

                                    }

                                    event = parser.next();
                                }

                                tempList.add(tempList.size(), s);
                            }
                            break;
                    }
                    event = parser.next();
                }
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }


            if( tempList.size() != 0 )
            {
                int count = tempList.size();
                while(count > 0 )
                {
                    count --;
                    Constants.fullSermonList.add(0, tempList.get(count));
                }
            }

        }

        //throws Exception is for the DateFormat parsing function
        public Date parseDate(String stringDate) throws Exception
        {
            DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");

            String prepDate = stringDate.substring(5, 16);

            Date sermon_date = dateFormat.parse(prepDate);

            return sermon_date;
            //return prepDate;
        }
    }


    //-------------------------------------------------------------------------------------------
    //XML Version of getting sermons

    public class getSermonsXML extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void ... arg0)
        {
            try {
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

                parseXMLAndStoreIt(myparser);
                stream.close();
            } catch (Exception e) {
                //Log.d("RSSFetcher", e.toString());

                //create no internet connection toast here
                //this crashes right now. look up handlers (multi threading)
                //Toast.makeText(MainPagerContext,"Connection Error", Toast.LENGTH_SHORT);
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void result)
        {
            super.onPreExecute();
        }




        public void parseXMLAndStoreIt(XmlPullParser myParser)
        {
            int event;
            String text = null;

            try
            {
                event = myParser.getEventType();

                //look for title and create new final sermon
                while (event != XmlPullParser.END_DOCUMENT) {
                    String name = myParser.getName();

                    switch (event) {
                        case XmlPullParser.START_TAG:
                            break;

                        case XmlPullParser.TEXT:
                            text = myParser.getText();
                            break;

                        case XmlPullParser.END_TAG:
                            if (name.equals("title")) {

                                if(text.equals("Covenant Fellowship"))
                                    break;

                                final Sermon s = new Sermon();
                                s.setTitle(text);
                                boolean sermonFinished = false;
                                event = myParser.next();

                                //look for other members of sermon
                                while (!sermonFinished) {
                                    name = myParser.getName();
                                    switch (event) {
                                        case XmlPullParser.START_TAG:
                                            if (name.equals("enclosure")) {
                                                s.setMp3url(myParser.getAttributeValue(null, "url"));
                                            }
                                            break;

                                        case XmlPullParser.TEXT:
                                            text = myParser.getText();
                                            break;

                                        case XmlPullParser.END_TAG:
                                            if (name.equals("pubDate"))
                                            {
                                                s.setDate(parseDate(text));
                                            }
                                            else if (name.equals("itunes:author"))
                                            {
                                                s.setPastor(text);
                                            }
                                            else if (name.equals("itunes:series"))
                                            {
                                                s.setEvent(text);
                                            }
                                            else if (name.equals("itunes:passage"))
                                            {
                                                s.setScripture(text);
                                                sermonFinished = true;
                                            }
                                            break;

                                    }
                                    //Log.d("working on sermon: ", s.getTitle());

                                    event = myParser.next();
                                }

                                Constants.fullSermonList.add(Constants.fullSermonList.size(), s);
                            }
                            break;
                    }
                    event = myParser.next();
                }
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        //throws Exception is for the DateFormat parsing function
        public Date parseDate(String stringDate) throws Exception
        {
            DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");

            String prepDate = stringDate.substring(5, 16);

            Date sermon_date = dateFormat.parse(prepDate);

            return sermon_date;
            //return prepDate;
        }

    }



    //-------------------------------------------------------------------------------------------
    //JSON version of getting sermons
    //note that there are 2 different Void types. ie void vs Void

/*
    private class getSermonsJSON extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0)
        {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(jsonURL, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    jsonSermonList = jsonObj.getJSONArray(TAG_SERMONS);

                    // looping through All Sermons
                    for (int i = 0; i < jsonSermonList.length(); i++)
                    {
                        JSONObject js = jsonSermonList.getJSONObject(i);

                        String title = js.getString(TAG_TITLE);
                        String pastor = js.getString(TAG_SPEAKER);
                        String date = js.getString(TAG_DATE);
                        String mp3URL = js.getString(TAG_FILENAME);
                        String scripture = js.getString(TAG_SCRIPTURE);
                        String event = js.getString(TAG_EVENT);


                        //create new sermon object
                        final Sermon s = new Sermon();
                        s.setTitle(title);
                        s.setPastor(pastor);
                        s.setSDate(date);
                        s.setMp3url(mp3URL);
                        s.setScripture(scripture);
                        s.setEvent(event);



                        getActivity().runOnUiThread(new Runnable(){
                            public void run()
                            {
                                //add sermon into SermonAdapter (Array Adapter)
                                mSermonAdapter.insert(s, mSermonAdapter.getCount());
                            }
                        });

                    }
                    //mSermonAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPreExecute();

        }
    }

*/
}
