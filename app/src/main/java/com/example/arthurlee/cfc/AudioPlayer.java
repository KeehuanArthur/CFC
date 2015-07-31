package com.example.arthurlee.cfc;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.widget.SeekBar;

import java.io.IOException;

/**
 * Created by arthurlee on 7/2/15.
 */
public class AudioPlayer extends Object
{
    private MediaPlayer mPlayer;
    private Context mContext;
    private Activity mActivity;
    private int mDuration = 10000;
    private Handler mHandler = new Handler();

    public MediaPlayer getPlayer() {
        return mPlayer;
    }

    public void stop()
    {
        if (mPlayer != null)
        {
            mPlayer.release();
            mPlayer = null;
        }
    }

    public void pause()
    {
        if (mPlayer != null)
        {
            if (mPlayer.isPlaying())
                mPlayer.pause();
            else
                mPlayer.start();
        }
    }

    public int getCurrentPosition()
    {
        if(mPlayer != null)
        {
            return mPlayer.getCurrentPosition();
        }
        return 0;
    }

    public int getDuration()
    {
        return mDuration;
    }



    //pass in the activity and the seekview and control it from here on the ui thread
    public void play(final Activity a, String url, final SeekBar sSeekbar)
    {
        //stops any previous plays ie prevents the creatiion of multiple instances of MediaPlayer
        //if the user clicks Play twice
        stop();


        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mPlayer.setDataSource(url);
            //mPlayer.setOnBufferingUpdateListener(this);
            //mPlayer.setOnPreparedListener(this);
            //mediaPlayer.prepare(); // might take long! (for buffering, etc)   //@@
            mPlayer.prepareAsync();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block///
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                mDuration = mPlayer.getDuration();


            }


        });


        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                stop();
                //make it start from beginning
            }
        });

        //you can't just play the audio, you need a onpreparedlistener
        //mPlayer.start();
    }


    //not sure if this section is necessary
    /*
    public void onPrepared(MediaPlayer mPlayer)
    {
        if(!mPlayer.isPlaying())
        {
            mPlayer.start();
        }
    }
    */


}
