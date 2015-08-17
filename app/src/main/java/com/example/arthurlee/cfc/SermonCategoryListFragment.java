package com.example.arthurlee.cfc;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mSermonCategoriesList = new ArrayList<String>();
        mRecentSermonsList = new ArrayList<Sermon>();
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

        mSermonCategoriesAdapter.add("Year");
        mSermonCategoriesAdapter.add("Speaker");
        mSermonCategoriesAdapter.add("Event");
        mSermonCategoriesAdapter.add("Series");



        //set a listener to the category items


        //for the sermons List View
        //note: this uses custom List View
        mRecent = (ListView) v.findViewById(R.id.categories_latest_sermons);

        mRecentSermonAdapter = new SermonAdapter(mRecentSermonsList);

        mRecent.setAdapter(mRecentSermonAdapter);

        Sermon test = new Sermon();
        test.setTitle("Test");
        test.setSDate("test Date");
        test.setPastor("Pastor name");
        test.setScripture("Test");

        mRecentSermonAdapter.add(test);

        return v;
    }



    public class SermonAdapter extends ArrayAdapter<Sermon> {
        public SermonAdapter(ArrayList<Sermon> sermons) {
            super(getActivity(), 0, sermons);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //If we weren't given a view, inflate one
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_sermon_no_card, null);
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
