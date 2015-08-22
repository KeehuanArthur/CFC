package com.example.arthurlee.cfc;

import android.app.ListFragment;
import android.os.Bundle;

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

        switch (Constants.categoryName)
        {
            case("Year"):
                searchByDate();
                break;
        }
    }


    private void searchByDate()
    {

        DateFormat dateFormat = new SimpleDateFormat("yyyy");
        int searchYear = Integer.parseInt(Constants.searchFor);
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

}