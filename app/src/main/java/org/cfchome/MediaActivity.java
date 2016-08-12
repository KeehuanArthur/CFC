package org.cfchome;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.amazonmobileanalytics.AnalyticsEvent;


/**
 * Created by arthurlee on 7/25/15.
 *
 *
 * This is the view that is displayed when a user clicks on a sermon
 */
public class MediaActivity extends FragmentActivity {

    private String TAG = "MediaActivity";

    private ImageButton mPlayButton;
    private TextView mTitle;
    private TextView mSpeaker;
    private TextView mPassage;
    private TextView mCurrentTime;
    private TextView mTotalTime;
    //private SeekBar mSeekBar;
    private ImageView mPastorPhoto;
    private PhoneStateListener mPhoneStateListener;


    private AudioPlayer mMediaPlayer = new AudioPlayer();
    private Context mAppContext;
    private Handler seekHandler = new Handler();

    public static final String EXTRA_MP3URL = "com.cfc.mp3_url";
    public static final String EXTRA_PASTOR_NAME = "com.cfc.pastorName";
    public static final String EXTRA_SERMON_DATE = "com.cfc.sermonDate";
    public static final String EXTRA_SERMON_TITLE = "com.cfc.sermonTitle";
    public static final String EXTRA_SERMON_SCRIPTURE = "com.cfc.sermonScripture";


