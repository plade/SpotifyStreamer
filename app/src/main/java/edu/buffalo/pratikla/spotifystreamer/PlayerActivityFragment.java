package edu.buffalo.pratikla.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.RetrofitError;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerActivityFragment extends Fragment {

    private String artistId, artistName, trackId, trackName, albumName;
    View rootview;
    private final String TAG = "PlayerActivityFragment";
    public PlayerActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_player, container, false);

        Intent receivedIntent = getActivity().getIntent();
        artistId = receivedIntent.getStringExtra("artistId");
        artistName = receivedIntent.getStringExtra("artistName");
        trackId = receivedIntent.getStringExtra("trackId");
        trackName = receivedIntent.getStringExtra("trackName");
        albumName = receivedIntent.getStringExtra("albumName");
        getActivity().setTitle("Spotify Streamer");
        final TextView artistNameTv = (TextView) rootview.findViewById(R.id.artist_name);
        final TextView trackNameTv = (TextView) rootview.findViewById(R.id.track_name);
        final TextView albumNameTv = (TextView) rootview.findViewById(R.id.album_name);
        trackNameTv.setText(trackName);
        artistNameTv.setText(artistName);
        albumNameTv.setText(albumName);

        playTrack(trackId);
        return rootview;
    }

    private void playTrack(String trackId) {
        new SpotifyAsyncTask().execute(trackId);
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
                Map<String, Object> queryMap = new HashMap<>();
                queryMap.put(SpotifyService.COUNTRY, "US");
                try {
                    Track track = spotifyApi.getService().getTrack(trackId);
                    Log.d(TAG, "Track name is: " + track.name);
                    return track;
                } catch (RetrofitError e) {
                    Log.d(TAG, "Exception in HTTP Request: " + e.getMessage());
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


            ImageView thumbnail = (ImageView) rootview.findViewById(R.id.album_image);
            Image image = track.album.images.get(0);
            thumbnail.setAdjustViewBounds(true);
            Picasso.with(getActivity())
                    .load(image.url)
                    .into(thumbnail);

        }
    }

    private void makeToast(String key) {
        Toast toast = Toast.makeText(getActivity(), key, Toast.LENGTH_SHORT);
        toast.show();
    }

}
