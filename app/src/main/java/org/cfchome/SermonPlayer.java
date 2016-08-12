package org.cfchome;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by arthurlee on 7/29/15.
 *
 * this is a singleton because you don't want 2 sermons to be playing at the same time
 *
 * if you want the graphical stuff, go to MediaActivity
 */
public class SermonPlayer extends Object {

    private static String TAG = "SermonPlayer";
    private static SermonPlayer sSermonPlayer;

    // maybe mediaActivityContext and dropDownControlsContext shouldn't be static..

    private static Context sMediaActivityContext;
    private static Context sDropDownControlsContext;
    private MediaPlayer mMediaPlayer;
    private String mp3Url;
    private Handler mHandler = new Handler();
    Runnable updateSeekbar;

    private boolean isPlaying;  // MediaPlayer.isPlaying() doesn't keep track of if player is paused or not so we need this

    private TextView mCurrentTime;
    private TextView mTotalTime;
    private SeekBar mSeekBar;
    private ImageButton mPlayPauseButton;

    private Intent notificationIntent;


    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    //private constructor for singleton
    private SermonPlayer(Context mediaActivityContext, Context dropDownControlsContext)
    {
        sMediaActivityContext = mediaActivityContext;
        sDropDownControlsContext = dropDownControlsContext;

        Log.d(TAG, "Constructor was calleddddddd --------------");
    }

    /**
     * Singleton get()
     *
     * to get singleton, you need to pass in the MediaPlayer Context and the DropDownControls Context
     * or null if they are not available from the class that get() is being called from
     *
     * @return Singleton instance of SermonPlayer
     */

    public static SermonPlayer get(Context mediaActivityContext, Context dropDownControlsContext)
    {
        if(sSermonPlayer == null)
        {
            sSermonPlayer = new SermonPlayer(mediaActivityContext, dropDownControlsContext);
            sSermonPlayer.mMediaPlayer = new MediaPlayer();

            if( sMediaActivityContext != null)
            {
                sSermonPlayer.mMediaPlayer.setWakeMode( sMediaActivityContext.getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK );
            }

            /**
             * in future, maybe set wifi lock too and keep lock until sermon is done buffering
             */

            /**
             * TEST: make async task that will keep track of the life of this singleton. while cfc app is in background
             *       open a bunch of different apps and see when the instance sSermonPlayer becomes null
             */
        }

        /**
         * you may want to make sMediaActivityContext update regardless if its null or not. might save processing power if
         * app is in background so you are not constantly updating a view that is not visible
         */

        if( mediaActivityContext != null )
        {
            sMediaActivityContext = mediaActivityContext;
        }
        if( dropDownControlsContext != null )
        {
            sDropDownControlsContext = dropDownControlsContext;
        }

        return sSermonPlayer;
    }

    public Context getDropDownControlsContext()
    {
        if( sSermonPlayer != null )
            return sDropDownControlsContext;

        else
            return null;
    }


    /**
     * play()
     *
     * this is called only when you open a new sermon, not when you want to play or pause a sermon.
     * there is a separate  playpause method
     * this is more like an initialization function
     *
     * This function is called when a Sermon activity starts but also checks if user clicked on already
     * playing sermon
     */
    public void play(String url, final SeekBar seekBar, TextView currentTime, final TextView totalTime,
                     ImageButton playpause)
        {
            Constants.sermonPlayerPaused = false;

            sSermonPlayer.mSeekBar = seekBar;
            sSermonPlayer.mCurrentTime = currentTime;
            sSermonPlayer.mTotalTime = totalTime;
            sSermonPlayer.mPlayPauseButton = playpause;

            /**
             * this checks if user is clicking the same sermon. If user is, you dont need to go though the
             * initialization process again
             */
            if(sSermonPlayer.mp3Url == null || !(sSermonPlayer.mp3Url).equals(url) || Constants.sermon_force_restart) {

                sSermonPlayer.mp3Url = url;

                sSermonPlayer.stop();
                sSermonPlayer.mPlayPauseButton.setBackgroundResource(R.drawable.pause_circle);
                sSermonPlayer.mMediaPlayer = new MediaPlayer();
                sSermonPlayer.mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                Constants.sermon_force_restart = false;

                Constants.sermon_buffering = true;

                //to switch between the JSON and XML version, change setDataSource Parameter
                mp3Url = url;

                try {
                    sSermonPlayer.mMediaPlayer.setDataSource(url);
                    //mPlayer.setOnBufferingUpdateListener(this);
                    //mPlayer.setOnPreparedListener(this);
                    //mediaPlayer.prepare(); // might take long! (for buffering, etc)   //@@
                    sSermonPlayer.mMediaPlayer.prepareAsync();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
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
                                ((Activity)sMediaActivityContext).runOnUiThread(new Runnable(){
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
                        // enable the play button
                        ((MediaActivity)sMediaActivityContext).enable_play_pause_button();
                        Constants.sermon_buffering = false;

                        updateProgress();

                        isPlaying = true;
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
                        //todo: make it start from beginning
                    }
                });

                ((MediaActivity)sMediaActivityContext).activate_notification_controller();
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
        ((Activity)sMediaActivityContext).runOnUiThread(new Runnable() {
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
                else
                {
                    //Log.d("Sermon Player", "Sermon Player just died");
                    // change this to sDropdownControlContext
                    sMediaActivityContext.getApplicationContext().stopService(notificationIntent);
                    //((DropdownControls)sDropDownControlsContext).
                    sDropDownControlsContext = null;

                }
            }
        });


        //update seekbar every 1 second
        sSermonPlayer.mHandler.postDelayed(updateSeekbar, 1000);

    }



