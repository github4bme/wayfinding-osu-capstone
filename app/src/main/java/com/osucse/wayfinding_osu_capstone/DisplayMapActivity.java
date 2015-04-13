package com.osucse.wayfinding_osu_capstone;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.AsyncTask;
import android.provider.Telephony;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;
import android.util.Log;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.ConnectionResult;
import android.hardware.SensorEventListener;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.content.Context;
import android.hardware.SensorEvent;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.math.BigDecimal;
import java.util.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import com.osucse.wayfinding_api.*;
import static com.google.maps.android.SphericalUtil.computeDistanceBetween;

public class DisplayMapActivity extends FragmentActivity implements SensorEventListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final String URL = "http://54.200.238.22:9000/";
    private static final String CURRENT_LOCATION_KEY = "com.osucse.wayfinding_osu_capstone.DisplayMapActivity.currentLocationKey";
    private static final String NEXT_DESTINATION_KEY = "com.osucse.wayfinding_osu_capstone.DisplayMapActivity.nextDestinationKey";
    private static final float AT_LOCATION_RADIUS = 10.0f;

    private GoogleMap ourMap;
    private Marker nextDestMarker;
    List<LatLng> ourRoute = new ArrayList<LatLng>();

    // Used for location services
    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;

    // Used for orientation of the phone
    private SensorManager mSensorManager;
    private Sensor mSensor;

    protected TextView textMessageDisplay;
    protected ImageView arrowImage;
    protected TextView distanceTV;
    protected TextView etaTV;

    protected String startLocation;
    protected String endLocation;
    protected boolean routeGenUsesCurrLoc;
    protected boolean routeNeeded;

    protected android.location.Location mCurrentLocation;
    protected float bearingToDestDegrees;
    protected float currBearing;
    // Set to 0, 0 for rare case of null pointer exception
    protected LatLng mNextDestination = new LatLng(0.0, 0.0);

    /******
     *  Boolean value for running on emulator
     */
    protected boolean runningOnEmulator = false;
    /***********/

    // Variable that could be used for turning location updates on and off
    // This might be helpful for running on emulator if we find that the updates cause problems
    protected boolean mRequestingLocationUpdates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_map);
        Intent intent = getIntent();

        textMessageDisplay = (TextView) findViewById(R.id.text_message_map_display);
        textMessageDisplay.setTextSize(20);
        textMessageDisplay.setText("OSU Wayfinding Application");
        arrowImage = (ImageView) findViewById(R.id.arrow_image);
        distanceTV = (TextView) findViewById(R.id.distanceTextView);
        etaTV = (TextView) findViewById(R.id.etaTextView);

        startLocation = intent.getStringExtra(SelectDestinationLocation.SOURCE_LOCATION);
        endLocation = intent.getStringExtra(SelectDestinationLocation.DESTINATION_LOCATION);

        // boolean used primarily for using current location for route
        routeNeeded = true;
        // Set boolean for ordering of asynchronous operations
        // if true get current location THEN get route and build map
        // else (get current location) AND (get route and build map) in parallel
        // NOTE: Uses parseInt() for comparison because comparison to String "-1" was strangely failing
        routeGenUsesCurrLoc = (Integer.parseInt(startLocation) == -1);

        // if not called here then called in onConnected
        if (!routeGenUsesCurrLoc) {
            new HttpRequestTask().execute();
        }

        // Set to true for all cases because we do not have any reason to turn these updates off
        // E.g. a setting to disable location information
        mRequestingLocationUpdates =  true;

        // Creates Location Request with desired parameters
        createLocationRequest();

        // Used to build the client which allows current location updating
        buildGoogleApiClient();

        // Set from null to stop initial null pointer exception in onSensorChanged() calling updateUI() before API
        // client has connected to set our user's current location
        mCurrentLocation = createAndroidLocation(new LatLng(0.0, 0.0));

        if (!runningOnEmulator) {
            // Used for getting orientation of phone
            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        }

        // To bring back saved state if activity is interrupted
        updateValuesFromBundle(savedInstanceState);
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, Route> implements OnMapReadyCallback {
        @Override
        protected Route doInBackground(Void... params) {
            try {
                String url;
                if (routeGenUsesCurrLoc) {
                    url = URL + "generateRouteCurrent?dest=" + endLocation + "&currlat=" +
                            Double.toString(mCurrentLocation.getLatitude()) + "&currlong=" +
                            Double.toString(mCurrentLocation.getLongitude());
                } else {
                    url = URL + "generateRoute?from=" + startLocation + "&to=" + endLocation;
                }
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                Route collection = restTemplate.getForObject(url, Route.class);
                return collection;
            } catch (Exception e) {
                Log.e("Route", e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Route collection) {
            if (collection != null) {
                final List<Node> routePoints = collection.getRoute();

                // Fills ourRoute with our path's lat/long coordinates
                for (int i = 0; i < routePoints.size(); i++) {
                    ourRoute.add(new LatLng(routePoints.get(i).getCoordinate().getLatitude(),
                            routePoints.get(i).getCoordinate().getLongitude()));
                }

                // Set first destination to the start of the route
                mNextDestination = ourRoute.get(0);

                MapFragment map = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

                // Sets up a non-null GoogleMap and calls onMapReady()
                map.getMapAsync(this);
            }
            else{
                // if no route generated, show error and go to home screen
                AlertDialog.Builder noRoute = new AlertDialog.Builder(DisplayMapActivity.this);
                noRoute.setTitle("Error");
                noRoute.setMessage("There is no possible route between these points.");
                noRoute.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        finish();
                    }
                });
                noRoute.show();
            }
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            // This is called when our getMapAsync() in onCreate() successfully gets a map
            // Set created googleMap to our global map
            ourMap = googleMap;

            plotRoute();
            ourMap.setMyLocationEnabled(true);
            ourMap.getUiSettings().setMapToolbarEnabled(false);

            // Set first marker to show the start of the route
            nextDestMarker = ourMap.addMarker(new MarkerOptions().title("Next Destination").position(mNextDestination));
        }
    }

    /**
     * Plots the route on the map
     */
    private void plotRoute() {
        // move camera to zoom on map to starting location
        ourMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ourRoute.get(0),
                17));

        // Loop puts a line between all points in ourRoute
        // Loop is kept so that we do not start a line at the last point
        for (int i = 0; i < ourRoute.size() - 1; i++){
            ourMap.addPolyline((new PolylineOptions()).add(ourRoute.get(i), ourRoute.get(i + 1))
                    .width(5).color(Color.BLUE).geodesic(true));
        }
    }

    // Defines how and when our location updates are made
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        // Original settings for later comparison
//        mLocationRequest.setInterval(10000);
//        mLocationRequest.setFastestInterval(5000);
        
        // Set update rate to as fast as possible
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
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
        // Set user's current location
