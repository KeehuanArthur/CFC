package com.example.arthurlee.cfc;

import android.os.AsyncTask;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by arthurlee on 8/16/15.
 *
 * These functions will store the sermons array list in global variable in Constants.java
 */
public class SermonDownloader
{
    private static String urlString = "http://cfchome.org/feed/sermons/";
    private String smallDate;
    XmlPullParserFactory xmlFactoryObject;

    public void getSermons()
    {
        new getSermonsXML().execute();
    }

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
                                            if (name.equals("pubDate")) {
                                                smallDate = text.substring(0, 16);
                                                s.setSDate(smallDate);
                                            } else if (name.equals("itunes:author")) {
                                                s.setPastor(text);
                                            } else if (name.equals("itunes:passage")) {
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

    }

}
