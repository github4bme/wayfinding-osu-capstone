package com.osucse.wayfinding_osu_capstone;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationServices;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;






import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import java.util.*;


public class DisplayMapActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {
    private static final LatLng LOWER_MANHATTAN = new LatLng(40.722543,
            -73.998585);
    private static final LatLng TIMES_SQUARE = new LatLng(40.7577, -73.9857);
    private static final LatLng BROOKLYN_BRIDGE = new LatLng(40.7057, -73.9964);
    //private static final LatLng STILLMAN_HALL = new LatLng(	40.0018248, -083.0110277);
    //private static final LatLng DEST = new LatLng(40.0018227, -083.0110479);
    //private static final LatLng NEXT = new LatLng(40.0018760, -083.0110638);
    //private static final LatLng NEXT = new LatLng(40.0018100, -083.0110455);
    //private static final LatLng NEXT1 = new LatLng(40.0017233, -083.0109211);

    //Curved line
    //    private static final LatLng DEST = new LatLng(39.9960940, -083.0140383);
    //    private static final LatLng NEXT = new LatLng(39.9960905, -083.0140724);
    //    private static final LatLng NEXT1 = new LatLng(39.9960909, -083.0141052);
    //    private static final LatLng NEXT2 = new LatLng(39.9960955, -083.0141336);

    private static final LatLng DEST = new LatLng(39.9986444, -083.0150867);
    private static final LatLng NEXT = new LatLng(39.9985652, -083.0151295);
    private static final LatLng NEXT1 = new LatLng(39.9984717, -083.0151624);
    private static final LatLng NEXT2 = new LatLng(39.9983703, -083.0151790);
    private static final LatLng NEXT3 = new LatLng(39.9983301, -083.0151664);

//    private GoogleMap googleMap;

    protected GoogleMap ourMap;
    protected GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_map);

        Intent intent = getIntent();
        String startLocation = intent.getStringExtra(SelectDestinationLocation.SOURCE_LOCATION);
        String endLocation = intent.getStringExtra(SelectDestinationLocation.DESTINATION_LOCATION);

        //TextView textView = new TextView(this);
        TextView startLocationDisplay = (TextView) findViewById(R.id.start_location_display);
        startLocationDisplay.setTextSize(20);
        startLocationDisplay.setText("starting id: " + startLocation + " ending id:" + endLocation);
        //setContentView(textView);




        buildGoogleApiClient();

        MapFragment map = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(this);
    }

    protected synchronized void buildGoogleApiClient() {
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
        android.location.Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            // currentLocationMarker = ourMap.addMarker(new MarkerOptions().title("I'm Here" + iteration).position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())));
            Marker currenLocationMarker = ourMap.addMarker(new MarkerOptions().title("I'm Here").position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())));
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

//    private void setUpMapIfNeeded() {
    @Override
    public void onMapReady(GoogleMap googleMap) {
        ourMap = googleMap;

        List<LatLng> path = new ArrayList<LatLng>();
        /*path.add(LOWER_MANHATTAN);
        path.add(TIMES_SQUARE);
        path.add(BROOKLYN_BRIDGE);
        path.add(LOWER_MANHATTAN);*/
        path.add(DEST);
        path.add(NEXT);
        path.add(NEXT1);
        path.add(NEXT2);
        path.add(NEXT3);
//        // check if we have got the googleMap already
//        if (googleMap == null) {
//            googleMap = ((SupportMapFragment) getSupportFragmentManager()
//                    .findFragmentById(R.id.map)).getMap();
//            if (googleMap != null) {
//                addLines(path);
//                googleMap.setMyLocationEnabled(true);
//            }
//        }

        addLines(path);
        ourMap.setMyLocationEnabled(true);
        ourMap.addMarker(new MarkerOptions().title("Destination").position(DEST));
    }

    private void addLines(List<LatLng> path) {
        int pathSize = path.size();
        int current =0;
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
