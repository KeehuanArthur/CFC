package com.example.arthurlee.cfc;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
    private Handler mHandler = new Handler();
    Runnable updateSeekbar;

    private TextView mCurrentTime;
    private TextView mTotalTime;
    private SeekBar mSeekBar;


    SimpleDateFormat simpleDateFormatIn = new SimpleDateFormat("S");
    SimpleDateFormat simpleDateFormatOut = new SimpleDateFormat("mm:ss");


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
    public void play(String url, UUID id, final SeekBar seekBar, TextView currentTime, final TextView totalTime)
        {
            mSeekBar = seekBar;
            mCurrentTime = currentTime;
            mTotalTime = totalTime;

            //check if you are clicking the same sermon. If you clicked the sermon that is playing
            //already, don't restart the sermon
            if(curUUID != id) {

                Log.d("UUID parameter", id.toString());
                //Log.d("UUID in Singleton", curUUID.toString());



                sSermonPlayer.curUUID = id;
                stop();
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);



                //to switch between the JSON and XML version, change setDataSource Parameter
                mp3Url = url;
                String fullUrl = "http://s3.amazonaws.com/awctestbucket1/" + url;

                try {
                    mMediaPlayer.setDataSource(url);
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
                        int tempMS = getMediaPlayer().getDuration();
                        mSeekBar.setMax(tempMS);


                        Long min = TimeUnit.MILLISECONDS.toMinutes(tempMS);
                        Long sec = TimeUnit.MILLISECONDS.toSeconds(tempMS);
                        sec = sec - TimeUnit.MINUTES.toSeconds(min);

                        String minStr = Long.toString(min);
                        String secStr = Long.toString(sec);

                        if( sec < 10 )
                        {
                            secStr = "0" + secStr;
                        }

                        totalTime.setText(minStr + ":" + secStr);

                        /*
                        mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                            @Override
                            public void onBufferingUpdate(MediaPlayer mp, final int percent) {
                                ((Activity)sAppContext).runOnUiThread(new Runnable(){
                                    @Override
                                    public void run(){
                                        if (percent<mSeekBar.getMax()) {
                                            mSeekBar.setSecondaryProgress(percent);
                                            mSeekBar.setSecondaryProgress(percent/100);
                                        }
                                    }
                                });

                            }
                        });
                        */

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
                    int curTime = mMediaPlayer.getCurrentPosition();

                    mSeekBar.setProgress(curTime);

                    long min = TimeUnit.MILLISECONDS.toMinutes(curTime);
                    long sec = TimeUnit.MILLISECONDS.toSeconds(curTime) - TimeUnit.MINUTES.toSeconds(min);

                    String minStr = Long.toString(min);
                    String secStr = Long.toString(sec);


                    if( min < 10 )
                    {
                        minStr = "0" + minStr;
                    }

                    if( sec < 10 )
                    {
                        secStr = "0"+ secStr;
                    }

                    mCurrentTime.setText(minStr + ":" + secStr);

                }
            }
        });

        //Log.d("progress update", "attempted--------------------------"+ Integer.toString(mMediaPlayer.getCurrentPosition())
        //        +"/"+Integer.toString(mMediaPlayer.getDuration()));


        //update seekbar every 1 second
        mHandler.postDelayed(updateSeekbar, 1000);

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
            play(mp3Url, curUUID, mSeekBar, mCurrentTime, mTotalTime);
        }
    }

    //position is in milliseconds
    public void setPosition(int position)
    {
        mMediaPlayer.seekTo(position);
    }
}
