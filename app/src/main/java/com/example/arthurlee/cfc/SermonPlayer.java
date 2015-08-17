package com.example.arthurlee.cfc;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
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
    private boolean mForceRestart;
    Runnable updateSeekbar;

    private TextView mCurrentTime;
    private TextView mTotalTime;
    private SeekBar mSeekBar;
    private ImageButton mPlayPauseButton;


    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    //private constructor for singleton
    private SermonPlayer(Context appContext)
    {
        sAppContext = appContext;
        //curUUID = UUID.randomUUID();
        //sSermonPlayer.mMediaPlayer = new MediaPlayer();
    }


    //call this function to use singleton
    public static SermonPlayer get(Context c, boolean fromService)
    {
        if(sSermonPlayer == null)
        {
            sSermonPlayer = new SermonPlayer(c);
            sSermonPlayer.curUUID = UUID.randomUUID();
            sSermonPlayer.mMediaPlayer = new MediaPlayer();
            sSermonPlayer.mForceRestart = false;
            Log.d("Constructor called", "Constructor called------------------------");
        }

        if(!fromService)
        {
            sAppContext = c;
        }

        return sSermonPlayer;
    }


    //this is called only when you open a new sermon. This is not to be used to restart sermon
    //to play/pause use pauseplay method
    //this is more like a init/setup function
    public void play(String url, final SeekBar seekBar, TextView currentTime, final TextView totalTime,
                     ImageButton playpause)
        {
            sSermonPlayer.mSeekBar = seekBar;
            sSermonPlayer.mCurrentTime = currentTime;
            sSermonPlayer.mTotalTime = totalTime;
            sSermonPlayer.mPlayPauseButton = playpause;

            //check if you are clicking the same sermon. If you clicked the sermon that is playing
            //already, don't restart the sermon


            if(sSermonPlayer.mp3Url == null || !(sSermonPlayer.mp3Url).equals(url) || sSermonPlayer.mForceRestart) {

                Log.d("SermonPlayer","play entered init phase");

                sSermonPlayer.mp3Url = url;

                sSermonPlayer.stop();
                mPlayPauseButton.setBackgroundResource(R.drawable.pause_button);
                sSermonPlayer.mMediaPlayer = new MediaPlayer();
                sSermonPlayer.mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                sSermonPlayer.mForceRestart = false;



                //to switch between the JSON and XML version, change setDataSource Parameter
                mp3Url = url;
                String fullUrl = "http://s3.amazonaws.com/awctestbucket1/" + url;

                try {
                    sSermonPlayer.mMediaPlayer.setDataSource(url);
                    //mPlayer.setOnBufferingUpdateListener(this);
                    //mPlayer.setOnPreparedListener(this);
                    //mediaPlayer.prepare(); // might take long! (for buffering, etc)   //@@
                    sSermonPlayer.mMediaPlayer.prepareAsync();
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
                sSermonPlayer.mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                        int tempMS = getMediaPlayer().getDuration();
                        sSermonPlayer.mSeekBar.setMax(tempMS);


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
                                        if (percent < 100) {
                                            //mSeekBar.setSecondaryProgress(percent);

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


                sSermonPlayer.updateSeekbar = new Runnable() {
                    @Override
                    public void run() {
                        if(mMediaPlayer != null)
                            updateProgress();
                    }
                };


                sSermonPlayer.mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        stop();
                        //make it start from beginning
                    }
                });

            }

            else
            {
                int tempMS = getMediaPlayer().getDuration();
                sSermonPlayer.mSeekBar.setMax(tempMS);


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
            }
    }


    public void updateProgress()
    {
        ((Activity)sAppContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(sSermonPlayer.mMediaPlayer != null)
                {
                    int curTime = sSermonPlayer.mMediaPlayer.getCurrentPosition();

                    sSermonPlayer.mSeekBar.setProgress(curTime);

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

                    sSermonPlayer.mCurrentTime.setText(minStr + ":" + secStr);

                }
            }
        });

        //Log.d("progress update", "attempted--------------------------"+ Integer.toString(mMediaPlayer.getCurrentPosition())
        //        +"/"+Integer.toString(mMediaPlayer.getDuration()));


        //update seekbar every 1 second
        sSermonPlayer.mHandler.postDelayed(updateSeekbar, 1000);

    }

    public void stop()
    {
        if (sSermonPlayer.mMediaPlayer != null)
        {
            sSermonPlayer.mPlayPauseButton.setBackgroundResource(R.drawable.play_button);
            sSermonPlayer.mMediaPlayer.release();
            sSermonPlayer.mMediaPlayer = null;
            sSermonPlayer.mForceRestart = true;
        }
    }


    public void playPause(Boolean forcePlay, Boolean forcePause)
    {

        Log.d("Force Restart Value", Boolean.toString(sSermonPlayer.mForceRestart));

        if (sSermonPlayer.mMediaPlayer != null && !sSermonPlayer.mForceRestart)
        {


            Log.d("pauseplayfunction", "entered wrong if statement");

            if (sSermonPlayer.mMediaPlayer.isPlaying() || forcePause)
            {
                sSermonPlayer.mMediaPlayer.pause();


                ((Activity)sAppContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPlayPauseButton.setBackgroundResource(R.drawable.play_button);
                    }
                });

            }

            else if( forcePlay )
            {
                sSermonPlayer.mMediaPlayer.start();
                mPlayPauseButton.setBackgroundResource(R.drawable.pause_button);
            }

            else
            {
                sSermonPlayer.mMediaPlayer.start();


                ((Activity)sAppContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPlayPauseButton.setBackgroundResource(R.drawable.pause_button);
                    }
                });

            }
        }
        else
        {
            Log.d("play function", "start ");
            play(mp3Url, mSeekBar, mCurrentTime, mTotalTime, mPlayPauseButton);
        }
    }

    public String getCurrentUrl()
    {
        if(sSermonPlayer == null)
        {
            return "00000";
        }
        else
        {
            return sSermonPlayer.mp3Url;
        }
    }

    public boolean isPlaying()
    {
        return mMediaPlayer.isPlaying();
    }

    //position is in milliseconds
    public void setPosition(int position)
    {
        sSermonPlayer.mMediaPlayer.seekTo(position);
    }
}
