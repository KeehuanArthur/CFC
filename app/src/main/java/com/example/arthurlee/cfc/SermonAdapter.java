package com.example.arthurlee.cfc;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by arthurlee on 8/19/15.
 *
 * This Class doesn't seem to be used. Appears once in HomeFragment but isn't used for anything..
 * The onClickListeners seem to be created in the LibraryFragment and the HomeFragment
 *
 * Delete if this isn't necessary
 *
 * Actually its used in SpecificSermonListFragment
 */
public class SermonAdapter extends ArrayAdapter<Sermon>
{
    Activity mActivity;
    String TAG = "SermonAdapter";

    public SermonAdapter(ArrayList<Sermon> sermons, Activity callingActivity)
    {
        super(callingActivity, 0, sermons);
        mActivity = callingActivity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        //If we weren't given a view, inflate one
        if(convertView == null)
        {
            convertView = mActivity.getLayoutInflater().inflate(R.layout.list_item_sermon, null);
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

        convertView.findViewById(R.id.card_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //New Activity Stuff
                Sermon s = Constants.fullSermonList.get(0);
                Intent i = new Intent(getContext(), MediaActivity.class);
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

                getContext().startActivity(i);
            }
        });


        return convertView;

    }
}
