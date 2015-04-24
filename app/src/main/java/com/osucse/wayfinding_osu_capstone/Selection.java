package com.osucse.wayfinding_osu_capstone;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.osucse.wayfinding_api.BuildingCollection;
import com.osucse.wayfinding_api.TourCollection;


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

        // Should only do this once in order to create the settings for the other
        // activities to use
        if (Settings.settings == null) {
            // connect to preferences file
            Settings.settings = getPreferences(MODE_PRIVATE);
        }
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
        new BuildingHttpRequestTask().execute();
    }

    /**
     * Starts an intent for the SelectTour activity, which starts
     * the tour sub-process of the app.
     */
    private void goToTours () {
        TextView loadingListDisplay = (TextView) findViewById(R.id.loading_list_display);
        loadingListDisplay.setTextSize(20);
        loadingListDisplay.setText("Loading...");
        // Get list with http request then go to next activity after list is retrieved
        new ToursHttpRequestTask().execute();
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
    private class BuildingHttpRequestTask extends AsyncTask<Void, Void, BuildingCollection> {
        @Override
        protected BuildingCollection doInBackground(Void... params) {
            return StartUpTasks.getBuildingsFromServer();
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

    /**
     * A private internal class that handles updating the internal class StartUpTasks
     */
    private class ToursHttpRequestTask extends AsyncTask<Void, Void, TourCollection> {
        @Override
        protected TourCollection doInBackground(Void... params) {
            return StartUpTasks.getToursFromServer();
        }

        @Override
        protected void onPostExecute(TourCollection tours) {
            // Clears the "Loading..." message so it is not there when you navigate back to this screen from Map Display
            // This has never given me a threading error, but it is possible that it could
            TextView loadingListDisplay = (TextView) Selection.this.findViewById(R.id.loading_list_display);
            loadingListDisplay.setTextSize(20);
            loadingListDisplay.setText("");
            Intent intent = new Intent(Selection.this, SelectTour.class);
            startActivity(intent);
        }
    }
}
