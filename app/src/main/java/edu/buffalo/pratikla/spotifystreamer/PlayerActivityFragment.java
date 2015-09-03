package edu.buffalo.pratikla.spotifystreamer;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerActivityFragment extends Fragment {

    public PlayerActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_player, container, false);

        Intent receivedIntent = getActivity().getIntent();
        String artistId = receivedIntent.getStringExtra("artistId");
        String trackId = receivedIntent.getStringExtra("trackId");
        String trackName = receivedIntent.getStringExtra("trackName");
        getActivity().setTitle("Spotify Streamer");



        return rootview;
    }
}
