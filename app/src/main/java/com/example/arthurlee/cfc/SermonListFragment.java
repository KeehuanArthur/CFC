package com.example.arthurlee.cfc;

import android.app.Activity;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;

/**
 * Created by arthurlee on 7/3/15.
 */
public class SermonListFragment extends ListFragment
{
    //private ArrayList<Sermon> mSermons;
    SermonAdapter mSermonAdapter;
    Context parentContext;
    Activity parentActivity;
    private String play = "Play";
    private String pause = "PAUSE";
    private Timer mTimer;
    private ProgressDialog pDialog;
    private Handler mHandler = new Handler();
    private String jsonURL = "http://s3.amazonaws.com/awctestbucket1/sermonInfo_version3.json";


    //tags for JSON parsing
    private static final String TAG_SERMONS = "sermons";
    private static final String TAG_FILENAME = "filename";
    private static final String TAG_DATE = "date";
    private static final String TAG_TITLE = "title";
    private static final String TAG_SCRIPTURE = "scripture";
    private static final String TAG_SPEAKER = "speaker";
    private static final String TAG_EVENT = "event";

    JSONArray jsonSermonList;


    //XML parser stuff
    static String urlString = "http://cfchome.org/feed/sermons/";
    static private XmlPullParserFactory xmlFactoryObject;
    static private String smallDate;





    public void setParentContext(Context parentContext) {
        this.parentContext = parentContext;
    }

    //overwrite the constructor?

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //getActivity().setTitle(R.string.sermon_title);
        //mSermons = SermonListSingleton.get(this.parentContext).getSermons();

        //mSermons = RSSFetcher.fetchRSS();

        parentActivity = getActivity();

        mSermonAdapter = new SermonAdapter(SermonListSingleton.get(getActivity()).getSermons());
        setListAdapter(mSermonAdapter);


        /////////////////////////////////////////////////////////////////////////////////
        //to switch between JSON and XML
        //don't forget to edit the url lookup on the SermonPlayer class
        /////////////////////////////////////////////////////////////////////////////////
        //JSON
        //new getSermons().execute();
        //XML
        new getSermonsXML().execute();
        ////////////////////////////////////////////////////////////////////////////////

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        //Log.d("SermonListFragment", "List fragment created!!");
        super.onCreate(savedInstanceState);
        getListView().setDivider(null);
        getListView().setDividerHeight(0);
        getListView().setBackgroundColor(Color.WHITE);

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        //New Activity Stuff
        Sermon s = ((SermonAdapter)getListAdapter()).getItem(position);
        Intent i = new Intent(getActivity(), MediaActivity.class);
        i.putExtra(MediaActivity.EXTRA_PASTOR_NAME, s.getPastor());
        i.putExtra(MediaActivity.EXTRA_MP3URL, s.getMp3url());
        i.putExtra(MediaActivity.EXTRA_SERMON_DATE, s.getSDate());
        i.putExtra(MediaActivity.EXTRA_SERMON_TITLE, s.getTitle());

        //Set Global Vars
        Constants.nowPlayingTitle = s.getTitle();
        Constants.nowPlayingPastor = s.getPastor();
        Constants.nowPlayingPassage = s.getScripture();
        Constants.nowPlayingDate = s.getSDate();
        Constants.nowPlayingUrl = s.getMp3url();

        //start notification controller here
        Intent intent = new Intent( getActivity().getApplicationContext(), DropdownControls.class );
        intent.putExtra(DropdownControls.ACTION_NOTIFICATION_EXTRA_TITLE, s.getTitle());
        intent.putExtra(DropdownControls.ACTION_NOTIFICATION_EXTRA_PASTOR, s.getPastor());
        intent.putExtra(DropdownControls.ACTION_NOTIFICATION_EXTRA_DATE, s.getSDate());
        intent.putExtra(DropdownControls.ACTION_NOTIFICATION_EXTRA_PASSAGE, s.getScripture());


        intent.setAction(DropdownControls.ACTION_NOTIFICATION_NULL);
        getActivity().getApplicationContext().startService(intent);


        startActivity(i);

    }



    //use default onCreateView for now which will inflate the stuff in the ArrayAdapter


    //this adapter is used to convert sermon info into xml style views
    public class SermonAdapter extends ArrayAdapter<Sermon>
    {
        public SermonAdapter(ArrayList<Sermon> sermons)
        {
            super(getActivity(), 0, sermons);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            //If we weren't given a view, inflate one
            if(convertView == null)
            {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_sermon, null);
            }

            //Configure view for Sermon
            final Sermon s = getItem(position);

            TextView titleTextView = (TextView)convertView.findViewById(R.id.sermon_list_item_titleTextView);
            titleTextView.setText(s.getTitle());

            TextView pastorTextView = (TextView)convertView.findViewById(R.id.sermon_list_item_pastorName);
            pastorTextView.setText(s.getPastor());

            TextView dateTextView = (TextView)convertView.findViewById(R.id.sermon_list_item_dateTextView);
            dateTextView.setText(s.getSDate());

            TextView passageTextView = (TextView)convertView.findViewById(R.id.sermon_list_item_passage);
            passageTextView.setText(s.getScripture());

            //buffering is called secondary progress


            /*
            DateFormat simpleDate = new SimpleDateFormat("MMM dd, yyyy");
            String formattedDate = simpleDate.format(s.getDate());
            dateTextView.setText(formattedDate);
            */
            return convertView;

        }
    }

//-------------------------------------------------------------------------------------------
//JSON version of getting sermons
    //note that there are 2 different Void types. ie void vs Void


    private class getSermons extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            // Showing progress dialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
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
            if (pDialog.isShowing())
                pDialog.dismiss();
        }
    }


 //--------------------------------------------------------------------------------------
//XML version of getting sermons


    private class getSermonsXML extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            // Showing progress dialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();


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
            if (pDialog.isShowing())
                pDialog.dismiss();
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
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        //add sermon into SermonAdapter (Array Adapter)
                                        mSermonAdapter.insert(s, mSermonAdapter.getCount());
                                    }
                                });
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

