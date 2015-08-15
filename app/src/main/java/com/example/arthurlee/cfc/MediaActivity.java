package com.example.arthurlee.cfc;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.UUID;

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


    private AudioPlayer mMediaPlayer = new AudioPlayer();
    private Context mAppContext;
    private Handler seekHandler = new Handler();

    public static final String EXTRA_MP3URL = "com.cfc.mp3_url";
    public static final String EXTRA_PASTOR_NAME = "com.cfc.pastorName";
    public static final String EXTRA_SERMON_DATE = "com.cfc.sermonDate";
    public static final String EXTRA_SERMON_TITLE = "com.cfc.sermonTitle";
    public static final String EXTRA_SERMON_UUID = "com.cfc.uuid";

    private String mMP3URL;
    private String mPastorName;
    private String mSermonDate;
    private String mSermonTitle;
    private UUID sermonUUID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_top);
        setContentView(R.layout.activity_player);



        mMP3URL = getIntent().getStringExtra(EXTRA_MP3URL);

        mPastorName = getIntent().getStringExtra(EXTRA_PASTOR_NAME);
        mPastorPhoto = (ImageView)findViewById(R.id.pastorPhoto);
        mSermonTitle = getIntent().getStringExtra(EXTRA_SERMON_TITLE);
        sermonUUID = UUID.fromString(getIntent().getStringExtra(EXTRA_SERMON_UUID));


        mTitle = (TextView)findViewById(R.id.media_title);
        mTitle.setText(mSermonTitle);

        //Log.d("UUID found in player", sermonUUID.toString());


        Drawable new_image;
        switch (mPastorName)
        {
            case "Rev. Min Chung":
                new_image = getResources().getDrawable(R.drawable.pmin);
                mPastorPhoto.setImageDrawable(new_image);
                break;
            case "Pastor KJ Kim":
                new_image = getResources().getDrawable(R.drawable.pkj);
                mPastorPhoto.setImageDrawable(new_image);
                break;
            case "Rev. KJ Kim":
                new_image = getResources().getDrawable(R.drawable.pkj);
                mPastorPhoto.setImageDrawable(new_image);
                break;
            case "Pastor Jim Han":
                new_image = getResources().getDrawable(R.drawable.pjim);
                mPastorPhoto.setImageDrawable(new_image);
                break;
            case "Pastor Tony Thomas":
                new_image = getResources().getDrawable(R.drawable.ptony);
                mPastorPhoto.setImageDrawable(new_image);
                break;
            case "Pastor David Kang":
                new_image = getResources().getDrawable(R.drawable.pdave);
                mPastorPhoto.setImageDrawable(new_image);
                break;
            case "Rev. David Kang":
                new_image = getResources().getDrawable(R.drawable.pdave);
                mPastorPhoto.setImageDrawable(new_image);
                break;
            case "Pastor Sean Lee":
                new_image = getResources().getDrawable(R.drawable.psean);
                mPastorPhoto.setImageDrawable(new_image);
                break;

        }



        mPlayButton = (ImageButton)findViewById(R.id.media_play_button);
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SermonPlayer.get(MediaActivity.this, false).pauseplay();

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



        //start the sermon when new activity is created
        //the sermon player class also controls the UI elements: seekbar, currenttime, totaltime


        SermonPlayer.get(MediaActivity.this, false).play(mMP3URL, sermonUUID, mSeekBar, mCurrentTime, mTotalTime, mPlayButton);

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.abc_slide_in_top, R.anim.abc_slide_out_bottom);
    }


}
