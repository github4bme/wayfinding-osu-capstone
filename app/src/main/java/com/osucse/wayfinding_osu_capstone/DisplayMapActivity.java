package com.osucse.wayfinding_osu_capstone;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;






import android.util.Log;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;


// Josh Added
import com.google.android.gms.maps.OnMapReadyCallback;









import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import java.util.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import com.osucse.wayfinding_api.*;
//import com.osucse.utilities.Coordinate;
//import com.google.android.gms.maps.SupportMapFragment;


public class DisplayMapActivity extends FragmentActivity {

    private static final String URL = "http://54.200.238.22:9000/";

//    private static final LatLng DEST = new LatLng(39.9986444, -083.0150867);
//    private static final LatLng NEXT = new LatLng(39.9985652, -083.0151295);
//    private static final LatLng NEXT1 = new LatLng(39.9984717, -083.0151624);
//    private static final LatLng NEXT2 = new LatLng(39.9983703, -083.0151790);
//    private static final LatLng NEXT3 = new LatLng(39.9983301, -083.0151664);


    /*****
     * TO BE CHOPPED
     */
    private static String startLocation = "";
    private static String endLocation = "";






    private GoogleMap ourMap;
    List<LatLng> ourRoute = new ArrayList<LatLng>();















    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_map);
        Intent intent = getIntent();
        startLocation = intent.getStringExtra(SelectDestinationLocation.SOURCE_LOCATION);
        endLocation = intent.getStringExtra(SelectDestinationLocation.DESTINATION_LOCATION);

        TextView startLocationDisplay = (TextView) findViewById(R.id.start_location_display);
        startLocationDisplay.setTextSize(20);
        startLocationDisplay.setText("This is all me -- starting id: " + startLocation + " ending id:" + endLocation);
        //setContentView(textView);
        new HttpRequestTask().execute();
    }



    private class HttpRequestTask extends AsyncTask<Void, Void, Route> implements OnMapReadyCallback {
        @Override
        protected Route doInBackground(Void... params) {
            try {

                final String url = URL + "generateRoute?from=" + startLocation + "&to=" + endLocation;
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

                MapFragment map = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

                // Sets up a non-null GoogleMap and calls onMapReady()
                map.getMapAsync(this);
            }
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            // This is called when our getMapAsync() in onCreate() successfully gets a map
            // Set created googleMap to our global map
            ourMap = googleMap;

            plotRoute();
            ourMap.setMyLocationEnabled(true);
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
}

