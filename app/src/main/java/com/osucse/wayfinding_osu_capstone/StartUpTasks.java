package com.osucse.wayfinding_osu_capstone;

import android.content.Context;
import android.util.Log;

import com.osucse.utilities.LocationTuple;
import com.osucse.wayfinding_api.Location;
import com.osucse.wayfinding_api.LocationCollection;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

/**
 * Created by thomas forte on 3/3/2015.
 */
public class StartUpTasks {

    // server url
    private static final String URL = "http://54.200.238.22:9000/";

    private static ArrayList<LocationTuple> LOCATION_LIST;

    private static final String LOCATION_LIST_FILE = "LOCATION_LIST_FILE";


    public static LocationCollection checkForLocationList (Context context) {
        if (doesLocationFileExist()) {
            loadLocationList(context);
        } else {
            getLocationCollectionFromServer(context);
        }
        return null;
    }

    private static void  getLocationCollectionFromServer (Context context) {
        LocationCollection locationCollection = null;
        try {
            String url = URL + "locations";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            locationCollection = restTemplate.getForObject(url, LocationCollection.class);

        } catch (Exception e) {
            Log.e("LocationList", e.getMessage(), e);
        }

        LOCATION_LIST = createLocationList(locationCollection);
        saveLocationList(context);
    }

    private static ArrayList<LocationTuple> createLocationList (LocationCollection locationCollection) {

        // initialize a new array
        ArrayList<LocationTuple> temp = new ArrayList<LocationTuple>();

        // removes null location data while copying pointers
        for(Location l : locationCollection.getLocations())
        {
            if(l.getName() != null) {
                temp.add(new LocationTuple(l.getName(), Integer.toString(l.getId())));
            }
        }

        Collections.sort(temp);

        return temp;
    }

    private static void saveLocationList (Context context) {
        FileOutputStream outputStream;

        try {
            outputStream = context.openFileOutput(LOCATION_LIST_FILE, Context.MODE_PRIVATE);
            for (LocationTuple l : LOCATION_LIST) {
                outputStream.write((l.getName() + "\n").getBytes());
                outputStream.write((l.getId() + "\n").getBytes());
            }
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadLocationList (Context context) {
        LOCATION_LIST = new ArrayList<LocationTuple>();
        try {
            FileInputStream inputStream = context.openFileInput(LOCATION_LIST_FILE);
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = r.readLine()) != null) {
                LOCATION_LIST.add(new LocationTuple(line, r.readLine()));
            }
            r.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean doesLocationFileExist () {
        File file = new File(LOCATION_LIST_FILE);
        return file.exists();
    }

    public static ArrayList<LocationTuple> getLocationList () {
        return new ArrayList<LocationTuple>(LOCATION_LIST);
    }
}


