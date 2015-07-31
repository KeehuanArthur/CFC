package com.example.arthurlee.cfc;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.widget.Button;

import java.io.IOException;

/**
 * Created by arthurlee on 7/29/15.
 *
 * this is a singleton
 */
public class SermonPlayer extends Object {

    private static SermonPlayer sSermonPlayer;
    private Context mAppContext;
    private MediaPlayer mMediaPlayer;
    private String mp3Url;

    //private constructor for singleton
    private SermonPlayer(Context appContext)
    {
        mAppContext = appContext;
        mMediaPlayer = new MediaPlayer();
    }


    //call this function to use singleton
    public static SermonPlayer get(Context c)
    {
        if(sSermonPlayer == null)
        {
            sSermonPlayer = new SermonPlayer(c);
        }

        return sSermonPlayer;
    }


    //this is called only when you open a new sermon. This is not to be used to restart sermon
    //to play/pause use pauseplay method
    public void play(String url)
    {
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


        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                //mDuration = mMediaPlayer.getDuration();
            }

        });


        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                stop();
                //make it start from beginning
            }
        });

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
            play(mp3Url);
        }
    }

}
