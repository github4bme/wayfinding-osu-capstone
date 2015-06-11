package com.osucse.wayfinding_osu_capstone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
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
public class InitialActivity extends BaseActivity implements OnMapReadyCallback {

    private GoogleMap ourMap;
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
        MapFragment mapFrag = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

        // Sets up a non-null GoogleMap and calls onMapReady()
        mapFrag.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // This is called when our getMapAsync() in showMap() successfully gets a map
        // Set created googleMap to our global map
        ourMap = googleMap;

        // Enable MyLocation Layer of Google Map
        ourMap.setMyLocationEnabled(true);

        // Turns off Google toolbar if a marker is selected
        ourMap.getUiSettings().setMapToolbarEnabled(false);
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
            return BuildingListAdapter.getBuildingsFromServer();
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
            return BuildingListAdapter.getToursFromServer();
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
