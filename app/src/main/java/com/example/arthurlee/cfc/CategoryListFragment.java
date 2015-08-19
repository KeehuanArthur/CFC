package com.example.arthurlee.cfc;

import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by arthurlee on 8/17/15.
 */
public class CategoryListFragment extends ListFragment
{
    ArrayAdapter mCategoryItemsAdapter;
    ArrayList<String> mCategoryItemsList;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Log.d("Category Fragment", "starting fragment");


        mCategoryItemsList = new ArrayList<String>();
        mCategoryItemsAdapter = new ArrayAdapter(getActivity().getBaseContext(), R.layout.list_item_single_text_view, R.id.single_text_view);

        switch (Constants.categoryName)
        {
            case("Year"):
                populateDates();
                break;

            default:
                mCategoryItemsAdapter.add("test 1");
                mCategoryItemsAdapter.add("test 2");
                break;
        }



        setListAdapter(mCategoryItemsAdapter);


    }



    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {


    }


    private void populateDates()
    {
        //oldest recorded sermon is from year 2000
        Date curDate = new Date();
        DateFormat year = new SimpleDateFormat("yyyy");

        int curYear = Integer.parseInt(year.format(curDate));

        while(curYear >= 2000)
        {
            mCategoryItemsAdapter.add(Integer.toString(curYear));
            curYear --;
        }

    }

}
