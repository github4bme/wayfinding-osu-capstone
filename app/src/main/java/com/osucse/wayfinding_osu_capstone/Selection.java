package com.osucse.wayfinding_osu_capstone;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;

import com.osucse.wayfinding_api.LocationCollection;


/**
 * This is the starting activity. It provides the user the choice to get directions,
 * take a tour of the university, or to change their settings.
 *
 * Requires files:
 * layout/activity_selection.xml
 */
public class Selection extends ActionBarActivity {

    /**
     * onCreate - executes the private httpRequestTask()
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
        new HttpRequestTask().execute();
    }

    /**
     * Handler for when the direction
     * @param view
     */
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

    /**
     * A private internal class that handles updating the internal class StartUpTasks
     */
    private class HttpRequestTask extends AsyncTask<Void, Void, LocationCollection> {
        @Override
        protected LocationCollection doInBackground(Void... params) {
            StartUpTasks.getLocationCollectionFromServer();
            return StartUpTasks.getLocationCollection();
        }

        @Override
        protected void onPostExecute(LocationCollection locations) {
            //
        }

    }
}
