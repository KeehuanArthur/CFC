package com.example.arthurlee.cfc;

import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by arthurlee on 1/14/16.
 */
public class temp_nested_viewfragment extends Fragment
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.temp_home, parent, false);

        LinearLayout l = (LinearLayout)v.findViewById(R.id.test_child_viewpager);

        ViewPager vp = (ViewPager)v.findViewById(R.id.test_viewpager);

        PagerAdapter pa = new ScreenSlidePagerAdapter(getFragmentManager());

        vp.setAdapter(pa);

        return v;
    }


    private class ScreenSlidePagerAdapter extends FragmentPagerAdapter
    {
        public ScreenSlidePagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return new ScreenSlidePageAdapter();
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
