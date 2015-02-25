package com.osucse.wayfinding_osu_capstone;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;
import android.os.AsyncTask;
import android.util.*;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import com.osucse.wayfinding_api.*;
import java.util.*;


import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class DisplayMapActivity extends FragmentActivity {
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

    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_map);

        Intent intent = getIntent();
        String startLocation = intent.getStringExtra(MainActivity.START_LOCATION);
        //TextView textView = new TextView(this);
        TextView startLocationDisplay = (TextView) findViewById(R.id.start_location_display);
        startLocationDisplay.setTextSize(20);
        startLocationDisplay.setText(startLocation);
        //setContentView(textView);
        new HttpRequestTask().execute();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
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
        // check if we have got the googleMap already
        if (googleMap == null) {
            googleMap = ((SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map)).getMap();
            if (googleMap != null) {
                addLines(path);
                googleMap.setMyLocationEnabled(true);
            }
        }
    }

    private void plotSegments(List<LatLng> segList){
//        for (Map.Entry<LatLng, LatLng> entry : segMap.entrySet() ){
//            LatLng start = entry.getKey();
//            LatLng end = entry.getValue();
//            googleMap
//                    .addPolyline((new PolylineOptions())
//                            .add(start, end).width(5).color(Color.BLUE)
//                            .geodesic(true));
//        }
          int left = 0;
          int right = 1;
          int size = segList.size();
          LatLng start = null;
          LatLng end = null;
          for (int i =right ; i < segList.size(); i+=2){
              start = segList.get(left);
              end = segList.get(right);
              left+=2;
              right+=2;
              googleMap
                      .addPolyline((new PolylineOptions())
                              .add(start, end).width(5).color(Color.BLUE)
                              .geodesic(true));
              System.out.println(right);
          }

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(NEXT3,
                17));
    }


    /**
     * Adds the path to the maps fragment.
     *
     * @param path - list of coordinate points that make up the lines in a path
     */
    private void addLines(List<LatLng> path) {
        int pathSize = path.size();
        int current = 0;
        LatLng start = null;
        LatLng end = null;
        for (int i = 1; i < pathSize; i++) {
            if (start == null) {
                start = path.get(current);
            } else {
                start = end;
            }
            current++;
            end = path.get(current);
            googleMap
                    .addPolyline((new PolylineOptions())
                            .add(start, end).width(5).color(Color.BLUE)
                            .geodesic(true));
            // move camera to zoom on map
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(start,
                    17));
        }
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, SegmentCollection> {
        @Override
        protected SegmentCollection doInBackground(Void... params) {
            try {
                final String url = "http://cseosuwintest.cloudapp.net:9000/segments";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                SegmentCollection collection = restTemplate.getForObject(url, SegmentCollection.class);
                return collection;
            } catch (Exception e) {
                Log.e("SegmentCollection", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(SegmentCollection collection) {
            final List<LatLng> segmentPoints = new ArrayList<LatLng>();
            final Map<LatLng, LatLng> segMap = new HashMap<>();
            for(Segment s : collection.getSegments() )
            {
                Location node1 = s.getToNode();
                Location node2 = s.getFromNode();
                if((node1 != null) && (node2 != null)) {
                    double lat1 = node1.getLatitude();
                    double lng1 = node1.getLongitude();
                    LatLng point1 = new LatLng(lng1, lat1);
                    double lat2 = node2.getLatitude();
                    double lng2 = node2.getLongitude();
                    LatLng point2 = new LatLng(lng2, lat2);
                    segmentPoints.add(point1);
                    segmentPoints.add(point2);
                    //segMap.put(point1, point2);

                }
            }
            //addLines(segmentPoints);
            //System.out.println(segMap.size());
            plotSegments(segmentPoints);


            //  TextView tv = (TextView) findViewById(R.id.textView8);
            // tv.setText("Select One:");

            /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(LocationList.this, R.layout.abc_list_menu_item_layout, locationNames);
            locationListView.setAdapter(adapter);*/

            //adapter.clear();
            //adapter.addAll(locationNames);

        }

    }

}
