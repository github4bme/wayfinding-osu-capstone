package com.osucse.wayfinding_osu_capstone;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.osucse.wayfinding_api.Location;
import com.osucse.wayfinding_api.LocationCollection;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by thomas forte on 3/3/2015.
 */
public class StartUpTasks {

    // server url
    private static final String URL = "http://54.200.238.22:9000/";


    // location data
    private static LocationCollection LOCATION_COLLECTION = null;

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

    public static ArrayList<Location> cloneLocationCollection () {

        ArrayList<Location> clone = new ArrayList<Location>(LOCATION_COLLECTION.getLocations().size());

        for(Location l : LOCATION_COLLECTION.getLocations())
        {
            if(l.getName() != null) {
                clone.add(l);
            }
        }

        Collections.sort(clone);
        return clone;
    }

    public static LocationCollection getLocationCollection () {
        return LOCATION_COLLECTION;
    }

}


