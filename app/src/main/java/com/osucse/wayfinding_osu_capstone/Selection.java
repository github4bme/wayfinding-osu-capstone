package com.osucse.wayfinding_osu_capstone;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.osucse.wayfinding_api.Location;
import com.osucse.wayfinding_api.LocationCollection;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Selection extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
        new HttpRequestTask().execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_selection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // temp
        Intent intent;

        switch (id) {
            case R.id.action_directions:
                goToDirections();
                return true;

            case R.id.action_tours:
                goToTours();
                return true;

            case R.id.action_settings:
                goToSettings();
                return true;

            // for programming/debugging to get access to activies
            // to add an activity"
            //  1. add an entry to /res/menu/menu_selection.xml
            //  2. add a case here with an intent
            //  3. all activity parents should be selection unless a they are a subprocess
            //  4. ask tommy if you are confused.

            case R.id.action_main_activity:
                intent = new Intent(this, DisplayMapActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_activity_display_map_activity :
                intent = new Intent(this, DisplayMapActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_activity_location_list:
                intent = new Intent(this, LocationList.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // button handler methods
    public void directionsButtonPressed (View view) {
        goToDirections();
    }

    public void tourButtonPressed (View view) {
        goToTours();
    }

    public void settingsButtonPressed (View view) {
        goToSettings();
    }

    // intent switching methods
    private void goToDirections () {
        Intent intent = new Intent(this, SelectSourceLocation.class);
        startActivity(intent);
    }

    private void goToTours () {
        Intent intent = new Intent(this, SelectTour.class);
        startActivity(intent);
    }

    private void goToSettings () {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }




    private class HttpRequestTask extends AsyncTask<Void, Void, LocationCollection> {
        @Override
        protected LocationCollection doInBackground(Void... params) {
            StartUpTasks.getLocationCollectionFromServer();
            return StartUpTasks.getLocationCollection();
        }

        @Override
        protected void onPostExecute(LocationCollection locations) {
            StartUpTasks.createLocationList();
        }

    }
}