    // todo the bottom play button should be brought out from the sMediaActivityView context and
    // check if the mediacontext is null or not
    public void stop()
    {
        if (sSermonPlayer.mMediaPlayer != null)
        {
            sSermonPlayer.mPlayPauseButton.setBackgroundResource(R.drawable.play_circle);
            sSermonPlayer.mMediaPlayer.release();
            sSermonPlayer.mMediaPlayer = null;
            Constants.sermon_force_restart = true;
        }
    }

    /**
     * this function is to be called after the user presses back enough to exit the application. When this happens
     * this function should clean up all parts of the singleton and prevent memory leaks
     */
    public void prepareForClose()
    {
        if( mMediaPlayer != null )
        {
            sSermonPlayer.mMediaPlayer.release();
            sSermonPlayer.mMediaPlayer = null;
        }
        Constants.sermon_force_restart = true;

        if( notificationIntent != null)
        {
            sMediaActivityContext.getApplicationContext().stopService(notificationIntent);
        }
        sMediaActivityContext = null;
        sDropDownControlsContext = null;

    }

    public void playPause(Boolean forcePlay, Boolean forcePause)
    {
        if (sSermonPlayer.mMediaPlayer != null && !Constants.sermon_force_restart )
        {
            if (sSermonPlayer.mMediaPlayer.isPlaying() )
            {
                sSermonPlayer.mMediaPlayer.pause();

                if( sMediaActivityContext != null )
                {
                    ((Activity)sMediaActivityContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sSermonPlayer.mPlayPauseButton.setBackgroundResource(R.drawable.play_circle);
                            Constants.sermonPlayerPaused = true;
                        }
                    });
                }
            }

            else if( forcePlay )
            {
                sSermonPlayer.mMediaPlayer.start();
                sSermonPlayer.mPlayPauseButton.setBackgroundResource(R.drawable.pause_circle);
                Constants.sermonPlayerPaused = true;
            }

            else
            {
                sSermonPlayer.mMediaPlayer.start();

                if( sMediaActivityContext != null)
                {
                    ((Activity)sMediaActivityContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mPlayPauseButton.setBackgroundResource(R.drawable.pause_circle);
                            Constants.sermonPlayerPaused = false;
                        }
                    });
                }
            }

            isPlaying = !isPlaying;
        }
        else
        {
            play(mp3Url, mSeekBar, mCurrentTime, mTotalTime, mPlayPauseButton);
            Constants.sermonPlayerPaused = false;
        }

        /* maybe call this in the activity because it seems to cause crash when you have multiple apps running at the
           same time and parts begin to
        if( sDropDownControlsContext != null )
        {
            ((DropdownControls)sDropDownControlsContext).updateNotification();
        }
        */
    }

    /**
     * detects if a sermon is playing or not
     * @return
     */
    public boolean isPlaying()
    {
        if( mMediaPlayer != null )
            return isPlaying;
        else
            return false;
    }

    /**
     * SermonPlayer.get() will never return null but the media player in the SermonPlayer might be null if nothing
     * is playing at the moment so you can use this function to check if the media player is actually null or not
     *
     * the check for sDropDownControls accounts for the base case for in the beginning when the Constructor is called.
     * the controller creates a new MediaPlayer and its not null.
     * @return
     */
    public boolean isActive()
    {
        if( mMediaPlayer == null || sDropDownControlsContext == null )
            return false;
        else
            return true;
    }

    //position is in milliseconds
    public void setPosition(int position)
    {
        sSermonPlayer.mMediaPlayer.seekTo(position);
    }



    /**
     * TEST: make async task that will keep track of the life of this singleton. while cfc app is in background
     *       open a bunch of different apps and see when the instance sSermonPlayer becomes null
     */
    private class singletonChecker extends AsyncTask<Void, Void, Void>
    {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void ... arg0)
        {


            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {

        }
    }
}
