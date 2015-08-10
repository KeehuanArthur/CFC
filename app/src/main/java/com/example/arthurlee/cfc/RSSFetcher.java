package com.example.arthurlee.cfc;

import android.content.Context;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by arthurlee on 7/6/15.
 */
public class RSSFetcher
{
    static private Context MainPagerContext;
    static String urlString = "http://cfchome.org/feed/sermons/";
    static private XmlPullParserFactory xmlFactoryObject;
    static public volatile boolean parsingComplete = true;
    static public String smallDate;
    static Sermon c;






   static ArrayList<Sermon> sSermon = new ArrayList<Sermon>();

    public static ArrayList<Sermon> fetchRSS()
    {
        //ArrayList<Sermon> sSermon = new ArrayList<Sermon>();
        //Log.d("RSSFetcher", "RssLooked up");
        //MainPagerContext = parentContext;
        lookUpRSS();//
        return sSermon;
    }

    public static void lookUpRSS()
    {
        //note: you need to multi-thread here b/c android doesn't allow internet connecting methods
        //      to run as main method
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
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
            }
        });
        thread.start();
    }


    static public void parseXMLAndStoreIt(XmlPullParser myParser)
    {
        int event;
        boolean doneWithSermon;
        String text=null;

        //Log.d("RSSFetcher", "parse and store it");

        try
        {
            event = myParser.getEventType();

            while (event != XmlPullParser.END_DOCUMENT) {
                String name = myParser.getName();

                switch (event)
                {
                    case XmlPullParser.START_TAG:
                        if(name.equals("enclosure"))
                        {
                            c.setMp3url(myParser.getAttributeValue(null, "url"));
                            //Log.d("mp3URL", c.getMp3url());
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = myParser.getText();
                        break;

                    case XmlPullParser.END_TAG:

                        if(name.equals("title"))
                        {
                            c = new Sermon();
                            c.setTitle(text);
                        }


                        else if(name.equals("pubDate"))
                        {
                            //convert this to date type sometime in the future

                            smallDate = text.substring(0,16);
                            c.setSDate(smallDate);
                        }




                        else if(name.equals("itunes:author"))
                        {
                            //Log.d("RSSFetcher", "Sermon Added");
                            c.setPastor(text);


                            //this is the fix the issue that xmlparser thinking that the channel
                            //is an item.
                            //note: in java garbage collector takes care of null pointers so you
                            //      don't have to worry about memory leaks
                            if(text.equals("Covenant Fellowship"))
                            {
                                c = null;
                                break;
                            }

                            //try adding them to array adapter directly using ArrayAdapter.add(...)
                            //maybe we dont need sermonListSingleton and we can add to SermonAdapter directly
                            sSermon.add(c);
                        }

                        else if(name.equals("itunes:duration"))
                        {
                            String minString = text.substring(0,1);
                            String secString = text.substring(text.length()-2, text.length()-1);

                            int min = Integer.parseInt(minString);
                            int sec = Integer.parseInt(secString);


                            //something is wrong with the min sec parsing
                            //might not need this anyways

                            //Log.d("min", minString);
                            //Log.d("sec", secString);

                            c.setLength((sec+ min*60)*1000);
                        }

                        break;
                }

                event = myParser.next();
            }

            //parsingComplete = false;
            Log.d("RSSFetcher", "parse and store it");

        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


}
