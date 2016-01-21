package com.example.arthurlee.cfchome;

import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by arthurlee on 8/18/15.
 */
public class SpecificSermonListFragment extends ListFragment
{
    private ArrayList<Sermon> mSermonArrayList;
    private SermonAdapter mSermonAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mSermonArrayList = new ArrayList<>();
        mSermonAdapter = new SermonAdapter(mSermonArrayList, getActivity());
        setListAdapter(mSermonAdapter);

        getActivity().setTitle(Constants.searchFor);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        getListView().setDivider(null);

        switch (Constants.categoryName)
        {
            case("Year"):
                searchByDate(Constants.searchFor);
                break;

            case("Speaker"):
                searchBySpeaker(Constants.searchFor);
                break;

            case("Event"):
                Log.d("Specific sermon", "Event selected: " + Constants.searchFor);
                searchByEvent(Constants.searchFor);
                break;

            case("Series"):
                Log.d("Specific sermon", "Series selected selected" + Constants.searchFor);
                searchBySeries(Constants.searchFor);
                break;
        }
    }

    /* the click listener is handled by the individual sermon adapters in SermonAdapter. the reason why is because
       having the listener in the ListAdapter makes the card view click animation incorrect
    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        Sermon s = mSermonArrayList.get(position);

        if( s.getMp3url().isEmpty() )
        {
            Toast.makeText(getActivity(), "No mp3 file found",
                    Toast.LENGTH_LONG).show();
        }
        else
        {
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
    }
       */

    private void searchByDate(String searchFor)
    {

        DateFormat dateFormat = new SimpleDateFormat("yyyy");
        int searchYear = Integer.parseInt(searchFor);
        int sermonYear;
        Date sermonDate;

        for( int i = 0; i < Constants.fullSermonList.size(); i++ )
        {
            sermonDate = Constants.fullSermonList.get(i).getDate();
            sermonYear = Integer.parseInt(dateFormat.format(sermonDate));

            if ( searchYear == sermonYear )
            {
                mSermonAdapter.add(Constants.fullSermonList.get(i));
            }
        }

    }

    private void searchBySpeaker(String searchFor)
    {
        for( int i = 0; i < Constants.fullSermonList.size(); i++ )
        {
            Sermon s = Constants.fullSermonList.get(i);

            if( !searchFor.equals("Guest Speakers") )
            {
                if( (s.getPastor()).equals(searchFor) )
                {
                    mSermonAdapter.add(s);
                }
            }
            else
            {
                if( !Constants.pastoral_staff.contains(s.getPastor()) )
                {
                    mSermonAdapter.add(s);
                }
            }
        }
    }

    public void searchByEvent(String searchFor)
    {
        for( int i = 0; i < Constants.fullSermonList.size(); i++ )
        {
            Sermon s = Constants.fullSermonList.get(i);
            if( s.getEvent() != null && (s.getEvent()).equals(searchFor))
            {
                mSermonAdapter.add(s);
            }
        }
    }

    public void searchBySeries(String searchFor)
    {
        for( int i = 0; i < Constants.fullSermonList.size(); i++ )
        {
            Sermon s = Constants.fullSermonList.get(i);

            if( s.getSeries() != null && (s.getSeries()).equals(searchFor) )
            {
                mSermonAdapter.add(s);
            }
        }
    }

}
