package com.example.arthurlee.cfc;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.SeekBar;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by arthurlee on 7/29/15.
 *
 * this is a singleton
 */
public class SermonPlayer extends Object {

    private static SermonPlayer sSermonPlayer;
    private static Context sAppContext;
    private MediaPlayer mMediaPlayer;
    private String mp3Url;
    private UUID curUUID;
    private SeekBar mSeekBar;
    private Handler mHandler = new Handler();
    Runnable updateSeekbar;

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    //private constructor for singleton
    private SermonPlayer(Context appContext)
    {
        sAppContext = appContext;
        //curUUID = UUID.randomUUID();
        mMediaPlayer = new MediaPlayer();
    }


    //call this function to use singleton
    public static SermonPlayer get(Context c)
    {
        if(sSermonPlayer == null)
        {
            sSermonPlayer = new SermonPlayer(c);
            Log.d("Constructor called", "Constructor called------------------------");
        }
        sAppContext = c;
        return sSermonPlayer;
    }


    //this is called only when you open a new sermon. This is not to be used to restart sermon
    //to play/pause use pauseplay method
    //this is more like a init/setup function
    public void play(String url, UUID id, SeekBar seekBar)
        {
            mSeekBar = seekBar;

            //check if you are clicking the same sermon. If you clicked the sermon that is playing
            //already, don't restart the sermon
            if(curUUID != id) {

                Log.d("UUID parameter", id.toString());
                //Log.d("UUID in Singleton", curUUID.toString());



                sSermonPlayer.curUUID = id;
                mp3Url = url;
                stop();
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                String fullUrl = "http://s3.amazonaws.com/awctestbucket1/" + url;

                try {
                    mMediaPlayer.setDataSource(fullUrl);
                    //mPlayer.setOnBufferingUpdateListener(this);
                    //mPlayer.setOnPreparedListener(this);
                    //mediaPlayer.prepare(); // might take long! (for buffering, etc)   //@@
                    mMediaPlayer.prepareAsync();
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

                //dont play the sermon until enough of sermon has buffered
                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                        mSeekBar.setMax(getMediaPlayer().getDuration());

                        updateProgress();
                    }

                });


                updateSeekbar = new Runnable() {
                    @Override
                    public void run() {
                        if(mMediaPlayer != null)
                            updateProgress();
                    }
                };


                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        stop();
                        //make it start from beginning
                    }
                });
            }
    }


    public void updateProgress()
    {
        ((Activity)sAppContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mMediaPlayer != null)
                {
                    mSeekBar.setProgress(mMediaPlayer.getCurrentPosition());
                }
            }
        });

        //Log.d("progress update", "attempted--------------------------"+ Integer.toString(mMediaPlayer.getCurrentPosition())
        //        +"/"+Integer.toString(mMediaPlayer.getDuration()));


        //update seekbar every 5 seconds
        mHandler.postDelayed(updateSeekbar, 5000);

    }

    public void stop()
    {
        if (mMediaPlayer != null)
        {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void pauseplay(Button pauseplaybutton)
    {
        if (mMediaPlayer != null)
        {
            if (mMediaPlayer.isPlaying())
            {
                mMediaPlayer.pause();
            }
            else
            {
                mMediaPlayer.start();
            }
        }
        else
        {
            play(mp3Url, curUUID, mSeekBar);
        }
    }



    public int getCurrentPosition()
    {
        if(mMediaPlayer != null)
        {

            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public int getMaxPosition()
    {
        if(mMediaPlayer != null)
        {
            return mMediaPlayer.getDuration();
        }
        return 1;
    }



}
