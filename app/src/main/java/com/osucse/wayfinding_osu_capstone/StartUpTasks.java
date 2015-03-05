package com.osucse.wayfinding_osu_capstone;

import android.util.Log;

import com.osucse.wayfinding_api.Location;
import com.osucse.wayfinding_api.LocationCollection;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by thomas forte on 3/3/2015.
 */
public class StartUpTasks {

    // server url
    private static final String URL = "http://54.200.238.22:9000/";

    // location data
    private static LocationCollection LOCATION_COLLECTION = null;

    /**
     * getLocationCollectionFromServer will attempt to get a connection
     * to the api server and pull the location data into the app. It fills
     * the static variable with the LocationCollection information it collects
     * and returns a pointer to that instance.
     *
     * Data: json is 156kb and takes 365ms to pull down on wireless.osu
     *
     * @return a pointer to the LOCATION_COLLECTION object
     */
    public static LocationCollection getLocationCollectionFromServer () {
        try {
            String url = URL + "locations";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            LOCATION_COLLECTION = restTemplate.getForObject(url, LocationCollection.class);
        } catch (Exception e) {
            Log.e("LocationList", e.getMessage(), e);
        }
        return LOCATION_COLLECTION;
    }

    /**
     * cloneLocationCollection (couldn't make a deep copy because there are no constructors
     * allowed on the app side)
     * @return a shallow copy of LOCATION_COLLECTION
     */
    public static ArrayList<Location> cloneLocationCollection () {

        // initialize a new array
        ArrayList<Location> clone = new ArrayList<Location>(LOCATION_COLLECTION.getLocations().size());

        // removes null location data while copying pointers
        for(Location l : LOCATION_COLLECTION.getLocations())
        {
            if(l.getName() != null) {
                clone.add(l);
            }
        }

        // sorts the LocationCollection by name (see added compareTo part of the location class)
        Collections.sort(clone);

        return clone;
    }



}


