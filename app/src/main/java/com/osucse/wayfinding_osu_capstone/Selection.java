package com.osucse.wayfinding_osu_capstone;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class Selection extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // action bar settings button pressed
            goToSettings();
            return true;

        } else if (id == R.id.action_directions) {
            // action bar directions pressed
            goToDirections();
            return true;

        } else if (id == R.id.action_tours) {
            // action bar tours pressed
            goToTours();
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

    }

    private void goToTours () {

    }

    private void goToSettings () {

    }
}
