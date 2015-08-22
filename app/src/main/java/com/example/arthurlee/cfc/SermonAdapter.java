package com.example.arthurlee.cfc;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by arthurlee on 8/19/15.
 */
public class SermonAdapter extends ArrayAdapter<Sermon>
{
    Activity mActivity;

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

        //buffering is called secondary progress


            /*
            DateFormat simpleDate = new SimpleDateFormat("MMM dd, yyyy");
            String formattedDate = simpleDate.format(s.getDate());
            dateTextView.setText(formattedDate);
            */
        return convertView;

    }
}
