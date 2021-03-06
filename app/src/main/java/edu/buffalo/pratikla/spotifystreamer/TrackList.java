package edu.buffalo.pratikla.spotifystreamer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class TrackList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_list);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(false);
        }

        if (savedInstanceState == null) {

            Bundle arguments = new Bundle();
            arguments.putString("artistId", getIntent().getStringExtra("artistId"));
            arguments.putString("artistName", getIntent().getStringExtra("artistName"));
            arguments.putBoolean("twoPane", false);

            TrackListFragment tf = new TrackListFragment();
            tf.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.track_container, tf)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_track_list, menu);
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
}
