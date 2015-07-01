package edu.buffalo.pratikla.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private static final String TAG = "MainFragment";
    // private ArrayAdapter mSearchAdapter;
    private ArtistsListAdapter mSearchAdapter;
    private ListView listView;
    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        final EditText searchBar = (EditText) rootView.findViewById(R.id.search_bar);
        listView = (ListView) rootView.findViewById(R.id.listview_results);

        searchBar.setOnEditorActionListener(new EditText.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                Editable key = (Editable) v.getText();
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager)getActivity().
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    searchArtists(key.toString());
                    handled = true;
                }
                return handled;
            }
        });

/*
        mSearchAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.list_item_results_artists,
                R.id.list_item_results_textview,
                new ArrayList<String>());
*/

        mSearchAdapter = new ArtistsListAdapter(getActivity(), new ArrayList<Artist>());


        // Get the list view from the xml and set adapter to it.
        listView.setAdapter(mSearchAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Artist artist = mSearchAdapter.mArtistsList.get(position);
                String artistId = artist.id;
                String artistName = artist.name;
                makeToast("Searching songs for " + artistName + ".");
                Intent intent = new Intent(getActivity(), TrackList.class);
                intent.putExtra("artistId", artistId);
                intent.putExtra("artistName", artistName);
                startActivity(intent);

            }
        });
        return rootView;
    }

    private void searchArtists(String key) {
        new SpotifyAsyncTask().execute(key);
        //  makeToast(key);
    }

    private void makeToast(String key) {
        Toast toast = Toast.makeText(getActivity(), key, Toast.LENGTH_SHORT);
        toast.show();
    }

    private class SpotifyAsyncTask extends AsyncTask<String, Void, ArtistsPager> {
        private final String TAG = "SpotifyAsyncTask";
        private String searchKey;

        @Override
        protected ArtistsPager doInBackground(String... params) {
            ArtistsPager artists = null;
            if (params != null) {
                searchKey = params[0];
            }

            if (searchKey != null) {
                SpotifyApi api = new SpotifyApi();

                artists = api.getService().searchArtists(searchKey);
            }
            return artists;
        }

        @Override
        protected void onPostExecute(ArtistsPager artistsPager) {
            if (artistsPager == null
                    || artistsPager.artists == null
                    || artistsPager.artists.items == null
                    || artistsPager.artists.items.size() == 0) {
                makeToast("Artist " + searchKey + " was not found. try again.");
                return;
            }
            List<Artist> artistsList = artistsPager.artists.items;
            // ArrayList<String> artistNames = new ArrayList<String>();
            mSearchAdapter = new ArtistsListAdapter(getActivity(), artistsList);
            listView.setAdapter(mSearchAdapter);
        }
    }

    public class ArtistsListAdapter extends BaseAdapter {
        private final LayoutInflater mInflater;
        private final List<Artist> mArtistsList;

        public ArtistsListAdapter(Context context, List<Artist> artists) {
            mInflater = LayoutInflater.from(context);
            mArtistsList = artists;
        }

        @Override
        public int getCount() {
            return mArtistsList.size();
        }

        @Override
        public Object getItem(int position) {
            return mArtistsList.get(position);
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
                view = mInflater.inflate(R.layout.list_item_results_artists, parent, false);
                holder = new ViewHolder();
                holder.thumbnail =
                        (ImageView) view.findViewById(R.id.list_item_results_artists_imageview);
                holder.name =
                        (TextView) view.findViewById(R.id.list_item_results_artists_textview);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }

            Artist artist = mArtistsList.get(position);
            if (artist != null && artist.images != null && artist.images.size() > 0) {
                Image artistImage = artist.images.get(0);
                holder.name.setText(artist.name);
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
            public TextView name;
        }
    }
}
