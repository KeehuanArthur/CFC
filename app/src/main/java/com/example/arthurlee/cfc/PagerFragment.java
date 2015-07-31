package com.example.arthurlee.cfc;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by arthurlee on 7/3/15.
 */
public class PagerFragment extends FragmentPagerAdapter
{
    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "About", "Sermons" };
    private Context context;
    private SermonListFragment sermonList = new SermonListFragment();
    private Activity parentActivty;


    public SermonListFragment getSermonList() {
        return sermonList;
    }

    public PagerFragment(FragmentManager fm, Context context, Activity parentActivty)
    {
        super(fm);
        this.context = context;
        this.parentActivty = parentActivty;
        sermonList.setParentContext(parentActivty);
        //sermonList = new SermonListFragment();

    }

    //create list of sermons singleton on create
    /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    */



    @Override
    public int getCount()
    {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position)
    {
        if( position == 0)
            return CFCabout.newInstance(position);
        else
        {
            return new Fragment();
        }
    }


    @Override
    public CharSequence getPageTitle(int position)
    {
        // Generate title based on item position
        return tabTitles[position];
    }
}