//        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(
//                mGoogleApiClient);
//
//        // if route gen uses current location then current location must be found first
//        if (routeGenUsesCurrLoc) {
//            new HttpRequestTask().execute();
//        }

//        checkNextDestUpdateUI();

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    protected void startLocationUpdates() {
        // Adds location listener "this" to our Api Client
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(android.location.Location currentLocation) {
        // This is called anytime the location is detected as changed
        mCurrentLocation = currentLocation;

        // if route gen uses current location then current location must be found first
        if (routeNeeded && routeGenUsesCurrLoc) {
            new HttpRequestTask().execute();
            // set so do not make http request again
            routeNeeded = false;
        }

        checkNextDestUpdateUI();

        recalculateDistanceAndETA();
    }

    private void recalculateDistanceAndETA() {
        double distanceLeftInMeters = 0;

        distanceLeftInMeters = computeDistanceBetween(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), mNextDestination);

        // Loops from index of nextDestination in route to the end adding up distances
        for(int i = ourRoute.indexOf(mNextDestination); i < ourRoute.size() - 1; i++)
        {
            distanceLeftInMeters += computeDistanceBetween(ourRoute.get(i), ourRoute.get(i + 1));
        }

        double distanceLeftInMiles = distanceLeftInMeters / 1609.344;

        distanceTV.setText(String.format("%.3f", distanceLeftInMiles) + " mi. remaining");
        etaTV.setText(Math.round(distanceLeftInMiles / 3.1 * 60) + " minutes");
    }

    /**
     * Method that checks to see if user is within AT_LOCATION_RADIUS distance from any point
     * on route; if the user is, then method sets mNextDestination to next point on route
     *
     * NOTE: It is possible to go backwards on the route - this method will always try to point
     * you to the next node as compared to where you currently are
     */
    private void checkNextDestUpdateUI() {
        LatLng tempDest = mNextDestination;

        // Loop checks to see if the user is close to a point on the route
        for (int i = 0; i < ourRoute.size() - 1; i++) {
            float distance = mCurrentLocation.distanceTo(createAndroidLocation(ourRoute.get(i)));
            if (distance < AT_LOCATION_RADIUS) {
                mNextDestination = ourRoute.get(i + 1);
            }
        }
        if (ourMap != null && nextDestMarker != null && tempDest != mNextDestination) {
            nextDestMarker.remove();
            nextDestMarker = ourMap.addMarker(new MarkerOptions().title("Next Destination").position(mNextDestination));
        }

        bearingToDestDegrees = mCurrentLocation.bearingTo(createAndroidLocation(mNextDestination));
        updateUI();
    }

    private void updateUI() {
        // Need to rotate the arrow by the difference of the two bearings
        float arrowRotation = bearingToDestDegrees - currBearing;
        arrowImage.setRotation(arrowRotation);
    }

    // Code used to repopulate necessary fields if the Activity is interrupted
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mCurrentLocation from the Bundle
            if (savedInstanceState.keySet().contains(CURRENT_LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that
                // mCurrentLocationis not null.
                mCurrentLocation = savedInstanceState.getParcelable(CURRENT_LOCATION_KEY);
            }

            // Update the value of mNextDestination from the Bundle
            if (savedInstanceState.keySet().contains(NEXT_DESTINATION_KEY)) {
                mNextDestination = savedInstanceState.getParcelable(NEXT_DESTINATION_KEY);
            }
            bearingToDestDegrees = mCurrentLocation.bearingTo(createAndroidLocation(mNextDestination));
            updateUI();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Saves all of the pertinent information if Activity is interrupted
        savedInstanceState.putParcelable(CURRENT_LOCATION_KEY, mCurrentLocation);
        savedInstanceState.putParcelable(NEXT_DESTINATION_KEY, mNextDestination);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stops location updates when the app is in the background
        stopLocationUpdates();

        if (!runningOnEmulator) {
            mSensorManager.unregisterListener(this);
        }
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        // I am unclear as to why we use 'not' mRequestingLocationUpdates, but I am following Google's
        // tutorial here
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }

        if (!runningOnEmulator) {
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
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
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // I am unsure why the accuracy would change.  It is possible it is changing in order to suit the
        // device
        Log.i("Unknown", "*********************\nAccuracy Changed for Sensor\n*********************");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // This is called every time a change in the device's orientation is detected
        // Because this will be called very often, the logic should be kept simple

        // The even.values[2] gives sin(theta/2) where theta is the rotation about the z axis
        // Need to do this calculation to get the rotation, theta, in degrees
        currBearing = (float) (Math.toDegrees(Math.asin((double) event.values[2])) * -2.0);
        updateUI();
    }

    // Simple helper method for converting from LatLng to an android.location.Location
    private android.location.Location createAndroidLocation(LatLng point) {
        // Provider name is unnecessary
        android.location.Location newLocation = new android.location.Location("");
        newLocation.setLatitude(point.latitude);
        newLocation.setLongitude(point.longitude);
        return newLocation;
    }
}