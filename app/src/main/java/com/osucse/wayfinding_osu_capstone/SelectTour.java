package com.osucse.wayfinding_osu_capstone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.osucse.wayfinding_api.Building;
import com.osucse.wayfinding_api.Tour;

import java.util.ArrayList;


public class SelectTour extends BaseActivity {

    // instance variables
    public ArrayList<Tour> tours;
    public ArrayAdapter<Tour> adapter;
    public ListView listView;
    public EditText editText;

    // memory location to pass between intents
    public final static String       TOUR = "com.osucse.wayfinding_osu_capstone.TOUR";

    // static variable to kill activity with an intent
    public final static String      KILL_TOUR = "KILL_TOUR";

    // broadcastReceiver for killing the activity remotely
    private final BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent intent) {
            String action = intent.getAction();
            if (action.equals(KILL_TOUR)) {
                killActivity();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_tour);
        super.onCreateDrawer();

        // add a broadcast reliever for killing the activity remotely
        registerReceiver(receiver, new IntentFilter(KILL_TOUR));

        // connect activity to layout items
        listView = (ListView) findViewById(R.id.tour_list);
        editText = (EditText) findViewById(R.id.tour_list_search);

        // gets copy of ordered list of locations
        tours = BuildingListAdapter.cloneTourList();

        // creates adapter and attaches it to the listView
        adapter = new ArrayAdapter<Tour>(this, android.R.layout.simple_list_item_1, tours);
        listView.setAdapter(adapter);

        // create listener for the editText and have it filter the list on input changes
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SelectTour.this.adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // create a listener for when an item is selected in the list
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // get the id of the selected item
                String selectedItem = Integer.toString(((Tour) (parent.getItemAtPosition(position))).getId());

                // create an intent
                Intent intent = new Intent(SelectTour.this, DisplayMapActivity.class);

                // add the selected id to intent
                intent.putExtra(TOUR, selectedItem);

                // start the intent
                startActivity(intent);
            }
        });
    }

    /**
     * Simple private class that is used to kill this activity.
     */
    private void killActivity () {
        this.unregisterReceiver(receiver);
        this.finish();
    }
}
