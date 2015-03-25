package com.osucse.wayfinding_osu_capstone;

import android.content.Context;
import android.util.Log;

import com.osucse.wayfinding_api.Location;
import com.osucse.wayfinding_api.LocationCollection;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

    // the list of locations from server
    private static ArrayList<Location> LOCATION_LIST;

    private static final String LOCATION_LIST_FILE = "LOCATION_LIST_FILE";




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
        LocationCollection locationCollection = null;
        try {
            String url = URL + "locations";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            locationCollection = restTemplate.getForObject(url, LocationCollection.class);

            LOCATION_LIST = makeLocationList(locationCollection);

        } catch (Exception e) {
            Log.e("LocationList", e.getMessage(), e);
        }
        return locationCollection;
    }

    /**
     * Processes the locationCollection by removing null locations, and sorting the
     * remaining locations. The result is returned
     * @param locationCollection a LocationCollection to process
     * @return a processed ArrayList of the LocationCollection data
     */
    private static ArrayList<Location> makeLocationList (LocationCollection locationCollection) {

        // initialize a new array
        ArrayList<Location> temp = new ArrayList<Location>(locationCollection.getLocations().size());

        // removes null location data while copying pointers
        for(Location l : locationCollection.getLocations())
        {
            if(l.getName() != null) {
                temp.add(l);
            }
        }

        // sorts the LocationCollection by name (see added compareTo part of the location class)
        Collections.sort(temp);

        return temp;
    }

    private static void saveLocationList (Context context) {
        try {
            FileOutputStream fos = context.openFileOutput(LOCATION_LIST_FILE, Context.MODE_PRIVATE);
            ObjectOutputStream oos= new ObjectOutputStream(fos);
            oos.writeObject(LOCATION_LIST);
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadLocationList () {
        LOCATION_LIST = new ArrayList<Location>();
        try
        {
            FileInputStream fis = new FileInputStream(LOCATION_LIST_FILE);
            ObjectInputStream ois = new ObjectInputStream(fis);
            LOCATION_LIST = (ArrayList) ois.readObject();
            ois.close();
            fis.close();
        }catch(IOException ioe){
            ioe.printStackTrace();
        }catch(ClassNotFoundException c){
            System.out.println("Class not found");
            c.printStackTrace();
        }
    }

    private static boolean doesLocationFileExist () {
        File file = new File(LOCATION_LIST_FILE);
        return file.exists();
    }

    public static ArrayList<Location> getLocationList () {
        return new ArrayList<Location>(LOCATION_LIST);
    }
}


