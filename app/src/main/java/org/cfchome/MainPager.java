package org.cfchome;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.amazonmobileanalytics.InitializationException;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.MobileAnalyticsManager;

import java.util.ArrayList;


/**
 * This is the starting Activity for the Application. It's called MainPager because the app used to
 * use pagers but i was too lazy to rename the file.
 *
 * To detect if the application is in view, this class and the MediaActivity class both set a
 * global variable at onPause() and onResume().
 */

//public class MainPager extends ActionBarActivity
public class MainPager extends AppCompatActivity
{
    private String TAG = "Main Pager";
    public static MobileAnalyticsManager analytics;

    // fragmentManagers are used to switch between fragments in a single activity
    FragmentManager fragmentManager;

    // listen for changes in wifi or cellular signals
    NetworkStateReceiver networkStateReceiver;

    //slide out bar stuff
    //-------------------------------------------------------------------
    ListView mDrawerList;
    RelativeLayout mDrawerPane;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();
    //-------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

       Log.d(TAG, "onCreate called ---------------------");

        /**
         * Alex's mobile analytics account number: 8415647aa5814de8b7c14b02607164b7
         * Alex's general cognito identity pool id: us-east-1:473ecee9-e260-47de-a713-4593e7f8ddc4
         * My mobile analytics account number: 9d2215ddf13640a3a131bf5a821c57f0
         */


        //Set up Amazon Mobile Analytics   -> might wanna move this into after checking if network is available
        try {
            analytics = MobileAnalyticsManager.getOrCreateInstance(
                    this.getApplicationContext(),
                    "8415647aa5814de8b7c14b02607164b7", //Amazon Mobile Analytics App ID
                    "us-east-1:473ecee9-e260-47de-a713-4593e7f8ddc4" //Amazon Cognito Identity Pool ID
            );
        } catch(InitializationException ex) {
            Log.e(this.getClass().getName(), "Failed to initialize Amazon Mobile Analytics", ex);
        }


        // set global var to say app is viewable
        Constants.viewable = true;

        // initialize pastoral staff array
        if( Constants.pastoral_staff.size() == 0 )
        {
            Constants.pastoral_staff.add("Rev. Min Chung");
            Constants.pastoral_staff.add("Rev. KJ Kim");
            Constants.pastoral_staff.add("Rev. David Kang");
            Constants.pastoral_staff.add("Pastor Sean Lee");
            Constants.pastoral_staff.add("Pastor Tony Thomas");
            Constants.pastoral_staff.add("Pastor Jim Han");
        }


