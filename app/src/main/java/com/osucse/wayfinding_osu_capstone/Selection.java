package com.osucse.wayfinding_osu_capstone;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.osucse.wayfinding_api.BuildingCollection;


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
    }

    /**
     * Handler for when the directions_button is pressed on screen.
     * @param view a required input for the button
     */
    public void directionsButtonPressed (View view) {
        goToDirections();
    }

    /**
     * Handler for when the tours_button is pressed on screen.
     * @param view a required input for the button
     */
    public void tourButtonPressed (View view) {
        goToTours();
    }

    /**
     * Handler for when the settings_button is pressed on screen.
     * @param view a required input for the button
     */
    public void settingsButtonPressed (View view) {
        goToSettings();
    }

    /**
     * Starts an intent for the SelectSourceLocation activity, which
     * starts the navigation sub-process of the app.
     */
    private void goToDirections () {
        TextView loadingListDisplay = (TextView) findViewById(R.id.loading_list_display);
        loadingListDisplay.setTextSize(20);
        loadingListDisplay.setText("Loading...");
        // Get list with http request then go to next activity after list is retrieved
        new HttpRequestTask().execute();
    }

    /**
     * Starts an intent for the SelectTour activity, which starts
     * the tour sub-process of the app.
     */
    private void goToTours () {
        Intent intent = new Intent(this, SelectTour.class);
        startActivity(intent);
    }

    /**
     * Starts an intent for the Settings activity, which allows
     * users to change characteristics of the navigation.
     */
    private void goToSettings () {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    /**
     * A private internal class that handles updating the internal class StartUpTasks
     */
    private class HttpRequestTask extends AsyncTask<Void, Void, BuildingCollection> {
        @Override
        protected BuildingCollection doInBackground(Void... params) {
            return BuildingListAdapter.getBuildingsFromServer();
        }

        @Override
        protected void onPostExecute(BuildingCollection buildings) {
            // Clears the "Loading..." message so it is not there when you navigate back to this screen from Map Display
            // This has never given me a threading error, but it is possible that it could
            TextView loadingListDisplay = (TextView) Selection.this.findViewById(R.id.loading_list_display);
            loadingListDisplay.setTextSize(20);
            loadingListDisplay.setText("");
            Intent intent = new Intent(Selection.this, SelectSourceLocation.class);
            startActivity(intent);
        }
    }
}
