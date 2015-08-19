package com.example.arthurlee.cfc;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by arthurlee on 8/16/15.
 */
public class SermonCategoryListFragment extends Fragment
{
    private ArrayAdapter<String> mSermonCategoriesAdapter;
    private ArrayList<String> mSermonCategoriesList;

    private SermonAdapter mRecentSermonAdapter;
    private ArrayList<Sermon> mRecentSermonsList;

    private ListView mCategories;
    private ListView mRecent;

    ListFragment categoryListFragment;
    FragmentManager fragmentManager;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mSermonCategoriesList = new ArrayList<>();
        mRecentSermonsList = new ArrayList<>();
        //mSermonCategoriesAdapter = new ArrayAdapter<String>(getActivity().getBaseContext(), 0);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.categories_fragment, parent, false);



        //for the categories List view
        mCategories = (ListView) v.findViewById(R.id.categories_list);

        mSermonCategoriesAdapter = new ArrayAdapter<String>(getActivity().getBaseContext(), R.layout.list_item_categories, R.id.list_item_category_name);

        mCategories.setAdapter(mSermonCategoriesAdapter);

        mSermonCategoriesAdapter.add("Year");       //0
        mSermonCategoriesAdapter.add("Speaker");    //1
        mSermonCategoriesAdapter.add("Event");      //2
        mSermonCategoriesAdapter.add("Series");     //3



        //for the sermons List View----------------------------------------------------------------
        //note: this uses custom List View
        mRecent = (ListView) v.findViewById(R.id.categories_latest_sermons);

        mRecentSermonAdapter = new SermonAdapter(mRecentSermonsList);

        mRecent.setAdapter(mRecentSermonAdapter);


        for( int i = 0; i < 24; i++ )
        {
            mRecentSermonAdapter.add(Constants.fullSermonList.get(i));
        }

        mRecent.setDivider(null);



        return v;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        AdapterView.OnItemClickListener clickedCategory = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Log.d("SermonCategory", "clicked item");


                fragmentManager = getFragmentManager();

                switch (position)
                {
                    case 0:
                        Constants.categoryName = "Year";
                        Log.d("sermonCategory","clicked year");
                        break;

                    case 1:
                        Constants.categoryName = "Speaker";
                        break;

                    case 2:
                        Constants.categoryName = "Events";
                        break;

                    case 3:
                        Constants.categoryName = "Series";
                        break;
                }

                categoryListFragment = new CategoryListFragment();

                fragmentManager.beginTransaction()
                        .replace(R.id.mainContent, categoryListFragment)
                        .addToBackStack("Categories_List")
                        .commit();

            }
        };
        mCategories.setOnItemClickListener(clickedCategory);



        AdapterView.OnItemClickListener clickedLatestSermon = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d("SermonCategory", "Sermon Clicked");

                //New Activity Stuff
                Sermon s = mRecentSermonAdapter.getItem(position);
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

        };

        mRecent.setOnItemClickListener(clickedLatestSermon);





    }

    public class SermonAdapter extends ArrayAdapter<Sermon> {
        public SermonAdapter(ArrayList<Sermon> sermons) {
            super(getActivity(), 0, sermons);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //If we weren't given a view, inflate one
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_sermon, null);
            }

            //Configure view for Sermon
            final Sermon s = getItem(position);

            TextView titleTextView = (TextView) convertView.findViewById(R.id.sermon_list_item_titleTextView);
            titleTextView.setText(s.getTitle());

            TextView pastorTextView = (TextView) convertView.findViewById(R.id.sermon_list_item_pastorName);
            pastorTextView.setText(s.getPastor());

            TextView dateTextView = (TextView) convertView.findViewById(R.id.sermon_list_item_dateTextView);
            dateTextView.setText(s.getSDate());

            TextView passageTextView = (TextView) convertView.findViewById(R.id.sermon_list_item_passage);
            passageTextView.setText(s.getScripture());


            return convertView;

        }
    }
}
