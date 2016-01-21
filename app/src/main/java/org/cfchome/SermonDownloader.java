package org.cfchome;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Created by arthurlee on 8/16/15.
 *
 * This class is responsible for downloading the sermons and adding them to a global array list called
 * fullSermonList. There are a few ways to download the sermons using this class but the best way seems
 * to be using a JSON file.
 *
 * note for if you change download method:
 *  only the JSON downloader updates the global series and events ArrayList
 */
public class SermonDownloader
{
    String TAG = "SermonDownloader";
    private static String urlString = "http://cfchome.org/feed/sermons/";
    private String jsonURL = "http://cfchome.org/mobile/?get=sermons-json";

    private String smallDate;
    XmlPullParserFactory xmlFactoryObject;

    private MainPager mMainPager;

    public void getSermons()
    {
        new getSermonsXML().execute();
    }

    public void checkForNewSermons(MainPager mainPager)
    {
        mMainPager = mainPager;
        //new checkForUpdates().execute();
        new getSermonsJSON().execute();
    };



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

                updateLocalSermonList(myparser);
                stream.close();
                //Log.d("SermonDownloader","went through update");
            } catch (Exception e) {
                Constants.no_internet_connection = true;
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            mMainPager.updateHomeView();
        }

        /**
         * updateLocalSermonList() uses the parser to update the local arraylist that is already
         * occupied by the sermons from the JSON sermons that were parsed before this function was
         * called
         *
         * It looks at the current newest sermon in the list and while the sermon being checked
         * isn't the same as that new one, keep adding sermons to the list from the XML
         * this prevents an overlap from the sermons online and the sermons from the local file
         *
         * @param parser
         */
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
                                boolean event_found = false;
                                event = parser.next();

                                //look for other members of sermon
                                while (!sermonFinished) {
                                    name = parser.getName();
                                    switch (event) {
                                        case XmlPullParser.START_TAG:
                                            if (name.equals("enclosure")) {
                                                s.setMp3url(parser.getAttributeValue(null, "url"));
                                            }
                                            if( name.equals("itunes:event") )
                                            {
                                                event_found = true;
                                                //Log.d("SermonDownloader", "event found ------");
                                            }
                                            break;

                                        case XmlPullParser.TEXT:
                                            text = parser.getText();
                                            break;

                                        case XmlPullParser.END_TAG:
                                            //Log.d("SermonDownloader", "name " + name );
                                            String series_name = null;
                                            if( name != null && name.length() >= 12 )
                                            {
                                                series_name = name.substring(0, 12);
                                                //Log.d("SermonDownloader", "subname set: " + series_name);
                                            }
                                            if (name.equals("pubDate"))
                                            {
                                                s.setDate(parseDate(text));
                                            }
                                            else if (name.equals("itunes:author"))
                                            {
                                                s.setPastor(text);
                                            }
                                            else if ( event_found )
                                            {
                                                s.setEvent(text);
                                                event_found = false;
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
            try
            {
                JSONparser jsonParser = new JSONparser();
                RSSDateParser rssDateParser = new RSSDateParser();
                JSONObject jsonObject = jsonParser.getJSONFromUrl(Constants.jsonSermonUrl);
                JSONArray jsonArray = jsonObject.getJSONArray("sermons");

                ArrayList<Sermon> reversed_sermon_list = new ArrayList<>();
                String latest_local_sermon = Constants.fullSermonList.get(0).getTitle();

                for( int i = 0; i < jsonArray.length(); i++ )
                {
                    JSONObject jSermon = jsonArray.getJSONObject(i);

                    final Sermon s = new Sermon();

                    s.setTitle(jSermon.getString("title"));
                    s.setMp3url(jSermon.getJSONObject("enclosure").getString("url"));
                    s.setDate(rssDateParser.parse(jSermon.getString("pubDate")));
                    s.setPastor(jSermon.getString("author"));
                    s.setScripture(jSermon.getString("passage"));

                    if( jSermon.getJSONObject("event").has("name") )
                    {
                        s.setEvent(jSermon.getJSONObject("event").getString("name"));
                    }
                    if( jSermon.getJSONObject("series").has("name") )
                    {
                        s.setSeries(jSermon.getJSONObject("series").getString("name") );
                    }

                    // only parse the sermons that are not in the local list
                    if(s.getTitle().equals(latest_local_sermon))
                        break;

                    // update global event list
                    if(!Constants.eventList.contains(s.getEvent()) && s.getEvent() != null && !s.getEvent().equals("") )
                    {
                        Constants.eventList.add(s.getEvent());
                    }
                    // update global series list
                    if( !Constants.series_list.contains(s.getSeries()) && s.getSeries() != null && !s.getSeries().equals("") )
                    {
                        Constants.series_list.add( s.getSeries() );
                    }

                    reversed_sermon_list.add(s);
                }


                for( int i = reversed_sermon_list.size() - 1; i >= 0; i --)
                {
                    Constants.fullSermonList.add(0, reversed_sermon_list.get(i));
                }

                // alphabetize event list
                Collections.sort(Constants.eventList);

                // alphabetize series list
                Collections.sort(Constants.series_list);


            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void result)
        {
            mMainPager.updateHomeView();
        }
    }


}
