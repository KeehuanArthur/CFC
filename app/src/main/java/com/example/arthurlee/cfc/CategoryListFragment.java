package com.example.arthurlee.cfc;

import android.app.FragmentManager;
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

            case("Speaker"):
                populateSpeaker();
                break;

            case("Series"):
                populateSeries();
                break;

            case("Events"):
                populateEvents();
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
        Constants.searchFor = mCategoryItemsAdapter.getItem(position).toString();

        Log.d("CategoryList", Constants.searchFor);

        FragmentManager fragmentManager = getFragmentManager();

        Log.d("CategoryList", "Click registered");

        ListFragment specificSermonListFragment = new SpecificSermonListFragment();

        fragmentManager.beginTransaction()
                .replace(R.id.mainContent, specificSermonListFragment)
                .addToBackStack("Specific Category")
                .commit();


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

    private void populateSpeaker()
    {
        mCategoryItemsAdapter.add("Rev. Min Chung");
        mCategoryItemsAdapter.add("Rev. KJ Kim");
        mCategoryItemsAdapter.add("Rev. David Kang");
        mCategoryItemsAdapter.add("Pastor Sean Lee");
        mCategoryItemsAdapter.add("Pastor Tony Thomas");
        mCategoryItemsAdapter.add("Pastor Jim Han");
        mCategoryItemsAdapter.add("Guest Speakers");
    }

    private void populateSeries()
    {
        String[] booksOfBible = { "Genesis", "Exodus", "Leviticus", "Numbers", "Deuteronomy", "Joshua", "Judges",
                                    "Ruth", "1 Samuel", "2 Samuel", "1 Kings", "2 Kings", "1 Chronicles",
                                    "2 Chronicles", "Ezra", "Nehemiah", "Esther", "Job", "Psalms",
                                    "Proverbs", "Ecclesiastes", "Song of Solomon", "Isaiah", "Jeremiah",
                                    "Lamentations", "Ezekiel", "Daniel", "Hosea", "Joel", "Amos", "Obadiah",
                                    "Jonah", "Micah", "Nahum", "Habakkuk", "Zephaniah", "Haggai", "Zechariah",
                                    "Malachi", "Matthew", "Mark", "Luke", "John", "Acts", "Romans", "1 Corinthians",
                                    "2 Corinthians", "Galatians", "Ephesians", "Philippians", "Colossians",
                                    "1 Thessalonians", "2 Thessalonians", "1 Timothy", "2 Timothy", "Titus",
                                    "Philemon", "Hebrew", "James", "1 Peter", "2 Peter", "1 John", "2 John",
                                    "3 John", "Jude", "Revelation"
        };

        for(int i = 0; i < 66; i++)
        {
            mCategoryItemsAdapter.add(booksOfBible[i]);
        }
    }

    private void populateEvents()
    {
        for( int i = 0; i < Constants.eventList.size(); i++ )
        {
            mCategoryItemsAdapter.add(Constants.eventList.get(i));
        }
    }

}
