package edu.buffalo.pratikla.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RetrofitError;

/**
 * A placeholder fragment containing a simple view.
 */
public class TrackListFragment extends Fragment {

    private final String TAG = "TrackListFragment";
    private String artistName;
    private String artistId;
    private List<Track> mTrackList;

    private ListView listView;
    private TrackListAdapter mTrackAdapter;
    public TrackListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_track_list, container, false);

        Intent receivedIntent = getActivity().getIntent();
        artistId = receivedIntent.getStringExtra("artistId");
        artistName = receivedIntent.getStringExtra("artistName");

        if (artistName != null) {

            ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
            ab.setTitle("Top 10 Tracks");
            ab.setSubtitle(artistName);

//            getActivity().setTitle("Top 10 Tracks: " + artistName);
            Log.d(TAG, getActivity().getLocalClassName());


            Log.d(TAG, "Artist Name: " + artistName);
        }
        if (artistId != null) {
            Log.d(TAG, "Artist Id:" + artistId);
            populateTrackList(artistId);
        }

        listView = (ListView) rootView.findViewById(R.id.listview_track_results);

        mTrackAdapter = new TrackListAdapter(getActivity(), new ArrayList<Track>());
        listView.setAdapter(mTrackAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Track track = mTrackAdapter.mTrackList.get(position);
                String trackId = track.id;
                String trackName = track.name;
                String albumName = track.album.name;
                makeToast("Playing " + trackName + ".");
                Intent intent = new Intent(getActivity(), PlayerActivity.class);
                intent.putExtra("artistId", artistId);
                intent.putExtra("artistName", artistName);
                intent.putExtra("trackName", trackName);
                intent.putExtra("trackId", trackId);
                intent.putExtra("albumName", albumName);
                startActivity(intent);
            }
        });


        return rootView;
    }

    void populateTrackList(String artistId) {
        new SpotifyAsyncTask().execute(artistId);
    }

    private void makeToast(String key) {
        Toast toast = Toast.makeText(getActivity(), key, Toast.LENGTH_SHORT);
        toast.show();
    }

    private class SpotifyAsyncTask extends AsyncTask<String, Void, Tracks> {
        // private final String TAG = "SpotifyAsyncTask";
        private String artistId;

        @Override
        protected Tracks doInBackground(String... params) {
            Tracks tracks = null;
            if (params != null) {
                artistId = params[0];
            }

            if (artistId != null) {
                SpotifyApi spotifyApi = new SpotifyApi();
                Map<String, Object> queryMap = new HashMap<>();
                queryMap.put(SpotifyService.COUNTRY, "US");
                try {
                    tracks = spotifyApi.getService().getArtistTopTrack(artistId, queryMap);
                } catch (RetrofitError e) {
                    Log.d(TAG, "Exception in HTTP Request: " + e.getMessage());
                    return null;
                } catch (Exception e) {
                    Log.e(TAG, "Unexpected error: ");
                    e.printStackTrace();
                }
            }
            return tracks;
        }

        @Override
        protected void onPostExecute(Tracks tracks) {
            if (tracks == null
                    || tracks.tracks == null
                    || tracks.tracks.isEmpty()) {
                makeToast("No tracks found for " + artistName + ". Try again.");
                return;
            }
            mTrackList = tracks.tracks;
            if (mTrackList.size() > 10) {
                mTrackList = mTrackList.subList(0, 10);
            }

            mTrackAdapter = new TrackListAdapter(getActivity(), mTrackList);
            listView.setAdapter(mTrackAdapter);
        }
    }

    private class TrackListAdapter extends BaseAdapter {
        private final LayoutInflater mInflater;
        private final List<Track> mTrackList;

        public TrackListAdapter(Context context, List<Track> trackList) {
            mInflater = LayoutInflater.from(context);
            mTrackList = trackList;
        }

        @Override
        public int getCount() {
            return mTrackList.size();
        }

        @Override
        public Object getItem(int position) {
            return mTrackList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder holder;
            if (convertView == null) {
                view = mInflater.inflate(R.layout.list_item_results_tracks, parent, false);
                holder = new ViewHolder();
                holder.thumbnail =
                        (ImageView) view.findViewById(R.id.list_item_results_tracks_imageview);
                holder.albumName =
                        (TextView) view.findViewById(R.id.list_item_results_tracks_albumName);
                holder.trackName =
                        (TextView) view.findViewById(R.id.list_item_results_tracks_trackName);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }

            Track track = mTrackList.get(position);
            if (track != null
                    && track.album.images != null
                    && !track.album.images.isEmpty()) {
                Image artistImage = track.album.images.get(0);
                holder.albumName.setText(track.album.name);
                holder.trackName.setText(track.name);
                Picasso.with(getActivity())
                        .load(artistImage.url)
                        .resize(80, 80)
                        .centerCrop()
                        .into(holder.thumbnail);
            }
            return view;
        }

        private class ViewHolder {
            public ImageView thumbnail;
            public TextView albumName;
            public TextView trackName;
        }

    }
}
