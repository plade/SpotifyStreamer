package edu.buffalo.pratikla.spotifystreamer;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private ArrayAdapter<LinearLayout> mSearchAdapter;
    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        final EditText searchBar = (EditText) rootView.findViewById(R.id.search_bar);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_results);

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
        mSearchAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.list_item_results,
                R.id.list_item_results_linearlayout, new ArrayList<LinearLayout>());

        return rootView;
    }

    private void searchArtists(String key) {
        makeToast(key);
    }

    private void makeToast(String key) {
        Toast toast = Toast.makeText(getActivity(), key, Toast.LENGTH_SHORT);
        toast.show();
    }
}
