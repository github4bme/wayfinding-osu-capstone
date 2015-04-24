package com.osucse.wayfinding_osu_capstone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.osucse.wayfinding_api.BuildingCollection;
import com.osucse.wayfinding_api.TourCollection;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import com.google.android.gms.maps.model.MarkerOptions;
/**
 * This is the starting activity that consists of the map of the user's current location
 */
public class InitialActivity extends BaseActivity {

    private GoogleMap googleMap;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);
        super.onCreateDrawer();
        progress = new ProgressDialog(this);
        showMap();
    }

    /**
     * This creates a map for the initial screen and sets the map to the user's last known location.
     */
    private void showMap(){
        if (googleMap == null) {
            googleMap = ((SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map)).getMap();
        }
        googleMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker").snippet("Snippet"));

        // Enable MyLocation Layer of Google Map
        googleMap.setMyLocationEnabled(true);

        // Get LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Create a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Get the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // Get Current Location
        Location myLocation = locationManager.getLastKnownLocation(provider);

        // set map type
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Get latitude of the current location
        double latitude = myLocation.getLatitude();

        // Get longitude of the current location
        double longitude = myLocation.getLongitude();

        // Create a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);

        // Show the current location in Google Map
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
    }

    /**
     * This is if the big directions button is pushed
     * @param view
     */
    public void directionsButtonPressed (View view) {
        progress.setMessage("Loading... ");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
        new BuildingHttpRequestTask().execute();

    }

    public void toursButtonPressed (View view) {
        progress.setMessage("Loading... ");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
        new ToursHttpRequestTask().execute();

    }

    /**
     * Request task for getting the building list (This class also appears in BaseActivity along with
     * the associated Tours one, which is not needed from this screen)
     */
    private class BuildingHttpRequestTask extends AsyncTask<Void, Void, BuildingCollection> {
        @Override
        protected BuildingCollection doInBackground(Void... params) {
            return StartUpTasks.getBuildingsFromServer();
        }

        @Override
        protected void onPostExecute(BuildingCollection buildings) {
            //Clears loading screen
            progress.hide();
            Intent intent = new Intent(InitialActivity.this, SelectSourceLocation.class);
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
            //Clear loading bar
            progress.hide();
            Intent intent = new Intent(InitialActivity.this, SelectTour.class);
            startActivity(intent);
        }
    }
}
