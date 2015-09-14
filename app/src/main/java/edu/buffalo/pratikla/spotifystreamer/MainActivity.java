package edu.buffalo.pratikla.spotifystreamer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback {

    private final String TAG = "MainActivity";
    private final String TRACKLISTFRAGMENT_TAG = "TLFTAG";

    private boolean mTwoPane;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (findViewById(R.id.track_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.track_container, new TrackListFragment(), TRACKLISTFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_help) {
            new AlertDialog.Builder(this)
                    .setTitle("Help")
                    .setMessage(getResources().getString(R.string.help_dialog_box_text))
                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(String artistId, String artistName) {
        Log.d(TAG, "Received callback");
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putString("artistId", artistId);
            args.putString("artistName", artistName);
            TrackListFragment tf = new TrackListFragment();
            tf.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.track_container, tf, TRACKLISTFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, TrackList.class);
            intent.putExtra("artistId", artistId);
            intent.putExtra("artistName", artistName);
            startActivity(intent);
        }
    }
}
