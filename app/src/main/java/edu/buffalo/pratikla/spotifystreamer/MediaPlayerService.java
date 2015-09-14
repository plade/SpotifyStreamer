package edu.buffalo.pratikla.spotifystreamer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

public class MediaPlayerService extends Service {
    public final static String PLAY = "PLAY";
    public final static String PAUSE = "PAUSE";
    public final static String SEEK = "SEEK";
    public final static String UNPAUSE = "UNPAUSE";
    private final String TAG = "MediaPlayerService";
    MediaPlayer mMediaPlayer = null;

    public MediaPlayerService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int retVal = super.onStartCommand(intent, flags, startId);
        String action = intent.getAction();
        String url = intent.getStringExtra("previewUrl");
        int seek = intent.getIntExtra("seek", 0);
        switch (action) {
            case PLAY:
                Log.d(TAG, "On Start Command");
                playTrack(url);
                break;
            case PAUSE:
                pauseTrack();
                break;
            case SEEK:
                seekTrack(seek);
                break;
            case UNPAUSE:
                unPauseTrack();
                break;
        }

        return retVal;
    }

    private void unPauseTrack() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                Log.d(TAG, "Unpause clicked while playing! What?? Whoa!!");
            }
            mMediaPlayer.start();
        }
    }

    private void seekTrack(int seekTime) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(seekTime);
        }
    }

    private void playTrack(String url) {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(url);
            Log.d(TAG, "Preparing Async");
            mMediaPlayer.prepareAsync(); // might take long! (for buffering, etc)
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.d(TAG, "Prepared, starting");
                    mp.start();
                }
            });
        } catch (IOException e) {
            Log.e(TAG, "IO Exception: " + e);
        }
    }

    private void pauseTrack() {
        mMediaPlayer.pause();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
