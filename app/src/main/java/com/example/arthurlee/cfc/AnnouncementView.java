package com.example.arthurlee.cfc;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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

        /**
         * because the sermons my not be all loaded into the arraylist oncreateview(), there must be a
         * check or there will be IndexOutOfBoundsException
         */
        //if(Constants.announcementsTotal != 0)
        if( Constants.announcementsList.size() != 0 )
        {
            announcement_image.setImageBitmap(Constants.announcementsList
                    .get(announcementNumb % Constants.announcementsTotal).getImage());
        }

        return v;
    }


}
