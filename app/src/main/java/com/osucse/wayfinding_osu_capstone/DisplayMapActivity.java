package com.osucse.wayfinding_osu_capstone;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

// Josh added these imports for updating user's current location
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationServices;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DateFormat;
import java.util.*;


public class DisplayMapActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, OnMapReadyCallback {
    private static final LatLng DEST = new LatLng(39.9986444, -083.0150867);
    private static final LatLng NEXT = new LatLng(39.9985652, -083.0151295);
    private static final LatLng NEXT1 = new LatLng(39.9984717, -083.0151624);
    private static final LatLng NEXT2 = new LatLng(39.9983703, -083.0151790);
    private static final LatLng NEXT3 = new LatLng(39.9983301, -083.0151664);

    private static final String REQUESTING_LOCATION_UPDATES_KEY = "com.osucse.wayfinding_osu_capstone.DisplayMapActivity.requestingLocationUpdatesKey";
    private static final String LOCATION_KEY = "com.osucse.wayfinding_osu_capstone.DisplayMapActivity.locationKey";
    private static final String LAST_UPDATED_TIME_STRING_KEY = "com.osucse.wayfinding_osu_capstone.DisplayMapActivity.lastUpdatedTimeStringKey";

    protected GoogleMap ourMap;
    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;



    /****** to be deleted
     *
     */

    protected TextView latDisplay;
    protected TextView lngDisplay;
    protected TextView timeDisplay;

    protected android.location.Location mCurrentLocation;
    protected String mLastUpdateTime;
    /**/
    /** */
    //?????
    protected boolean mRequestingLocationUpdates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_map);

        Intent intent = getIntent();
        String startLocation = intent.getStringExtra(SelectDestinationLocation.SOURCE_LOCATION);
        String endLocation = intent.getStringExtra(SelectDestinationLocation.DESTINATION_LOCATION);

        TextView startLocationDisplay = (TextView) findViewById(R.id.start_location_display);
        startLocationDisplay.setTextSize(20);
        startLocationDisplay.setText("starting id: " + startLocation + " ending id:" + endLocation);


        /****** to be deleted
         *
         */
        latDisplay = (TextView) findViewById(R.id.lat_display);
        latDisplay.setTextSize(20);
        latDisplay.setText("lat: ");


        lngDisplay = (TextView) findViewById(R.id.lng_display);
        lngDisplay.setTextSize(20);
        lngDisplay.setText("lng: ");

        timeDisplay = (TextView) findViewById(R.id.time_display);
        timeDisplay.setTextSize(20);
        timeDisplay.setText("time: ");

        mRequestingLocationUpdates =  true;

        // To bring back saved state if activity is interrupted
        updateValuesFromBundle(savedInstanceState);

        // Creates Location Request with desired parameters
        createLocationRequest();

        // Used to build the client which allows current location updating
        buildGoogleApiClient();

        MapFragment map = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

        // Sets up a GoogleMap and calls onMapReady()
        map.getMapAsync(this);
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected synchronized void buildGoogleApiClient() {
        // Build API Client, add callbacks as needed
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
//        android.location.Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
//                mGoogleApiClient);

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }

//        if (mLastLocation != null) {
            // currentLocationMarker = ourMap.addMarker(new MarkerOptions().title("I'm Here" + iteration).position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())));
            // Marker currenLocationMarker = ourMap.addMarker(new MarkerOptions().title("I'm Here").position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())));
//        }
    }

    protected void startLocationUpdates() {
        // adds location listener "this" to our Api Client
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and
            // make sure that the Start Updates and Stop Updates buttons are
            // correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        REQUESTING_LOCATION_UPDATES_KEY);
                //setButtonsEnabledState();
            }

            // Update the value of mCurrentLocation from the Bundle and update the
            // UI to show the correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that
                // mCurrentLocationis not null.
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(
                        LAST_UPDATED_TIME_STRING_KEY);
            }
            updateUI();
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
                mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
    }

    private void updateUI() {
        latDisplay.setText(String.valueOf(mCurrentLocation.getLatitude()));
        lngDisplay.setText(String.valueOf(mCurrentLocation.getLongitude()));
        timeDisplay.setText(mLastUpdateTime);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // stops location updates when the app is in the background
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        // I am unclear as to why we use not mRequestingLocationUpdates, but I am following Google's
        // tutorial here
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i("Error:", "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i("Error:", "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // set created googleMap to our global map
        ourMap = googleMap;

        List<LatLng> path = new ArrayList<LatLng>();
        path.add(DEST);
        path.add(NEXT);
        path.add(NEXT1);
        path.add(NEXT2);
        path.add(NEXT3);

        addLines(path);
        ourMap.setMyLocationEnabled(true);
        ourMap.addMarker(new MarkerOptions().title("Destination").position(DEST));
    }

    private void addLines(List<LatLng> path) {
        int pathSize = path.size();
        int current = 0;
        LatLng start = null;
        LatLng end = null;
        for (int i =1; i < pathSize; i++){
            if (start == null) {
                start = path.get(current);
            }else{
                start = end;
            }
            current++;
            end = path.get(current);
            ourMap
                    .addPolyline((new PolylineOptions())
                            .add(start, end).width(5).color(Color.BLUE)
                            .geodesic(true));
            // move camera to zoom on map
            ourMap.moveCamera(CameraUpdateFactory.newLatLngZoom(start,
                    18));
        }
    }
}
