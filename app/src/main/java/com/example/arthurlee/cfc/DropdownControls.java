package com.example.arthurlee.cfc;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Created by arthurlee on 8/12/15.
 */
public class DropdownControls extends Service
{
    public static final String ACTION_NOTIFICATION_PLAY_PAUSE = "action_notification_playpause";
    public static final String ACTION_NOTIFICATION_CLOSE = "action_notification_close";
    public static final String ACTION_NOTIFICATION_NULL = "null";


    public static final String ACTION_NOTIFICATION_EXTRA_TITLE = "com.cfc.notificationTitle";
    public static final String ACTION_NOTIFICATION_EXTRA_PASTOR = "com.cfc.notificationPastor";
    public static final String ACTION_NOTIFICATION_EXTRA_DATE ="com.cfc.notificationDate";
    public static final String ACTION_NOTIFICATION_EXTRA_PASSAGE = "com.cfc.notificationPassage";

    private String mTitle;
    private String mPastor;
    private String mDate;
    private String mPassage;

    private boolean mIsPlaying = SermonPlayer.get(getBaseContext(), true).isPlaying();
    private NotificationManager mManager;
    private RemoteViews mCustomRemoteView;
    Notification mSermonController;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d("handeling intent", "handeling intent");
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

    }


    public void onDestory()
    {

    }


    private void handleIntent( Intent intent )
    {
        if (intent != null && intent.getAction() != null) {

            mTitle = intent.getStringExtra(ACTION_NOTIFICATION_EXTRA_TITLE);
            mPastor = intent.getStringExtra(ACTION_NOTIFICATION_EXTRA_PASTOR);
            //mDate = intent.getStringExtra(ACTION_NOTIFICATION_EXTRA_DATE);
            mPassage = intent.getStringExtra(ACTION_NOTIFICATION_EXTRA_PASSAGE);

            if (intent.getAction().equalsIgnoreCase(ACTION_NOTIFICATION_PLAY_PAUSE))
            {
                SermonPlayer.get(getApplicationContext(), true).pauseplay();
                //showNotification(mIsPlaying);
                updateNotification();
            }
            if (intent.getAction().equalsIgnoreCase(ACTION_NOTIFICATION_NULL))
            {
                mIsPlaying = false;
                showNotification(mIsPlaying);
            }

            if(intent.getAction().equalsIgnoreCase(ACTION_NOTIFICATION_CLOSE))
            {
                Log.d("handling close", "handling close" );
                SermonPlayer.get(getBaseContext(),true).stop();
                mManager.cancelAll();
            }

        }

        //Log.d("notification control", "intent might be null");
    }


    private void showNotification(boolean isPlaying)
    {
        mSermonController = new NotificationCompat.Builder( getApplicationContext() )
                .setAutoCancel(false)
                .setSmallIcon(R.drawable.play_button_white)
                .setContentTitle(getString(R.string.app_name))
                .build();

        mSermonController.bigContentView = getExpandedView( isPlaying );
        mSermonController.flags = Notification.FLAG_ONGOING_EVENT;


        mManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
        mManager.notify(1, mSermonController);

    }

    private void updateNotification()
    {
        if( SermonPlayer.get(getBaseContext(), true).isPlaying() )
        {
            mCustomRemoteView.setImageViewResource(R.id.notification_playpause, R.drawable.play_button );
            Log.d("icon update attempt", "to play");
        }
        else
        {
            mCustomRemoteView.setImageViewResource(R.id.notification_playpause, R.drawable.pause_button );
            Log.d("icon update attempt", "to pause");
        }
    }

    private RemoteViews getExpandedView( boolean isPlaying ) {
        mCustomRemoteView = new RemoteViews( getPackageName(), R.layout.dropdown_controls );

        mCustomRemoteView.setTextViewText(R.id.notification_title, mTitle);
        mCustomRemoteView.setTextViewText(R.id.notification_speaker, mPastor);
        mCustomRemoteView.setTextViewText(R.id.notification_passage, mPassage);

        if( isPlaying )
        {
            mCustomRemoteView.setImageViewResource(R.id.notification_playpause, R.drawable.play_button);
        }
        else
        {
            //customView.setImageViewResource(R.id.notification_playpause, R.drawable.pause_button);
        }

        Intent intent = new Intent( getApplicationContext(), DropdownControls.class );

        intent.setAction(ACTION_NOTIFICATION_PLAY_PAUSE);
        PendingIntent pendingIntent = PendingIntent.getService( getApplicationContext(), 1, intent, 0 );
        mCustomRemoteView.setOnClickPendingIntent( R.id.notification_playpause, pendingIntent );


        intent.setAction(ACTION_NOTIFICATION_CLOSE);
        pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        mCustomRemoteView.setOnClickPendingIntent(R.id.notification_close, pendingIntent);

        return mCustomRemoteView;
    }

}