    private String mMP3URL;
    private String mSpeakerName;
    private String mSermonDate;
    private String mSermonTitle;
    private String mSermonScripture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_top);
        setContentView(R.layout.activity_player);


        mMP3URL = getIntent().getStringExtra(EXTRA_MP3URL);

        mSpeakerName = getIntent().getStringExtra(EXTRA_PASTOR_NAME);
        mPastorPhoto = (ImageView)findViewById(R.id.pastorPhoto);
        mSermonTitle = getIntent().getStringExtra(EXTRA_SERMON_TITLE);
        mSermonScripture = getIntent().getStringExtra(EXTRA_SERMON_SCRIPTURE);

        //Set Global Vars
        Constants.nowPlayingTitle = mSermonTitle;
        Constants.nowPlayingPastor = mSpeakerName;
        Constants.nowPlayingPassage = mSermonScripture;
        //Constants.nowPlayingDate = s.getSDate();
        Constants.nowPlayingUrl = mMP3URL;


        /**
         * when app returns from onStop() after a while, MainPager.analytics seems to be null b/c we only need to log
         * this entry once anyways, just use the analytics manager once
         */
        if( MainPager.analytics != null )
        {
            AnalyticsEvent testEvent = MainPager.analytics.getEventClient().createEvent("test")
                    .withAttribute("test title", mSermonTitle);
            MainPager.analytics.getEventClient().recordEvent(testEvent);
            MainPager.analytics.getEventClient().submitEvents();
        }



        // set up text views
        mTitle = (TextView)findViewById(R.id.media_title);
        mSpeaker = (TextView)findViewById(R.id.sermon_player_speaker);
        mPassage = (TextView)findViewById(R.id.sermon_player_passage);
        mTitle.setText(mSermonTitle);
        mSpeaker.setText(mSpeakerName);
        mPassage.setText(mSermonScripture);




        Drawable new_image;
        switch (mSpeakerName)
        {
            case "Rev. Min Chung":
                new_image = ContextCompat.getDrawable(this, R.drawable.pmin);
                mPastorPhoto.setImageDrawable(new_image);
                break;
            case "Pastor KJ Kim":
                new_image = ContextCompat.getDrawable(this, R.drawable.pkj);
                mPastorPhoto.setImageDrawable(new_image);
                break;
            case "Rev. KJ Kim":
                new_image = ContextCompat.getDrawable(this, R.drawable.pkj);
                mPastorPhoto.setImageDrawable(new_image);
                break;
            case "Pastor Jim Han":
                new_image = ContextCompat.getDrawable(this, R.drawable.pjim);
                mPastorPhoto.setImageDrawable(new_image);
                break;
            case "Rev. Jim Han":
                new_image = ContextCompat.getDrawable(this, R.drawable.pjim);
                mPastorPhoto.setImageDrawable(new_image);
                break;
            case "Pastor Tony Thomas":
                new_image = ContextCompat.getDrawable(this, R.drawable.ptony);
                mPastorPhoto.setImageDrawable(new_image);
                break;
            case "Rev. Tony Thomas":
                new_image = ContextCompat.getDrawable(this, R.drawable.ptony);
                mPastorPhoto.setImageDrawable(new_image);
                break;
            case "Pastor David Kang":
                new_image = ContextCompat.getDrawable(this, R.drawable.pdave);
                mPastorPhoto.setImageDrawable(new_image);
                break;
            case "Rev. David Kang":
                new_image = ContextCompat.getDrawable(this, R.drawable.pdave);
                mPastorPhoto.setImageDrawable(new_image);
                break;
            case "Pastor Sean Lee":
                new_image = ContextCompat.getDrawable(this, R.drawable.psean);
                mPastorPhoto.setImageDrawable(new_image);
                break;
            case "Rev. Sean Lee":
                new_image = ContextCompat.getDrawable(this, R.drawable.psean);
                mPastorPhoto.setImageDrawable(new_image);
                break;

            default:
                new_image = ContextCompat.getDrawable(this, R.drawable.guest_speaker);
                mPastorPhoto.setImageDrawable(new_image);
                break;
        }



        mPlayButton = (ImageButton)findViewById(R.id.media_play_button);
        if( !Constants.sermonPlayerPaused )
            mPlayButton.setBackgroundResource( R.drawable.pause_button );
        else
            mPlayButton.setBackgroundResource( R.drawable.play_button );

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SermonPlayer.get(MediaActivity.this, null).playPause(false, false);

                Context dropDownContext = SermonPlayer.get(null, null).getDropDownControlsContext();
                ((DropdownControls)dropDownContext).updateNotification();
            }
        });


        mCurrentTime = (TextView)findViewById(R.id.media_current_time);
        mTotalTime = (TextView)findViewById(R.id.media_total_time);


        /**
         * the seek bar is updated by the SermonPlayer class because the app needs to wait for
         * the MediaPlayer to buffer and go to the onPrepared state
         */
        final SeekBar mSeekBar = (SeekBar)findViewById((R.id.sermon_audio_seekBar));
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if( SermonPlayer.get(MediaActivity.this, null).isActive() )
                    SermonPlayer.get(MediaActivity.this, null).setPosition(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {

            }
        });

        mPhoneStateListener = new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    //Incoming call: Pause music
                    //SermonPlayer.get(MediaActivity.this, false).playPause(false, true);
                } else if(state == TelephonyManager.CALL_STATE_IDLE) {
                    //Not in call: Play music
                    //SermonPlayer.get(MediaActivity.this, false).playPause(true, false);
                } else if(state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    //A call is dialing, active or on hold
                    //SermonPlayer.get(MediaActivity.this, false).playPause(true, false);
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };
        TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if(mgr != null) {
            mgr.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }

        /**
         * Start the sermon player when the new activity is created
         * the sermon player class also controls the UI elements: seekbar, currenttime, totaltime
         *
         * There is also a loading circle in the place of the play button initially but once the sermon
         * is buffered enough, it is replaced with the play button
         */
        SermonPlayer.get(MediaActivity.this, null).play(mMP3URL, mSeekBar, mCurrentTime, mTotalTime, mPlayButton);

        if( !Constants.sermon_buffering )
        {
            enable_play_pause_button();
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();

        Constants.viewable = false;
    }
    @Override
    public void onResume()
    {
        super.onResume();

        Constants.viewable = true;
    }

    @Override
    public void onStop()
    {
        super.onStop();

        Log.d(TAG, "on stop called ----------");
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.abc_slide_in_top, R.anim.abc_slide_out_bottom);
    }

    /**
     * Initially the play_pause_button is invisible and there is only a buffering circle
     * this function removes the buffering circle and makes the play_pause_button visible
     */
    public void enable_play_pause_button()
    {
        mPlayButton.setVisibility(View.VISIBLE);
        RelativeLayout loading_circle = (RelativeLayout)findViewById(R.id.sermon_loading_circle);
        loading_circle.setVisibility(View.INVISIBLE);
    }

    /**
     * set up the notification controller. This function is called in SermonPlayer.
     *
     * This used to be in the onCreate portion of MediaActivity but there's case when user closes
     * the notification controller then presses play so this function should be called by the
     * play function of the SermonPlayer
     */
    public void activate_notification_controller()
    {
        final Context baseContext = getApplicationContext();
        Intent intent = new Intent( baseContext, DropdownControls.class );
        intent.putExtra(DropdownControls.ACTION_NOTIFICATION_EXTRA_TITLE, mSermonTitle);
        intent.putExtra(DropdownControls.ACTION_NOTIFICATION_EXTRA_PASTOR, mSpeakerName);
        intent.putExtra(DropdownControls.ACTION_NOTIFICATION_EXTRA_DATE, mSermonDate);
        intent.putExtra(DropdownControls.ACTION_NOTIFICATION_EXTRA_PASSAGE, mSermonScripture);

        intent.setAction(DropdownControls.ACTION_NOTIFICATION_NULL);

        getApplicationContext().startService(intent);
    }
}
