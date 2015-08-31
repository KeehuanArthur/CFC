package com.example.arthurlee.cfc;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by arthurlee on 8/21/15.
 */
public class HomeFragment extends Fragment
{
    private ArrayList<Sermon> mLatestSermonList;
    private SermonAdapter mLatestSermonAdapter;


    private LinearLayout mAnnouncementsLayout;
    private LinearLayout mRecentSermonLayout;

    FragmentManager fragmentManager;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.home_fragment, parent, false);

        //set up new sermon
        mRecentSermonLayout = (LinearLayout)v.findViewById(R.id.new_sermon_card);
        mRecentSermonLayout.addView(getSermonCard(0, inflater, parent));



        //set up announcements
        mAnnouncementsLayout = (LinearLayout)v.findViewById(R.id.announcement_cards);

        for(int i = 0; i < Constants.announcementsList.size(); i++)
        {
            mAnnouncementsLayout.addView(getAnnouncementCard(i, inflater, parent));

        }

        return v;
    }


    private View getSermonCard(int sermonNumber, LayoutInflater inflater, ViewGroup parent)
    {
        View sermonCard = inflater.inflate(R.layout.list_item_sermon, parent, false);

        TextView sermonTitle = (TextView)sermonCard.findViewById(R.id.sermon_list_item_titleTextView);
        TextView sermonSpeaker = (TextView)sermonCard.findViewById(R.id.sermon_list_item_pastorName);
        TextView sermonDate = (TextView)sermonCard.findViewById(R.id.sermon_list_item_dateTextView);
        TextView sermonPassage = (TextView)sermonCard.findViewById(R.id.sermon_list_item_passage);

        Sermon s = Constants.fullSermonList.get(sermonNumber);

        sermonTitle.setText(s.getTitle());
        sermonSpeaker.setText(s.getPastor());
        sermonDate.setText(s.getSDate());
        sermonPassage.setText(s.getScripture());

        return sermonCard;
    }

    private View getAnnouncementCard(int announcementNumber, LayoutInflater inflater, ViewGroup parent)
    {
        View announcementCard = inflater.inflate(R.layout.list_item_announcement, parent, false);

        TextView announcementTitle = (TextView)announcementCard.findViewById(R.id.list_item_announcement_Title);
        ImageView announcementImage = (ImageView)announcementCard.findViewById(R.id.list_item_announcement_image);

        Announcement a = Constants.announcementsList.get(announcementNumber);

        announcementTitle.setText(a.getTitle());
        announcementImage.setImageBitmap(a.getImage());

        return announcementCard;
    }
}
