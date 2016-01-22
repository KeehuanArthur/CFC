package org.cfchome;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

/**
 * Created by arthurlee on 1/21/16.
 */
public class SermonPlayerService extends Service implements MediaPlayer.OnCompletionListener
{
    SermonPlayer mSermonPlayer;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //mediaPlayer = MediaPlayer.create(this, R.raw.s);// raw/s.mp3
        //mediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       // if (!mediaPlayer.isPlaying()) {
         //   mediaPlayer.start();
        //}
        return 0;
    }

    public void onDestroy() {
        //if (mediaPlayer.isPlaying()) {
        //    mediaPlayer.stop();
        //}
        //mediaPlayer.release();
    }

    public void onCompletion(MediaPlayer _mediaPlayer) {
        stopSelf();
    }

}
