package com.example.arthurlee.cfc;

import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

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
                searchByEvent(Constants.searchFor);
                break;

            case("Series"):
                searchBySeries(Constants.searchFor);
                break;
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        
    }


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
                Log.d("Specific Sermon", "added sermon");
            }
        }

    }

    private void searchBySpeaker(String searchFor)
    {
        for( int i = 0; i < Constants.fullSermonList.size(); i++ )
        {
            Sermon s = Constants.fullSermonList.get(i);
            if( (s.getPastor()).equals(searchFor))
            {
                mSermonAdapter.add(s);
                Log.d("Specific Sermon", "added sermon");

            }
        }
    }

    public void searchByEvent(String searchFor)
    {
        for( int i = 0; i < Constants.fullSermonList.size(); i++ )
        {
            Sermon s = Constants.fullSermonList.get(i);
            if( (s.getEvent()).equals(searchFor))
            {
                mSermonAdapter.add(s);
                Log.d("Specific Sermon", "added sermon");
            }
        }
    }

    public void searchBySeries(String searchFor)
    {
        for( int i = 0; i < Constants.fullSermonList.size(); i++ )
        {
            Sermon s = Constants.fullSermonList.get(i);

        }
    }

}
