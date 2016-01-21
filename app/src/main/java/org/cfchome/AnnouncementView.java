package org.cfchome;

import android.app.Fragment;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;

/**
 * Created by arthurlee on 1/14/16.
 *
 * This is just a single view for one of the announcements in the AnnouncementPager. It knows which
 * announcmenet to show by looking at the global variable Constants.curAnnouncement and increments
 * that number to indicate that that sermon is shown already so there are no duplicate announcements
 */

public class AnnouncementView extends Fragment
{
    ImageView announcement_image;
    TextView announcement_title, announcement_date, announcement_time, announcement_location;
    int announcementNumb;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        announcementNumb = Constants.curAnnouncement;
        Constants.curAnnouncement ++;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(
                R.layout.announcement_view, container, false);

        announcement_image = (ImageView)v.findViewById(R.id.announcement_image);
        announcement_title = (TextView)v.findViewById(R.id.announcement_title);
        announcement_date = (TextView)v.findViewById(R.id.announcement_date);
        announcement_time = (TextView)v.findViewById(R.id.announcement_time);
        announcement_location = (TextView)v.findViewById(R.id.announcement_location);

        /**
         * because the sermons my not be all loaded into the arraylist oncreateview(), there must be a
         * check or there will be IndexOutOfBoundsException
         */
        //if(Constants.announcementsTotal != 0)
        if( Constants.announcementsList.size() != 0 )
        {
            Announcement curAnnouncement = Constants.announcementsList.get(announcementNumb);
            DateFormat dateFormat = new DateFormat();
            dateFormat.format("MMM-DD-YYYY", curAnnouncement.getDate());

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd  yyyy");

            announcement_image.setImageBitmap(curAnnouncement.getImage());
            announcement_title.setText(curAnnouncement.getTitle());
            announcement_date.setText(simpleDateFormat.format(curAnnouncement.getDate()));
            announcement_time.setText(curAnnouncement.getTime());
            announcement_location.setText(curAnnouncement.getLocation());
        }

        return v;
    }



}
