package com.example.arthurlee.cfchome;

import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;

/**
 * Created by arthurlee on 8/12/15.
 */
public class MediaPlayerService
{
    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_PAUSE = "action_pause";
    public static final String ACTION_STOP = "action_stop";

    private MediaSession mSession;
    private MediaSessionManager mSessionManager;
    private MediaController mMediaController;


}
