package com.example.arthurlee.cfc;

import android.content.Intent;
import android.os.Bundle;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by arthurlee on 8/21/15.
 */
public class LibraryFragment extends ListFragment
{
    private LibraryAdapter mLibraryAdapter;
    private ArrayList<Sermon> mSermonArrayList;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mSermonArrayList = new ArrayList<>();
        mLibraryAdapter = new LibraryAdapter(mSermonArrayList);

        /**
         * put in 4 blank sermons into the LibraryAdapter. the first 4 items in the LibraryAdapter
         * are over written and the space is used for category names
         */
        for( int i = 0; i < 4; i++)
        {
            mLibraryAdapter.add(new Sermon());
        }
        setListAdapter(mLibraryAdapter);
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getListView().setDivider(null);

        for(int i = 0; i < 25; i ++)
        {
            mLibraryAdapter.add(Constants.fullSermonList.get(i));
        }
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        if( position < 4 )
        {
            startCategoryFragment(position);
        }
        else
        {
            startSermonPlayer(position);
        }
    }

    private void startCategoryFragment(int categoryNumber)
    {
        FragmentManager fragmentManager = getFragmentManager();

        switch (categoryNumber)
        {
            case 0:
                Constants.categoryName = "Year";
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

        ListFragment categoryListFragment = new CategoryListFragment();

        fragmentManager.beginTransaction()
                .replace(R.id.mainContent, categoryListFragment)
                .addToBackStack("Categories_List")
                .commit();

    }

    private void startSermonPlayer(int sermonNumber)
    {
        Log.d("starting Sermon Player", "launching form LIBRARY FRAGMENT!");

        //New Activity Stuff
        Sermon s = mLibraryAdapter.getItem(sermonNumber);
        Intent i = new Intent(getActivity(), MediaActivity.class);
        i.putExtra(MediaActivity.EXTRA_PASTOR_NAME, s.getPastor());
        i.putExtra(MediaActivity.EXTRA_MP3URL, s.getMp3url());
        i.putExtra(MediaActivity.EXTRA_SERMON_DATE, s.getSDate());
        i.putExtra(MediaActivity.EXTRA_SERMON_TITLE, s.getTitle());
        i.putExtra(MediaActivity.EXTRA_SERMON_SCRIPTURE, s.getScripture());

        startActivity(i);
    }



    public class LibraryAdapter extends ArrayAdapter<Sermon>
    {

        public LibraryAdapter(ArrayList<Sermon> sermons)
        {
            super(getActivity(), 0, sermons);
        }

        public View getCategoryView(int position, View convertView)
        {
            TextView categoryName = (TextView)convertView.findViewById(R.id.list_item_category_name);

            switch (position)
            {
                case 0:
                    categoryName.setText("Year");
                    break;
                case 1:
                    categoryName.setText("Speaker");
                    break;
                case 2:
                    categoryName.setText("Event");
                    break;
                case 3:
                    categoryName.setText("Series");
                    break;
            }

            return convertView;

        }

        public View getSermonView(int position, View convertView)
        {
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

        /**
         * this function tells android that there are 2 kinds of items displayed, sermons and categories
         */
        @Override
        public int getViewTypeCount()
        {
            return 2;
        }

        @Override
        public int getItemViewType(int position)
        {
            if( position < 4 )
                return 0;
            else
                return 1;
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            //there are 4 categories and the rest are sermons

            //If we weren't given a view, inflate one
            if(convertView == null)
            {
                if( getItemViewType(position) == 0 )
                {
                    convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_categories, null);
                }
                else
                {
                    convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_sermon, null);
                }
            }


            //for categories return general text view
            if(position < 4)
            {
                return getCategoryView(position, convertView);
            }
            else
            {
                //return getSermonView(position, convertView);
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


}


