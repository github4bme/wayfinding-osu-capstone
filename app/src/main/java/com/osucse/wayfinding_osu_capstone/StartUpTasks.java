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
    private static final String URL = "http://cseosuwintest.cloudapp.net:9000/";


    // location data
    private static LocationCollection LOCATION_COLLECTION = null;
    private static List<String> LOCATION_LIST;

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

    public static void createLocationList () {

        LOCATION_LIST = new ArrayList<String>();

        if (LOCATION_COLLECTION == null){return;}

        for(Location l : LOCATION_COLLECTION.getLocations())
        {
            if(l.getName() != null) {
                LOCATION_LIST.add(l.getName());
            }
        }

        Collections.sort(LOCATION_LIST);
    }


    public static LocationCollection getLocationCollection () {
        return LOCATION_COLLECTION;
    }

    public static List<String> getLocationList () {
        return LOCATION_LIST;
    }
}