        // start up HomeFragment
        if( savedInstanceState == null )
        {
            HomeFragment homeFragment = new HomeFragment();
            fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.mainContent, homeFragment)
                    .commit();
        }

        // Load up the sermons if there is internet connection
        if( isNetworkAvailable() )
        {
            if( Constants.fullSermonList.size() == 0 )
            {
                LocalJSONManager sermonManager = new LocalJSONManager(this);
                sermonManager.parseLocalJSON();
            }

            SermonDownloader sermonDownloader = new SermonDownloader();
            sermonDownloader.checkForNewSermons(this);

            AnnouncementDownloader announcementDownloader = new AnnouncementDownloader();
            announcementDownloader.getAnnouncements(this);

            Constants.number_of_updates ++;
            Constants.failed_update = false;
        }
        else
        {
            Toast.makeText(MainPager.this, "no connection", Toast.LENGTH_LONG).show();
            Constants.no_internet_connection = true;
            Constants.failed_update = true;
        }


        //Set up view layouts
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //set up Navigation Items (side menu)
        mNavItems.add(new NavItem("Home", "What's new", R.drawable.alpha_block));
        mNavItems.add(new NavItem("Library", "Listen online", R.drawable.alpha_block));
        mNavItems.add(new NavItem("About", "Learn about our church", R.drawable.alpha_block));

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);

        mDrawerPane = (RelativeLayout)findViewById(R.id.drawerPane);
        mDrawerList = (ListView)findViewById(R.id.navList);

        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
        mDrawerList.setAdapter(adapter);


        // Drawer Item click listeners
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFromDrawer(position);
            }
        });


        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close)
        {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                //Log.d(TAG, "onDrawerClosed: " + getTitle());

                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        Constants.doneUpdatingAnnouncements = false;

        Log.d(TAG, "sermon arraylist count: " + Constants.fullSermonList.size() );
        Log.d(TAG, "announcement arraylist count: " + Constants.announcementsList.size() );
    }

    /**
     * updateHomeView()
     * is used to update the home view after sermons and announcements are done
     * downloading and is called in AnnouncementDownloader and SermonDownloader
     *
     * to remove the spinny wheel to indicate the Announcements loading, I put constant that indicates
     * if finished loading or not and is checked in onCreateView() in HomeFragment
     *
     * if this function is called while the app is in the background, it will set up a pending_homeview_update
     * flag and will automatically update the fragment the next time the app comes into view
     */
    public void updateHomeView()
    {
        if( Constants.homefragment_visible )
        {
            fragmentManager = getFragmentManager();
            Fragment homeFragment;
            homeFragment = new HomeFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.mainContent, homeFragment)
                    .commit();

            Log.d(TAG, "sermon arraylist count: " + Constants.fullSermonList.size());
            Log.d(TAG, "announcement arraylist count: " + Constants.announcementsList.size() );
        }
        else
        {
            Constants.pending_homeview_update = true;
        }
        if( Constants.no_internet_connection )
        {
            Toast.makeText(MainPager.this, "no connection", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * check if there is a internet connection before trying to update anything
     *
     * @return
     * state of connection
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }



    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();

    }

    @Override
    public void onStart()
    {
        super.onStart();

        // set up network state change listener. Will be used for sudden wifi access after initial update failure
        IntentFilter filter = new IntentFilter(NetworkStateReceiver.NETWORK_CONNECTED);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        networkStateReceiver = new NetworkStateReceiver();
        registerReceiver(networkStateReceiver, filter);


    }

    @Override
    public void onStop()
    {
        super.onStop();

        if( networkStateReceiver != null )
            unregisterReceiver( networkStateReceiver );

        Log.d(TAG, "onStop called ------------");
    }

    @Override
    public void onBackPressed()
    {
        if( getFragmentManager().getBackStackEntryCount() != 0 )
            getFragmentManager().popBackStack();


        // close app if there's no more fragments in the back stack
        else
        {
            SermonPlayer.get(null, null).prepareForClose();
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_pager, menu);
        //setTitle("CFC Home");

        return true;
    }

    @Override
    public void onPause()
    {
        super.onPause();

        //Log.d("MainPager", "onPause was called ---------");
        Constants.viewable = false;
    }
    @Override
    public void onResume()
    {
        super.onResume();

        //Log.d("MainPager", "onResume was called ---------");
        Constants.viewable = true;

        if( Constants.failed_update && isNetworkAvailable() )
        {
            SermonDownloader sermonDownloader = new SermonDownloader();
            sermonDownloader.checkForNewSermons(this);

            AnnouncementDownloader announcementDownloader = new AnnouncementDownloader();
            announcementDownloader.getAnnouncements(this);

            Constants.number_of_updates ++;
            Constants.failed_update = false;
        }

        if( Constants.pending_homeview_update )
        {
            fragmentManager = getFragmentManager();
            Fragment homeFragment;
            homeFragment = new HomeFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.mainContent, homeFragment)
                    .addToBackStack("main")
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        /**
         * Handle action bar item clicks here. The action bar will
         * automatically handle clicks on the Home/Up button, so long
         * as you specify a parent activity in AndroidManifest.xml.
         */
        int id = item.getItemId();

        /**
         * Pass the event to ActionBarDrawerToggle
         * If it returns true, then it has handled
         * the nav drawer indicator touch event
         */
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_now_playing)
        {

            if(!SermonPlayer.get(null, null).isActive())
            {
                Toast.makeText(MainPager.this, "Nothing Playing", Toast.LENGTH_SHORT).show();
            }

            else
            {
                Intent i = new Intent(this, MediaActivity.class);
                i.putExtra(MediaActivity.EXTRA_PASTOR_NAME, Constants.nowPlayingPastor);
                i.putExtra(MediaActivity.EXTRA_MP3URL, Constants.nowPlayingUrl);
                i.putExtra(MediaActivity.EXTRA_SERMON_DATE, Constants.nowPlayingDate);
                i.putExtra(MediaActivity.EXTRA_SERMON_TITLE, Constants.nowPlayingTitle);
                i.putExtra(MediaActivity.EXTRA_SERMON_SCRIPTURE, Constants.nowPlayingPassage);

                startActivity(i);

            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private void selectItemFromDrawer(int position) {

        Fragment aboutFragment;
        Fragment libraryFragment;
        Fragment homeFragment;

        fragmentManager = getFragmentManager();


        switch (position)
        {
            case 0:
                homeFragment = new HomeFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.mainContent, homeFragment)
                        .addToBackStack("main")
                        .commit();
                break;

            case 1:
                libraryFragment = new LibraryFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.mainContent, libraryFragment)
                        .addToBackStack("main")
                        .commit();
                break;

            case 2:
                aboutFragment = new CFCabout();
                fragmentManager.beginTransaction()
                        .replace(R.id.mainContent, aboutFragment)
                        .addToBackStack("main")
                        .commit();
                break;
        }



        mDrawerList.setItemChecked(position, true);
        setTitle(mNavItems.get(position).mTitle);

        // Close the drawer
        mDrawerLayout.closeDrawer(mDrawerPane);
    }


    //inner classes
    //-----------------------------------------------------------------------------

    class NavItem {
        String mTitle;
        String mSubtitle;
        int mIcon;

        public NavItem(String title, String subtitle, int icon) {
            mTitle = title;
            mSubtitle = subtitle;
            mIcon = icon;
        }
    }

    class DrawerListAdapter extends BaseAdapter {

        Context mContext;
        ArrayList<NavItem> mNavItems;

        public DrawerListAdapter(Context context, ArrayList<NavItem> navItems) {
            mContext = context;
            mNavItems = navItems;
        }

        @Override
        public int getCount() {
            return mNavItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mNavItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.drawer_item, null);
            }
            else {
                view = convertView;
            }

            TextView titleView = (TextView) view.findViewById(R.id.title);
            TextView subtitleView = (TextView) view.findViewById(R.id.subTitle);
            ImageView iconView = (ImageView) view.findViewById(R.id.icon);

            titleView.setText( mNavItems.get(position).mTitle );
            subtitleView.setText( mNavItems.get(position).mSubtitle );
            iconView.setImageResource(mNavItems.get(position).mIcon);

            return view;
        }
    }



}



