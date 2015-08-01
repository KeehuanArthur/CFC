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
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private Button mPlayButton;
    private Button mDownloadButton;
    private AudioPlayer mPlayer = new AudioPlayer();
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

        new getSermons().execute();
    }




    /*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.list_item_sermon, parent, false);

        return v;
    }
    */

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Log.d("SermonListFragment", "List fragment created!!");
        super.onCreate(savedInstanceState);
        getListView().setDivider(null);
        getListView().setDividerHeight(0);
        getListView().setBackgroundColor(Color.WHITE);
        //getListView().deferNotifyDataSetChanged();


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


            //buffering is called secondary progress



            mPlayButton = (Button)convertView.findViewById(R.id.sermon_list_item_playbutton);
            mPlayButton.setOnClickListener(new View.OnClickListener(){
                public void onClick(View convertView)
                {
                    Intent i = new Intent(getActivity(), MediaActivity.class);
                    i.putExtra(MediaActivity.EXTRA_PASTOR_NAME, s.getPastor());
                    i.putExtra(MediaActivity.EXTRA_MP3URL, s.getMp3url());
                    i.putExtra(MediaActivity.EXTRA_SERMON_DATE, s.getSDate());
                    i.putExtra(MediaActivity.EXTRA_SERMON_TITLE, s.getTitle());
                    i.putExtra(MediaActivity.EXTRA_SERMON_UUID, s.getId().toString());
                    startActivity(i);

                    //Log.d("UUID of sermon", s.getId().toString());
                    //getAllowEnterTransitionOverlap(R.anim.abc_slide_in_bottom);
                    //getActivity().overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_top);

                }
            });

            mDownloadButton = (Button)convertView.findViewById(R.id.sermon_list_item_downloadbutton);
            mDownloadButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View convertView) {
                    mPlayer.stop();
                }
            });




            /*
            DateFormat simpleDate = new SimpleDateFormat("MMM dd, yyyy");
            String formattedDate = simpleDate.format(s.getDate());
            dateTextView.setText(formattedDate);
            */
            return convertView;

        }
    }


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




}

