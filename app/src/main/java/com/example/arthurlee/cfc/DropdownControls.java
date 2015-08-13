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
    private boolean mIsPlaying = SermonPlayer.get(getApplicationContext()).isPlaying();

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

            Log.d("notification control", "intent and action are not null");

            if (intent.getAction().equalsIgnoreCase(ACTION_NOTIFICATION_PLAY_PAUSE))
            {
                SermonPlayer.get(getApplicationContext()).pauseplay();
                showNotification(mIsPlaying);
            }

        }

        Log.d("notification control", "intent might be null");
    }


    private void showNotification(boolean isPlaying)
    {
        Notification notification = new NotificationCompat.Builder( getApplicationContext() )
                .setAutoCancel( true )
                .setContentTitle(getString(R.string.app_name))
                .build();

        notification.bigContentView = getExpandedView( isPlaying );

        NotificationManager manager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
        manager.notify( 1, notification );

    }

    private RemoteViews getExpandedView( boolean isPlaying ) {
        RemoteViews customView = new RemoteViews( getPackageName(), R.layout.dropdown_controls );

        if( isPlaying )
            customView.setImageViewResource( R.id.notification_playpause, R.drawable.play_button );
        else
            customView.setImageViewResource( R.id.notification_playpause, R.drawable.pause_button );


        Intent intent = new Intent( getApplicationContext(), DropdownControls.class );

        intent.setAction( ACTION_NOTIFICATION_PLAY_PAUSE );
        PendingIntent pendingIntent = PendingIntent.getService( getApplicationContext(), 1, intent, 0 );
        customView.setOnClickPendingIntent( R.id.notification_playpause, pendingIntent );


        return customView;
    }

}
