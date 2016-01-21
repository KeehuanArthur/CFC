package org.cfchome;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;


/**
 * Created by arthurlee on 8/12/15.
 *
 * This class is used to control the media controller through the notification center
 * it uses the Notification builder and is created though a background service.
 * don't really remember why i made it this way TT_TT
 */
public class DropdownControls extends Service
{
    private String TAG = "DropDownControls";

    public static final String ACTION_NOTIFICATION_PLAY_PAUSE = "action_notification_playpause";
    public static final String ACTION_NOTIFICATION_CLOSE = "action_notification_close";
    public static final String ACTION_NOTIFICATION_CLICKSELF = "action_notification_clickself";
    public static final String ACTION_NOTIFICATION_NULL = "null";


    public static final String ACTION_NOTIFICATION_EXTRA_TITLE = "com.cfc.notificationTitle";
    public static final String ACTION_NOTIFICATION_EXTRA_PASTOR = "com.cfc.notificationPastor";
    public static final String ACTION_NOTIFICATION_EXTRA_DATE ="com.cfc.notificationDate";
    public static final String ACTION_NOTIFICATION_EXTRA_PASSAGE = "com.cfc.notificationPassage";

    private String mTitle;
    private String mPastor;
    private String mDate;
    private String mPassage;

    private boolean mIsPlaying = SermonPlayer.get(null, this).isPlaying();
    private NotificationManager mManager;
    private RemoteViews mCustomRemoteView;
    Notification mSermonController;


    /**
     * note: this function is called every time anything in the notification controller is touched by the user
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        handleIntent(intent);

        return super.onStartCommand(intent, flags, startId);

    }


    @Override
    public IBinder onBind(Intent i)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        // call this so that the Sermon Player singleton can get the DropDownControls context
        SermonPlayer.get(null, this);
    }

    // this is used to remove the dropdown notification controls from the notifications tray after
    // removing the app from the recent apps list

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        if( mManager != null )
            mManager.cancelAll();
    }

    @Override
    public void onDestroy()
    {
        if( mManager != null )
            mManager.cancelAll();
    }

    private void handleIntent( Intent intent )
    {
        if (intent != null && intent.getAction() != null) {

            mTitle = intent.getStringExtra(ACTION_NOTIFICATION_EXTRA_TITLE);
            mPastor = intent.getStringExtra(ACTION_NOTIFICATION_EXTRA_PASTOR);
            //mDate = intent.getStringExtra(ACTION_NOTIFICATION_EXTRA_DATE);
            mPassage = intent.getStringExtra(ACTION_NOTIFICATION_EXTRA_PASSAGE);

            // handle notification button control inputs
            if (intent.getAction().equalsIgnoreCase(ACTION_NOTIFICATION_PLAY_PAUSE))
            {
                SermonPlayer.get( null, this ).playPause(false, false);
                updateNotification();
            }
            else if(intent.getAction().equalsIgnoreCase(ACTION_NOTIFICATION_CLOSE))
            {
                /**
                 * set sermon_force_restart here because if notification controller is closed while user
                 * is at MediaActivity, you want sermon to restart when user presses play again
                 */
                if( SermonPlayer.get( null, this ).isActive() )
                {
                    SermonPlayer.get( null, this ).stop();
                    Constants.sermon_force_restart = true;
                }
                mManager.cancelAll();
            }
            else if(intent.getAction().equalsIgnoreCase(ACTION_NOTIFICATION_CLICKSELF))
            {
                // only restart the activity if the app is not viewable
                if( !Constants.viewable )
                {
                    Intent restartIntent = new Intent(getBaseContext(), MainPager.class);
                    restartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(restartIntent);
                }
            }
            else if (intent.getAction().equalsIgnoreCase(ACTION_NOTIFICATION_NULL))
            {
                //Log.d(TAG, "ACITON_NOTIFICATION_NULL case in intent handler---------------");
                showNotification(false);
            }
        }
    }


    private void showNotification(boolean isPlaying)
    {
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.cfclogo_se2);


        mSermonController = new NotificationCompat.Builder( getApplicationContext() )
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.play_button_white)
                .setLargeIcon(bm)
                .setContentTitle(Constants.nowPlayingTitle)
                .setWhen(0)
                .build();


        //.setLargeIcon(bm)
        //.setStyle(new NotificationCompat.BigPictureStyle().bigLargeIcon(bm))


        mSermonController.bigContentView = getExpandedView( isPlaying );
        //mSermonController.priority = Notification.PRIORITY_HIGH;
        mSermonController.flags = Notification.FLAG_ONGOING_EVENT;


        displayNotification();

    }


    public void updateNotification()
    {
        /**
         * for some reason mCustomRemoteView becomes null after you play sermon then open a bunch of different
         * apps...
         */
        if( mCustomRemoteView == null )
        {
            //Log.d(TAG, "mCustomRemoteView was null-----------");
            //mCustomRemoteView = getExpandedView( mIsPlaying );
            //showNotification( mIsPlaying );
        }

        if( SermonPlayer.get( null, this ).isPlaying() )
        {
            mCustomRemoteView.setImageViewResource(R.id.notification_playpause, R.drawable.pause_button );
        }
        else
        {
            mCustomRemoteView.setImageViewResource(R.id.notification_playpause, R.drawable.play_button );
        }

        displayNotification();
    }


    private void displayNotification()
    {
        mManager = (NotificationManager)getSystemService( Context.NOTIFICATION_SERVICE );

        //if( mManager == null)
            //Log.d(TAG, "bad notification manager");
        mManager.notify(1, mSermonController);
    }


    // change input to updating so you dont overwrite the text
    private RemoteViews getExpandedView( boolean isPlaying ) {
        mCustomRemoteView = new RemoteViews( getPackageName(), R.layout.dropdown_controls );

        mCustomRemoteView.setTextViewText(R.id.notification_title, mTitle);
        mCustomRemoteView.setTextViewText(R.id.notification_speaker, mPastor);
        mCustomRemoteView.setTextViewText(R.id.notification_passage, mPassage);

        mCustomRemoteView.setImageViewResource(R.id.notification_playpause, R.drawable.pause_button);

        Intent intent = new Intent( getApplicationContext(), DropdownControls.class );

        intent.setAction(ACTION_NOTIFICATION_PLAY_PAUSE);
        PendingIntent pendingIntent = PendingIntent.getService( getApplicationContext(), 1, intent, 0 );
        mCustomRemoteView.setOnClickPendingIntent( R.id.notification_playpause, pendingIntent );


        intent.setAction(ACTION_NOTIFICATION_CLOSE);
        pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        mCustomRemoteView.setOnClickPendingIntent(R.id.notification_close, pendingIntent);


        intent.setAction(ACTION_NOTIFICATION_CLICKSELF);
        pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        mCustomRemoteView.setOnClickPendingIntent(R.id.notification_controller, pendingIntent);

        return mCustomRemoteView;
    }

}
