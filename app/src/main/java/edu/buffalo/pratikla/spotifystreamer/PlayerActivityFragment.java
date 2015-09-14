package edu.buffalo.pratikla.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.RetrofitError;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerActivityFragment extends Fragment {

    private final String TAG = "PlayerActivityFragment";
    View rootView;
    //    MediaPlayer mediaPlayer;
    private int currentPosition;
    private int maxDuration = 30000;
    private boolean isPlaying = false;
    private ArrayList<Track> trackList;
    private String artistName;
    private TextView elapsedTime, totalTime;
    private SeekBar seekBar;
    private int trackPosition;
    private ImageButton playButton;
    private Handler seekHandler;
    public PlayerActivityFragment() {
    }

    @Override
    public void onPause() {
        super.onPause();
/*
        mediaPlayer.release();
        mediaPlayer = null;
*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_player, container, false);

        Intent receivedIntent = getActivity().getIntent();
        getActivity().setTitle("Spotify Streamer");


        seekHandler = new Handler();
        trackPosition = receivedIntent.getIntExtra("trackPosition", -1);
        trackList = receivedIntent.getParcelableArrayListExtra("trackList");
        artistName = receivedIntent.getStringExtra("artistName");
        if (trackPosition >= 0) {
            playTrack(trackPosition);
        }

        elapsedTime = (TextView) rootView.findViewById(R.id.elapsed_time);
        totalTime = (TextView) rootView.findViewById(R.id.total_time);
        playButton = (ImageButton) rootView.findViewById(R.id.playPauseButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!playButton.isActivated()) {
                    playButton.setImageResource(android.R.drawable.ic_media_play);
                    playButton.setActivated(true);
                    isPlaying = false;
                    callService(null, MediaPlayerService.PAUSE, 0);
                } else {
                    playButton.setImageResource(android.R.drawable.ic_media_pause);
                    playButton.setActivated(false);
                    isPlaying = true;
                    callService(null, MediaPlayerService.UNPAUSE, 0);
                }
            }
        });
        ImageButton nextButton = (ImageButton) rootView.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trackPosition = (trackPosition + 1) % trackList.size();
                playTrack(trackPosition);
            }
        });
        ImageButton prevButton = (ImageButton) rootView.findViewById(R.id.prevButton);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trackPosition = (trackPosition + trackList.size() - 1) % trackList.size();
                playTrack(trackPosition);
            }
        });

        seekBar = (SeekBar) rootView.findViewById(R.id.seekbar);
        seekBar.setMax(maxDuration);
        totalTime.setText("0:" + (maxDuration / 1000));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    callService(null, MediaPlayerService.SEEK, progress);
                    currentPosition = progress;
                    setElapsedTime(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isPlaying) {
                    if (currentPosition < maxDuration) {
                        currentPosition += 200;
                        seekBar.setProgress(currentPosition);
                        setElapsedTime(currentPosition);
                    } else {
                        currentPosition = 0;
                        isPlaying = false;
                    }
                }
                seekHandler.postDelayed(this, 200);
            }
        });

        return rootView;
    }

    private void setElapsedTime(int elapsed) {
        int sec = (elapsed / 1000) % 60;
        int min = elapsed / 60000;
        if (sec < 10) {
            elapsedTime.setText("" + min + ":0" + sec);
        } else {
            elapsedTime.setText("" + min + ":" + sec);
        }
    }

    private void playTrack(int trackPosition) {
        populateTrackDetails(trackList, artistName, trackPosition);
        new SpotifyAsyncTask().execute(trackList.get(trackPosition).id);
    }

    private void populateTrackDetails(ArrayList<Track> trackList, String artistName, int trackPosition) {
        Track track = trackList.get(trackPosition);
        String albumName = track.album.name;
        String trackName = track.name;

        final TextView artistNameTv = (TextView) rootView.findViewById(R.id.artist_name);
        final TextView trackNameTv = (TextView) rootView.findViewById(R.id.track_name);
        final TextView albumNameTv = (TextView) rootView.findViewById(R.id.album_name);
        trackNameTv.setText(trackName);
        artistNameTv.setText(artistName);
        albumNameTv.setText(albumName);

    }

    private void makeToast(String key) {
        Toast toast = Toast.makeText(getActivity(), key, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void callService(String url, String action, int seek) {
        Intent intent = new Intent(getActivity(), MediaPlayerService.class);
        intent.putExtra("previewUrl", url);
        intent.putExtra("seek", seek);
        intent.setAction(action);
        getActivity().startService(intent);
    }

    private class SpotifyAsyncTask extends AsyncTask<String, Void, Track> {
        @Override
        protected Track doInBackground(String... params) {
            String trackId = "";
            if (params != null) {
                trackId = params[0];
            }

            if (trackId != null) {
                SpotifyApi spotifyApi = new SpotifyApi();
                // Map<String, Object> queryMap = new HashMap<>();
                // queryMap.put(SpotifyService.COUNTRY, "US");
                try {
                    return spotifyApi.getService().getTrack(trackId);
                } catch (RetrofitError e) {
                    Log.e(TAG, "Exception in HTTP Request: " + e.getMessage());
                    return null;
                } catch (Exception e) {
                    Log.e(TAG, "Unexpected error: ");
                    e.printStackTrace();
                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(Track track) {
            if(track == null) {
                makeToast("Track not found.");
                return;
            }

            ImageView thumbnail = (ImageView) rootView.findViewById(R.id.album_image);
            Image image = track.album.images.get(0);
            thumbnail.setAdjustViewBounds(true);
            Picasso.with(getActivity())
                    .load(image.url)
                    .into(thumbnail);

            try {
                String url = track.preview_url;
/*
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(url);
                mediaPlayer.prepareAsync(); // might take long! (for buffering, etc)
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        seekBar.setMax(mediaPlayer.getDuration());
                        mp.start();
                    }
                });
*/
                playButton.setActivated(false);
                playButton.setImageResource(android.R.drawable.ic_media_pause);
                currentPosition = 0;
                isPlaying = true;
                callService(url, MediaPlayerService.PLAY, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
