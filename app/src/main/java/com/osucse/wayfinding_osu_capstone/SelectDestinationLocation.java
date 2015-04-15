package com.osucse.wayfinding_osu_capstone;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.osucse.wayfinding_api.Building;

import java.util.ArrayList;

public class SelectDestinationLocation extends ActionBarActivity {

    // instance variables
    public ArrayAdapter<Building>   adapter;
    public ArrayList <Building>     destinations;
    public ListView                 listView;
    public EditText                 editText;
    public String                   incomingSource;

    // memory location to pass between intents
    public final static String      SOURCE_LOCATION
            = "com.osucse.wayfinding_osu_capstone.SOURCE";
    public final static String      DESTINATION_LOCATION
            = "com.osucse.wayfinding_osu_capstone.DESTINATION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_destination_location);


        Intent intent = getIntent();
        incomingSource = intent.getStringExtra(SelectSourceLocation.SOURCE_LOCATION);

        // connect activity to layout items
        listView = (ListView) findViewById(R.id.destination_list);
        editText = (EditText) findViewById(R.id.destination_list_search);

        // creates a clone of the location list
        destinations = BuildingListAdapter.cloneBuildingList();

        // creates adapter and attaches it to the listView
        adapter = new ArrayAdapter<Building>(this, android.R.layout.simple_list_item_1, destinations);
        listView.setAdapter(adapter);

        // create listener for the editText and have it filter the list on input changes
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SelectDestinationLocation.this.adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // create a listener for when an item is selected in the list
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // get the id of the selected item
                String selectedItem = Integer.toString(((Building)(parent.getItemAtPosition(position))).getBuildingId());

                // create an intent
                Intent intent = null;

                if (Settings.getVisualSetting()){
                    intent = new Intent(SelectDestinationLocation.this, DisplayArrowActivity.class);
                } else {
                    intent = new Intent(SelectDestinationLocation.this, DisplayMapActivity.class);
                }


                // add the selected id to intent
                intent.putExtra(SOURCE_LOCATION, incomingSource);
                intent.putExtra(DESTINATION_LOCATION, selectedItem);

                // start the intent
                startActivity(intent);

                // finish the SelectSource & SelectDestination activities
                intent = new Intent(SelectSourceLocation.KILL_SELECT_DESTINATION);
                sendBroadcast(intent);
                SelectDestinationLocation.this.finish();
            }
        });
    }
}
