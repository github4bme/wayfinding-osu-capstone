package com.osucse.wayfinding_osu_capstone;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.util.Log;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.osucse.utilities.Coordinate;
import com.osucse.wayfinding_api.*;
import static com.google.maps.android.SphericalUtil.computeDistanceBetween;

public class DisplayMapActivity extends BaseActivity implements SensorEventListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final String URL = "http://54.200.238.22:9000/";
    private static final String CURRENT_LOCATION_KEY = "com.osucse.wayfinding_osu_capstone.DisplayMapActivity.currentLocationKey";
    private static final String NEXT_DESTINATION_KEY = "com.osucse.wayfinding_osu_capstone.DisplayMapActivity.nextDestinationKey";
    private static final String TOUR_KEY = "com.usecse.wayfinding_osu_capstone.DisplayMapActivity.tourKey";
    private static final float AT_LOCATION_RADIUS = 10.0f;
    // user has arrived radius has a 5 meter buffer because being close to the building tends to
    // make the phone lose the user's location right before the user has arrived message appears
    private static final float USER_HAS_ARRIVED_RADIUS = AT_LOCATION_RADIUS + 5.0f;
    private static final float  PATH_GAP_COMPARISON = AT_LOCATION_RADIUS;
    // this is in meters and is equal to 250ft
    private static final float FARTHEST_ALLOWED_FROM_PATH = 76.2f;
    private static final float STRAIGHT_ALLOWED_ANGLE_DIFF = 10.0f;
    private static final String CALCULATING_ROUTE_MESSAGE = "Calculating Route... ";
    private static final String RECALCULATING_ROUTE_MESSAGE = "Recalculating Route... ";

    private GoogleMap ourMap;
    private Marker nextDestMarker;
    private Marker userLocationArrow;
    private List<Polyline> polylineList = new ArrayList<Polyline>();
    List<LatLng> ourRoute = new ArrayList<LatLng>();

    // Used for location services
    protected GoogleApiClient googleApiClient;
    protected LocationRequest locationRequest;

    // Used for orientation of the phone
    private SensorManager sensorManager;
    private Sensor sensor;

    protected ImageView arrowImage;
    protected TextView distanceTV;
    protected TextView etaTV;

    protected String startLocation;
    protected String endLocation;
    protected String tourId;
    protected boolean routeGenUsesCurrLoc;
    protected boolean routeNeeded;

    protected LatLng finalLocation;

    protected android.location.Location currentLocation;
    protected float bearingToDestDegrees;
    protected float currBearing;
    // Set to 0, 0 for rare case of null pointer exception
    protected LatLng nextDestination = new LatLng(0.0, 0.0);

    /******
     *  Boolean value for running on emulator
     */
    protected boolean runningOnEmulator = false;
    /***********/

    // Variable that could be used for turning location updates on and off
    // This might be helpful for running on emulator if we find that the updates cause problems
    protected boolean requestingLocationUpdates;

    // Sizes and animation needed for arrow animation
    protected float lastRotation = 0.0f;
    protected float newScaleSize = 1.0f;
    protected float currentScaleSize = 1.0f;
    protected AnimationSet animationSet;

    protected AlertDialog.Builder hasArrivedMessage = null;
    protected ProgressDialog loadingMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_map);
        super.onCreateDrawer();
        Intent intent = getIntent();

        // builds for loading message when requesting route
        loadingMessage = new ProgressDialog(this);
        // message is started on create because any sort of request (e.g. tours, A to B route, current location
        // route) will be making a request from the start or waiting for a location change which should still have
        // the "Calculating Route... " message
        showProgressionMessage(CALCULATING_ROUTE_MESSAGE);

        // Ordered such that Hints and Tips is placed over top of the "Calculating Route... " message
        showHintsAndTips();


        arrowImage = (ImageView) findViewById(R.id.arrow_image);
        distanceTV = (TextView) findViewById(R.id.distanceTextView);
        etaTV = (TextView) findViewById(R.id.etaTextView);

        startLocation = intent.getStringExtra(SelectDestinationLocation.SOURCE_LOCATION);
        endLocation = intent.getStringExtra(SelectDestinationLocation.DESTINATION_LOCATION);
        tourId = intent.getStringExtra(SelectTour.TOUR);

        // boolean used primarily for using current location for route
        routeNeeded = true;

        // This logic is to decide which http request needs to be made
        if(tourId != null) {
            new HttpRequestTask().execute(getTourRouteURL());
            routeGenUsesCurrLoc = false;
        } else {
            // Set boolean for ordering of asynchronous operations
            // if true get current location THEN get route and build map
            // else (get current location) AND (get route and build map) in parallel
            // NOTE: Uses parseInt() for comparison because comparison to String "-1" was strangely failing
            routeGenUsesCurrLoc = (Integer.parseInt(startLocation) == -1);

            // if not called here then called in onConnected
            if (!routeGenUsesCurrLoc) {
                new HttpRequestTask().execute(getStartEndRouteURL());
            }
        }

        // Set to true for all cases because we do not have any reason to turn these updates off
        // E.g. a setting to disable location information
        requestingLocationUpdates =  true;

        // Creates Location Request with desired parameters
        createLocationRequest();

        // Used to build the client which allows current location updating
        buildGoogleApiClient();

        // Set from null to stop initial null pointer exception in onSensorChanged() calling updateUI() before API
        // client has connected to set our user's current location
        currentLocation = createAndroidLocation(new LatLng(0.0, 0.0));

        if (!runningOnEmulator) {
            // Used for getting orientation of phone
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        }

        // To bring back saved state if activity is interrupted
        updateValuesFromBundle(savedInstanceState);


        // The arrow's default size is set to small; this code checks the visually impaired setting;
        // If set then arrow should be toggled to large; Must use Global Listener to wait for
        // entire layout to be loaded before arrow is toggled
        if (Settings.getVisualSetting()) {
            final LinearLayout overallLayout = (LinearLayout) findViewById(R.id.overall_linear_layout);
            ViewTreeObserver treeObserver = overallLayout.getViewTreeObserver();
            treeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    toggleArrowSize(arrowImage);

                    overallLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }
    }

    /**
     * Method to start showing the progression message with the spinning wheel
     */
    private void showProgressionMessage(String message) {
        // Starts loading message
        if (loadingMessage != null) {
            loadingMessage.setMessage(message);
            loadingMessage.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            loadingMessage.setIndeterminate(true);
            loadingMessage.show();
        }
    }

    private void showHintsAndTips() {
        if (Settings.getShowMapHintsSetting()) {
            AlertDialog.Builder hintsAndTipsDialog = new AlertDialog.Builder(DisplayMapActivity.this);
            hintsAndTipsDialog.setTitle("Hints and Tips");
            hintsAndTipsDialog.setMessage("The arrow works more efficiently if held parallel to the ground like a compass. Click on the arrow to enlarge it.");
            hintsAndTipsDialog.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            hintsAndTipsDialog.setPositiveButton("Do Not Show Again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    Settings.setShowMapHintsSetting(false);
                }
            });
            hintsAndTipsDialog.show();
        }
    }

    private class HttpRequestTask extends AsyncTask<String, Void, Route> implements OnMapReadyCallback {
        @Override
        protected Route doInBackground(String... params) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                // makes request with input url string
                Route collection = restTemplate.getForObject(params[0], Route.class);
                return collection;
            } catch (Exception e) {
                Log.e("Route", e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Route collection) {
            // takes away loading message before showing route or error message
            if (loadingMessage != null) {
                loadingMessage.hide();
            }
            if (collection != null && collection.getErrorMsg() == null) {
                final List<Coordinate> routePoints = collection.getRoute();
                // Filters and fills ourRoute with our path's lat/long coordinates
                filterAndPopulateRoute(routePoints);

                // Set first destination to the start of the route
                nextDestination = ourRoute.get(0);

                int lastIndex = ourRoute.size() - 1;
                finalLocation = ourRoute.get(lastIndex);

                MapFragment map = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

                // Sets up a non-null GoogleMap and calls onMapReady()
                map.getMapAsync(this);
            } else {
                String message;
                if (collection != null) {
                    message = collection.getErrorMsg();
                } else {
                    message = "An unknown error has occurred.";
                }
                AlertDialog.Builder errorDialog = new AlertDialog.Builder(DisplayMapActivity.this);
                errorDialog.setTitle("Error");
                errorDialog.setMessage(message);
                errorDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                errorDialog.show();
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
            adjustMarkerPosition();
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
            Polyline segment = ourMap.addPolyline((new PolylineOptions()).add(ourRoute.get(i), ourRoute.get(i + 1))
                    .width(7).color(Color.BLUE).geodesic(true));
            polylineList.add(segment);
        }
    }

    // Defines how and when our location updates are made
    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        // Original settings for later comparison
//        locationRequest.setInterval(10000);
//        locationRequest.setFastestInterval(5000);
        
        // Set update rate to as fast as possible
        locationRequest.setInterval(0);
        locationRequest.setFastestInterval(0);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected synchronized void buildGoogleApiClient() {
        // Build API Client, add callbacks as needed
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (requestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    protected void startLocationUpdates() {
        // Adds location listener "this" to our Api Client
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);
    }

    @Override
    public void onLocationChanged(android.location.Location currentLocation) {
        // This is called anytime the location is detected as changed
        this.currentLocation = currentLocation;

        // hasArrivedMessage null check in order to not show multiple messages
        // finalLocation null check needed if http request has yet to set finalLocation
        if(hasArrivedMessage == null
                && finalLocation != null
                && this.currentLocation.distanceTo(createAndroidLocation(finalLocation)) < USER_HAS_ARRIVED_RADIUS) {
            hasArrivedMessage = new AlertDialog.Builder(DisplayMapActivity.this);
            hasArrivedMessage.setTitle("Arrived");
            hasArrivedMessage.setMessage("You Have Arrived!");
            hasArrivedMessage.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which){
                    finish();
                }
            });
            hasArrivedMessage.show();
        }

        // if route gen uses current location then current location must be found first
        if (routeNeeded && routeGenUsesCurrLoc) {
            new HttpRequestTask().execute(getCurrLocRouteURL());
            // set so do not make http request again
            routeNeeded = false;
        }

        checkNextDestUpdateUI();

        recalculateDistanceAndETA();
    }

    private void recalculateDistanceAndETA() {
        double distanceLeftInMeters = 0;

        distanceLeftInMeters = computeDistanceBetween(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), nextDestination);

        // Loops from index of nextDestination in route to the end adding up distances
        for(int i = ourRoute.indexOf(nextDestination); i < ourRoute.size() - 1; i++)
        {
            distanceLeftInMeters += computeDistanceBetween(ourRoute.get(i), ourRoute.get(i + 1));
        }

        double distanceLeftInMiles = distanceLeftInMeters / 1609.344;

        distanceTV.setText(String.format("%.3f", distanceLeftInMiles) + " mi. remaining");
        etaTV.setText(Math.round(distanceLeftInMiles / 3.1 * 60) + " minutes");
    }

    /**
     * Method that checks to see if user is within AT_LOCATION_RADIUS distance from any point
     * on route; if the user is, then method sets nextDestination to next point on route
     *
     * NOTE: It is possible to go backwards on the route - this method will always try to point
     * you to the next node as compared to where you currently are
     */
    private void checkNextDestUpdateUI() {
        // Needed for checking to see if next destination has moved
        LatLng tempDest = nextDestination;

        // Gets which "oval" around the path the user is currently in and changes the nextDestination accordingly
        for (int i = 0; i < ourRoute.size() - 1; i++) {
            Location node = createAndroidLocation(ourRoute.get(i));
            Location nextNode = createAndroidLocation(ourRoute.get(i + 1));

            // Gets the oval the user is currently in which is farthest along the route
            if (isWithinOval(currentLocation, node, nextNode, PATH_GAP_COMPARISON, AT_LOCATION_RADIUS)) {
                nextDestination = ourRoute.get(i + 1);
            }
        }

        // Checks to see if user is too far from current route segment
        checkMaxDistForRecalculation();

        // if next destination changed then move marker
        if (ourMap != null && nextDestMarker != null && tempDest != nextDestination) {
            adjustMarkerPosition();
        }

        bearingToDestDegrees = currentLocation.bearingTo(createAndroidLocation(nextDestination));
        updateUI();
    }

    /**
     * Method to set our nextDestMarker
     * Prereqs: ourMap is not null
     */
    private void adjustMarkerPosition() {
        if (nextDestMarker != null) {
            nextDestMarker.remove();
        }

        nextDestMarker = ourMap.addMarker(new MarkerOptions()
                .title("Next Destination")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(nextDestination));
    }

    private void updateUI() {
        // Need to rotate the arrow by the difference of the two bearings
        float arrowRotation = bearingToDestDegrees - currBearing;

        // null check is needed to short circuit the condition the first time to not get null exception
        // check to make sure animation has ended
        if (animationSet == null || animationSet.hasEnded()) {
            animateArrow(arrowRotation);
        } else {
            // do nothing until animation is done
        }

        // Called here because it is based on location and sensor changes
        setUserLocationArrow();
    }

    /**
     * Method to check and see if the user's current location arrow should be put on the route
     * line; if it should it places it and points it based on the user's current bearing
     */
    private void setUserLocationArrow() {
        Location node = null;
        Location nextNode = null;
        LatLng setArrowLocation = null;
        if (hasReachedRouteStart()) {
            // if user has reached route start then none of these values will be out of bounds
            int nextNodeIndex = ourRoute.indexOf(nextDestination);
            int nodeIndex = nextNodeIndex - 1;
            node = createAndroidLocation(ourRoute.get(nodeIndex));
            nextNode = createAndroidLocation(ourRoute.get(nextNodeIndex));

            float routeBearing = node.bearingTo(nextNode);
            float bearingToUser = node.bearingTo(currentLocation);
            double angleBetween = (double) (bearingToUser - routeBearing);
            // This returns the distance along the path towards the next node of the user's projected position
            // onto the route
            double currentDistFromNode = Math.abs(Math.cos(Math.toRadians(angleBetween)) * node.distanceTo(currentLocation));
            setArrowLocation = getLocBetweenNodes(node, nextNode, currentDistFromNode);
        }

        // hasReachedStart() being true would mean all variables are not null
        // if within 'oval' show arrow along path
        if (hasReachedRouteStart() && isWithinOval(currentLocation, node, nextNode, PATH_GAP_COMPARISON, 0.0f)) {
            // put marker at the projected location
            placeUserLocationMarker(setArrowLocation);
        }
        // not in oval, but still close enough to a node, put the user's arrow on that node;
        // added an offset of 2.0f for the "edges" of the next destination oval; if on route this won't get called
        // because this is the 'else if', the first 'if' with the route path will take precedence
        else if (setArrowLocation != null && node.distanceTo(currentLocation) < PATH_GAP_COMPARISON + 2.0f) {
            // put marker at node
            placeUserLocationMarker(new LatLng(node.getLatitude(), node.getLongitude()));
        } else {
            // take away marker, replace user's location with blue dot, set userLocationArrow to null
            // for logic reasons later
            if (ourMap != null) {
                // bring back user's current location blue dot
                ourMap.setMyLocationEnabled(true);
                if (userLocationArrow != null) {
                    // removing does not set null; all methods called on a removed marker are not defined
                    userLocationArrow.remove();
                    userLocationArrow = null;
                }
            }
        }
    }

    /**
     * Method to handle the actual placing of the user's location marker with the given settings
     */
    private void placeUserLocationMarker(LatLng locationToPlace) {
        if (ourMap != null) {
            // take away the user's blue dot of a location
            ourMap.setMyLocationEnabled(false);
            if (userLocationArrow == null) {
                // if null need a new marker
                userLocationArrow = ourMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_location_arrow))
                        .position(locationToPlace)
                        .flat(true)
                        .anchor(0.5f, 0.5f)
                        .rotation(currBearing));
            } else {
                // if not null then just need to update an existing marker
                userLocationArrow.setRotation(currBearing);
                userLocationArrow.setPosition(locationToPlace);
            }
        }
    }

    /**
     /* Method for animating the direction arrow based upon the incoming arrow and the given state of the arrow
     */
    private void animateArrow(float arrowRotation) {
        // change duration based on changing size or just rotating
        // sizes set in toggleArrowSize()
        int duration;
        if (currentScaleSize != newScaleSize) {
            duration = 1000;
        } else {
            duration = 1;
        }

        // first param is offset of rotation; last 4 parameters are to set to rotate about middle of arrow
        RotateAnimation rotateAnimation = new RotateAnimation(lastRotation, arrowRotation, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        // set to make rotation not start from 0 for next rotation, but where it stopped
        lastRotation = arrowRotation;
        rotateAnimation.setDuration(duration);
        rotateAnimation.setFillAfter(true);

        // often this is just scaled to itself, but when different it enlarges or shrinks based on percentage of
        // arrow image; last four params tell it to enlarge from upper left corner
        // new scale size is changed if image is tapped
        ScaleAnimation scaleAnimation = new ScaleAnimation(currentScaleSize, newScaleSize, currentScaleSize, newScaleSize, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 1.0f);

        // booleans to see if arrow was enlarged with this animation
        // Often they will be equal - when no size change is needed
        boolean enlarged = newScaleSize > currentScaleSize;
        boolean shrunk = newScaleSize < currentScaleSize;

        // set equal so no scale change is done unless image is touched
        currentScaleSize = newScaleSize;
        scaleAnimation.setDuration(duration);
        scaleAnimation.setFillAfter(true);

        animationSet = new AnimationSet(true);
        animationSet.addAnimation(rotateAnimation);
        animationSet.addAnimation(scaleAnimation);
        animationSet.setFillAfter(true);
        arrowImage.startAnimation(animationSet);

        // One line of repeated code, but most of the time we won't need to do
        // either operation so I thought it would be better to keep them separate
        if (enlarged) {
            ImageView xIcon = (ImageView) findViewById(R.id.x_icon);
            xIcon.setVisibility(View.VISIBLE);
        } else if (shrunk) {
            ImageView xIcon = (ImageView) findViewById(R.id.x_icon);
            xIcon.setVisibility(View.INVISIBLE);
        }
    }

    // Code used to repopulate necessary fields if the Activity is interrupted
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of currentLocation from the Bundle
            if (savedInstanceState.keySet().contains(CURRENT_LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that
                // mCurrentLocationis not null.
                currentLocation = savedInstanceState.getParcelable(CURRENT_LOCATION_KEY);
            }

            // Update the value of nextDestination from the Bundle
            if (savedInstanceState.keySet().contains(NEXT_DESTINATION_KEY)) {
                nextDestination = savedInstanceState.getParcelable(NEXT_DESTINATION_KEY);
            }
            bearingToDestDegrees = currentLocation.bearingTo(createAndroidLocation(nextDestination));
            updateUI();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Saves all of the pertinent information if Activity is interrupted
        savedInstanceState.putParcelable(CURRENT_LOCATION_KEY, currentLocation);
        savedInstanceState.putParcelable(NEXT_DESTINATION_KEY, nextDestination);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stops location updates when the app is in the background
        stopLocationUpdates();

        if (!runningOnEmulator) {
            sensorManager.unregisterListener(this);
        }
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        // I am unclear as to why we use 'not' requestingLocationUpdates, but I am following Google's
        // tutorial here
        if (googleApiClient.isConnected() && !requestingLocationUpdates) {
            startLocationUpdates();
        }

        if (!runningOnEmulator) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
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
        googleApiClient.connect();
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

    public void toggleArrowSize(View arrowImage) {
        // get the percentage of arrow image needed to fill screen
        LinearLayout overallLayout = (LinearLayout) findViewById(R.id.overall_linear_layout);
        // Subtract 20 in order to roughly have 5dp on each side of enlarged arrow
        int widthLayout = overallLayout.getWidth() - 20;
        int widthArrowImage = arrowImage.getWidth();
        float percentArrowToFitLayout = (widthLayout * 1.0f) / (widthArrowImage * 1.0f);

        // Check if should shrink or enlarge arrow
        if (currentScaleSize == 1.0f) {
            // If true then arrow is small so need to enlarge
            newScaleSize = percentArrowToFitLayout;
        } else {
            // Arrow should be shrunk
            newScaleSize = 1.0f;
        }
    }

    /**
     * Method to see if a location is within the 'oval' geo-fence range created by two
     * nodes and a limiting distance away from the plotted route between those two nodes
     */
    private boolean isWithinOval(Location locationToCheck, Location nodeA, Location nodeB, float allowedDistFromRoute, float allowedDistPastNode) {
        // Gets angle between route between nodes and the location to check
        float routeBearing = nodeA.bearingTo(nodeB);
        float bearingToLocToCheck = nodeA.bearingTo(locationToCheck);
        double angleBetween = (double) (bearingToLocToCheck - routeBearing);

        // in meters; Uses the sine of the created triangle to get the perpendicular distance from the route created
        // between nodeA and nodeB
        double distanceFromRoute = Math.abs(Math.sin(Math.toRadians(angleBetween)) * nodeA.distanceTo(locationToCheck));

        float distanceBetweenNodes = nodeA.distanceTo(nodeB);
        float locToCheckDistFromNodeA = nodeA.distanceTo(locationToCheck);
        float locToCheckDistFromNodeB = nodeB.distanceTo(locationToCheck);

        // checks if perpendicular distance from route is less than the allowed
        // AND if the location is within a circle created around nodeA with the added offset, "distance past node"
        // AND if the location is within a circle created around nodeB with the added offset, "distance past node"
        return (distanceFromRoute < allowedDistFromRoute
                && locToCheckDistFromNodeA < distanceBetweenNodes + allowedDistPastNode
                && locToCheckDistFromNodeB < distanceBetweenNodes + allowedDistPastNode);
    }

    /**
     * Method to get a new location based off of a bearing and distance from a
     * known location; uses idea of similar triangles to convert distance in meters to
     * Lat and Long
     */
    private LatLng getLocBetweenNodes(Location node, Location nextNode, double distanceOfNew) {
        // Legs of big triangle in Lat Long coordinates
        double diffLat = nextNode.getLatitude() - node.getLatitude();
        double diffLong = nextNode.getLongitude() - node.getLongitude();

        // Makes proportion of similar triangles (small and big) based on meter distances of hypotenuses
        double distBetweenNodes = node.distanceTo(nextNode);
        double smallTriOverBigTri = distanceOfNew / distBetweenNodes;

        // Uses the proportion to get fraction of legs measured in Lat or Long differences
        // Add to original node to get new location
        double newLat = (smallTriOverBigTri * diffLat) + node.getLatitude();
        double newLong = (smallTriOverBigTri * diffLong) + node.getLongitude();

        return new LatLng(newLat, newLong);
    }

    /**
     * Method to check the user's current distance from the current route segment
     * if more than FARTHEST_ALLOWED_FROM_PATH then recalculate route based on user's current location
     */
    private void checkMaxDistForRecalculation() {
        double userDistanceFromSegment = getUserDistFromCurrentSegment();

        if (userDistanceFromSegment > FARTHEST_ALLOWED_FROM_PATH) {
            // recalculation is needed
            // calculate a new route based upon the user's current location;
            // in essence this is done by clearing the map and making a new request

            // Start "Calculating Message... " while waiting for the onLocationChanged to get new route
            showProgressionMessage(RECALCULATING_ROUTE_MESSAGE);

            clearMapAndRoute();

            // these variables are used to tell onLocationChanged to make a new request
            // with the user's current location
            routeNeeded = true;
            routeGenUsesCurrLoc = true;
        }
    }

    /**
     * Method to get the shortest distance of the user to the current line segment
     */
    private double getUserDistFromCurrentSegment() {
        // nodeA pairs with start of segment
        Location nodeA = null;
        // nodeB pairs with end of segment/nextDestination
        Location nodeB = null;
        // user must have reached first node in order for a segment to exist
        if (hasReachedRouteStart()) {
            // if user has reached route start then none of these values will be out of bounds
            int nodeBIndex = ourRoute.indexOf(nextDestination);
            int nodeAIndex = nodeBIndex - 1;
            nodeA = createAndroidLocation(ourRoute.get(nodeAIndex));
            nodeB = createAndroidLocation(ourRoute.get(nodeBIndex));

            float distanceAtoB = nodeA.distanceTo(nodeB);
            float distanceAtoUser = nodeA.distanceTo(currentLocation);
            float distanceBtoUser = nodeB.distanceTo(currentLocation);

            float angleAtoB = nodeA.bearingTo(nodeB);
            float angleBtoA = nodeB.bearingTo(nodeA);
            float angleAtoUser = nodeA.bearingTo(currentLocation);
            float angleBtoUser = nodeB.bearingTo(currentLocation);

            // These are the angles to be used for the projections of the user upon the segment's lines;
            // They are used with the triangles created by the node, the user, and the point perpendicular from the
            // segment's line to the user
            double angleA = Math.toRadians((double) (angleAtoUser - angleAtoB));
            double angleB = Math.toRadians((double) (angleBtoUser - angleBtoA));

            // These are the distances from each node to the projected point of the user along the segment's line
            double projectionLengthFromA = Math.abs(Math.cos(angleA) * (double) distanceAtoUser);
            double projectionLengthFromB = Math.abs(Math.cos(angleB) * (double) distanceBtoUser);

            // Three cases exist:
            // 1) the user's projection onto the segment is on the line
            // 2) the user's closest point to the segment is nodeA
            // 3) the user's closest point to the segment is nodeB

            double userDistanceFromSegment = 0.0;
            // Check for case 1
            if (projectionLengthFromA <= distanceAtoB && projectionLengthFromB <= distanceAtoB) {
                // the user's projected location is on the line segment so need the perpendicular distance
                // from the line segment
                userDistanceFromSegment = Math.abs(Math.sin(angleA) * (double) distanceAtoUser);
            }
            // Check for case 2
            else if (distanceAtoUser < distanceBtoUser) {
                userDistanceFromSegment = (double) distanceAtoUser;
            }
            // Case 3
            else {
                userDistanceFromSegment = (double) distanceBtoUser;
            }

            return userDistanceFromSegment;
        }
        // if user has not gotten to the start of the route then simply return a default 0 response
        return 0.0;
    }

    /**
     * Method to clear our current map and route
     */
    private void clearMapAndRoute() {
        // remove all path segments from map
        for (int i = 0; i < polylineList.size(); i++) {
            polylineList.get(i).remove();
        }

        // remove nextDestinationMarker if it exists
        if (nextDestMarker != null) {
            nextDestMarker.remove();
            // set to null to fully remove for logic reasons - remove() does not remove fully and make null
            nextDestMarker = null;
        }

        // remove user's location arrow if it exists
        if (userLocationArrow != null) {
            userLocationArrow.remove();
            // set to null to fully remove for logic reasons - remove() does not remove fully and make null
            userLocationArrow = null;
        }

        // Make it so the user's location is shown as a blue dot
        if (ourMap != null) {
            ourMap.setMyLocationEnabled(true);
        }

        // clear ourRoute
        ourRoute = new ArrayList<LatLng>();
    }

    /**
     * Method to filter incoming route to connect short segments together and
     * populate ourRoute
     */
    private void filterAndPopulateRoute(List<Coordinate> inputRoute) {
        // loop looks at next two points so do not loop to last or second to last in list
        // loop looks at groups of three nodes to compare
        // Note: counting variable, i, can be changed in loop to skip already checked/added nodes
        for (int i = 0; i < inputRoute.size() - 2; i++) {
            // add first node as start of segment
            ourRoute.add(new LatLng(inputRoute.get(i).getLatitude(),
                    inputRoute.get(i).getLongitude()));

            // logic to see which segment should be added next as last segment in straight line

            Location startNode = createAndroidLocation(new LatLng(inputRoute.get(i).getLatitude(),
                    inputRoute.get(i).getLongitude()));


            int offsetFromStart = 1;
            int nextNodeIndex = i + offsetFromStart;

            Location nextNode = createAndroidLocation(new LatLng(inputRoute.get(nextNodeIndex).getLatitude(),
                    inputRoute.get(nextNodeIndex).getLongitude()));

            float bearingOfFirstSegment = startNode.bearingTo(nextNode);


            offsetFromStart++;
            int lastNodeIndex = i + offsetFromStart;

            Location lastNode = createAndroidLocation(new LatLng(inputRoute.get(lastNodeIndex).getLatitude(),
                    inputRoute.get(lastNodeIndex).getLongitude()));

            float bearingOfLastSegment = nextNode.bearingTo(lastNode);


            bearingOfFirstSegment = makeBearingZeroTo360(bearingOfFirstSegment);
            bearingOfLastSegment = makeBearingZeroTo360(bearingOfLastSegment);
            float angleDifference = getAcuteAngleDifference(bearingOfFirstSegment, bearingOfLastSegment);

            // compared to max allowed degree difference and checks lastNodeIndex is not at end of route
            while (angleDifference < STRAIGHT_ALLOWED_ANGLE_DIFF && lastNodeIndex < inputRoute.size() - 1) {
                nextNode = lastNode;

                offsetFromStart++;
                lastNodeIndex = i + offsetFromStart;
                lastNode = createAndroidLocation(new LatLng(inputRoute.get(lastNodeIndex).getLatitude(),
                        inputRoute.get(lastNodeIndex).getLongitude()));
                bearingOfLastSegment = nextNode.bearingTo(lastNode);
                bearingOfLastSegment = makeBearingZeroTo360(bearingOfLastSegment);
                angleDifference = getAcuteAngleDifference(bearingOfFirstSegment, bearingOfLastSegment);
            }

            // segment from nextNode to lastNode either failed or it got to the end of the route
            // case for reaching end of route with lastNodeIndex and last segment in line
            if (lastNodeIndex == inputRoute.size() - 1 && angleDifference < STRAIGHT_ALLOWED_ANGLE_DIFF) {
                // last node should be last node added to list
                ourRoute.add(new LatLng(inputRoute.get(lastNodeIndex).getLatitude(),
                        inputRoute.get(lastNodeIndex).getLongitude()));
                // reached end of route so no more nodes to be checked; exit loop
                break;
            }
            // case for reaching end of route with lastNodeIndex but last segment not in line
            else if (lastNodeIndex == inputRoute.size() - 1 && angleDifference >= STRAIGHT_ALLOWED_ANGLE_DIFF) {
                // should add nextNode and lastNode to list
                int nextNodeIndexToAdd = lastNodeIndex - 1;
                ourRoute.add(new LatLng(inputRoute.get(nextNodeIndexToAdd).getLatitude(),
                        inputRoute.get(nextNodeIndexToAdd).getLongitude()));
                ourRoute.add(new LatLng(inputRoute.get(lastNodeIndex).getLatitude(),
                        inputRoute.get(lastNodeIndex).getLongitude()));
                // reached end of route so no more nodes to be checked; exit loop
                break;
            }
            // case where did not reach end, but rather segment from nextNode to lastNode failed to be in line
            else {
                // need to add the start of that segment as last node that was in line
                // this addition comes at the beginning of the next loop
                int nextNodeIndexToAdd = lastNodeIndex - 1;
                // Subtract 1 because loop will increment i
                i = nextNodeIndexToAdd - 1;
            }
        }
    }

    /**
     * Method to get acute angle difference between two bearings
     */
    private float getAcuteAngleDifference(float bearingA, float bearingB) {
        float difference = Math.abs(bearingA - bearingB);

        if (difference > 180.0f) {
            difference = 360.0f - difference;
        }

        return difference;
    }

    /**
     * Method to make a bearing that goes from -180 to 180 go from 0 to 360
     */
    private float makeBearingZeroTo360(float bearing) {
        // if negative then convert
        if (bearing < 0) {
            return 360.0f + bearing;
        }
        // if positive then change is not needed
        return bearing;
    }

    /**
     * Method to check if nextDestination is set as the first node of the route
     */
    private boolean hasReachedRouteStart() {
        int nextNodeIndex = ourRoute.indexOf(nextDestination);
        // decides if nextNodeIndex is for the first node in the route; decides if user has reached first node
        return nextNodeIndex > 0;
    }

    // Simple helper method for converting from LatLng to an android.location.Location
    private android.location.Location createAndroidLocation(LatLng point) {
        // Provider name is unnecessary
        android.location.Location newLocation = new android.location.Location("");
        newLocation.setLatitude(point.latitude);
        newLocation.setLongitude(point.longitude);
        return newLocation;
    }

    private String getStartEndRouteURL() {
        return URL + "generateRoute?from=" + startLocation + "&to=" + endLocation;
    }

    private String getCurrLocRouteURL() {
        return URL + "generateRouteCurrent?dest=" + endLocation + "&currlat=" +
                Double.toString(currentLocation.getLatitude()) + "&currlong=" +
                Double.toString(currentLocation.getLongitude());
    }

    private String getTourRouteURL() {
        return URL + "tourRoute?id=" + tourId;
    }
}