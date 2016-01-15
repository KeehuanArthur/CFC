package com.example.arthurlee.cfc;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by arthurlee on 8/21/15.
 */
public class HomeFragment extends Fragment
{
    private ArrayList<Sermon> mLatestSermonList;
    private SermonAdapter mLatestSermonAdapter;
    private ViewPager mAnnouncementPager;
    private PagerAdapter mAnnouncementPagerAdapter;

    //private LinearLayout mAnnouncementsLayout;
    private LinearLayout mRecentSermonLayout;

    FragmentManager fragmentManager;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Log.d("home fragment", "home fragment has been created --------");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.home_fragment, parent, false);

        //set up new sermon
        mRecentSermonLayout = (LinearLayout)v.findViewById(R.id.new_sermon_card);
        mRecentSermonLayout.addView(getSermonCard(0, inflater, parent));

        /**
         * when announcements are done loading, MainPager.updateHomeView() will be called which will
         * reload the HomeFragment. check if announcements are done loading here
         */
        if(Constants.doneUpdatingAnnouncements)
        {
            v.findViewById(R.id.announcementsLoadingCircle).setVisibility(View.GONE);
        }


        mAnnouncementPager = (ViewPager)v.findViewById(R.id.announcements_viewpager);
        mAnnouncementPagerAdapter = new AnnouncementPagerAdapter(getChildFragmentManager());
        mAnnouncementPager.setAdapter(mAnnouncementPagerAdapter);

        return v;
    }


    @Override
    public void onPause()
    {
        super.onPause();

        Constants.curAnnouncement = 0;
    }


    private View getSermonCard(int sermonNumber, LayoutInflater inflater, ViewGroup parent)
    {
        View sermonCard = inflater.inflate(R.layout.list_item_sermon, parent, false);

        TextView sermonTitle = (TextView)sermonCard.findViewById(R.id.sermon_list_item_titleTextView);
        TextView sermonSpeaker = (TextView)sermonCard.findViewById(R.id.sermon_list_item_pastorName);
        TextView sermonDate = (TextView)sermonCard.findViewById(R.id.sermon_list_item_dateTextView);
        TextView sermonPassage = (TextView)sermonCard.findViewById(R.id.sermon_list_item_passage);

        /*   commented block was going to be used to check if there's a way to check when sermon parsing is done
             by seeing if null is returned by arraylist. remember to take into consideration that there's also internal JSON file
             containing sermons that are also parsed
        ArrayList<Sermon> fullsermon
        if(s == null )
            Log.d("didn't receive sermon:", "null returned");
        */
        Sermon s = Constants.fullSermonList.get(sermonNumber);



        sermonTitle.setText(s.getTitle());
        sermonSpeaker.setText(s.getPastor());
        sermonDate.setText(s.getSDate());
        sermonPassage.setText(s.getScripture());

        RelativeLayout.LayoutParams recentSermonCardParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        sermonCard.setLayoutParams(recentSermonCardParams);

        sermonCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("starting Sermon Player", "launching form LIBRARY FRAGMENT!");

                //New Activity Stuff
                Sermon s = Constants.fullSermonList.get(0);
                Intent i = new Intent(getActivity(), MediaActivity.class);
                i.putExtra(MediaActivity.EXTRA_PASTOR_NAME, s.getPastor());
                i.putExtra(MediaActivity.EXTRA_MP3URL, s.getMp3url());
                i.putExtra(MediaActivity.EXTRA_SERMON_DATE, s.getSDate());
                i.putExtra(MediaActivity.EXTRA_SERMON_TITLE, s.getTitle());
                i.putExtra(MediaActivity.EXTRA_SERMON_SCRIPTURE, s.getScripture());

                //Set Global Vars
                Constants.nowPlayingTitle = s.getTitle();
                Constants.nowPlayingPastor = s.getPastor();
                Constants.nowPlayingPassage = s.getScripture();
                Constants.nowPlayingDate = s.getSDate();
                Constants.nowPlayingUrl = s.getMp3url();

                startActivity(i);
            }
        });

        return sermonCard;
    }



    private class AnnouncementPagerAdapter extends FragmentPagerAdapter
    {
        public AnnouncementPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        /**
         * getItem() is only called once per position and only goes up. it doesn't get called again
         * when going from position 5 to 4 but does get called when going from 5 to 6
         *
         * ie it will only get called when the fragment that it wants doesn't exist and fragments
         *    don't get destroyed after they go off the screen like in a scroll view
         *
         * because of this, you can keep a constant that keeps track of which sermon is next to be
         * loaded and use that to determine which image to load up on single announcement page onCreateView()
         *
         * http://stackoverflow.com/questions/19339500/when-is-fragmentpageradapters-getitem-called
         */
        @Override
        public Fragment getItem(int position)
        {
            /**
             * the AnnouncementView knows which image to display by looking at the Constants.announcementNumb
             * which will be incremented each time a new AnnouncementView is made
             */
            Log.d("HomeFragment", "getitem was called");
            return new AnnouncementView();
        }

        @Override
        public int getCount()
        {
            return Constants.numbAnnouncements;

        }
    }
}
