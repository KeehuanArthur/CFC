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
 * This is just a single view for one of the announcements in the AnnouncementPager
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


        announcement_image.setImageBitmap(Constants.announcementsList.get(announcementNumb).getImage());

        return v;
    }


}
