package com.example.arthurlee.cfc;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.amazonmobileanalytics.AnalyticsEvent;

/**
 * Created by arthurlee on 7/25/15.
 */
public class MediaActivity extends FragmentActivity {

    private ImageButton mPlayButton;
    private TextView mTitle;
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
    private String mPastorName;
    private String mSermonDate;
    private String mSermonTitle;
    private String mSermonScripture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_top);
        setContentView(R.layout.activity_player);


        mMP3URL = getIntent().getStringExtra(EXTRA_MP3URL);

        mPastorName = getIntent().getStringExtra(EXTRA_PASTOR_NAME);
        mPastorPhoto = (ImageView)findViewById(R.id.pastorPhoto);
        mSermonTitle = getIntent().getStringExtra(EXTRA_SERMON_TITLE);
        mSermonScripture = getIntent().getStringExtra(EXTRA_SERMON_SCRIPTURE);


        AnalyticsEvent testEvent = MainPager.analytics.getEventClient().createEvent("test")
                .withAttribute("test title", mSermonTitle);

        MainPager.analytics.getEventClient().recordEvent(testEvent);
        MainPager.analytics.getEventClient().submitEvents();

        /*
        if(MainPager.analytics != null) {
            MainPager.analytics.getSessionClient().pauseSession();
        }
        */

        mTitle = (TextView)findViewById(R.id.media_title);
        mTitle.setText(mSermonTitle);



        Drawable new_image;
        switch (mPastorName)
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
            case "Pastor Tony Thomas":
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

        }



        mPlayButton = (ImageButton)findViewById(R.id.media_play_button);
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SermonPlayer.get(MediaActivity.this, false).playPause(false, false);

            }
        });


        mCurrentTime = (TextView)findViewById(R.id.media_current_time);
        mTotalTime = (TextView)findViewById(R.id.media_total_time);

        //the seekbar is currently being updated by the SermonPlayer class b/c program needs
        //to wait for on prepared
        final SeekBar mSeekBar = (SeekBar)findViewById((R.id.sermon_audio_seekBar));
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                SermonPlayer.get(MediaActivity.this, false).setPosition(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                // TODO Auto-generated method stub

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


        // set up the notification center controls via android service
        final Context baseContext = getApplicationContext();
        Intent intent = new Intent( baseContext, DropdownControls.class );
        intent.putExtra(DropdownControls.ACTION_NOTIFICATION_EXTRA_TITLE, mSermonTitle);
        intent.putExtra(DropdownControls.ACTION_NOTIFICATION_EXTRA_PASTOR, mPastorName);
        intent.putExtra(DropdownControls.ACTION_NOTIFICATION_EXTRA_DATE, mSermonDate);
        intent.putExtra(DropdownControls.ACTION_NOTIFICATION_EXTRA_PASSAGE, mSermonScripture);

        intent.setAction(DropdownControls.ACTION_NOTIFICATION_NULL);
        getApplicationContext().startService(intent);



        //start the sermon when new activity is created
        //the sermon player class also controls the UI elements: seekbar, currenttime, totaltime
        SermonPlayer.get(MediaActivity.this, false).play(mMP3URL, mSeekBar, mCurrentTime, mTotalTime, mPlayButton);

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.abc_slide_in_top, R.anim.abc_slide_out_bottom);
    }



}
