package com.osucse.wayfinding_osu_capstone;

import android.util.Log;

import com.osucse.wayfinding_api.Building;
import com.osucse.wayfinding_api.BuildingCollection;
import com.osucse.wayfinding_api.Tour;
import com.osucse.wayfinding_api.TourCollection;

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

    private static ArrayList<Building> BUILDING_LIST = null;
    private static ArrayList<Tour> TOUR_LIST = null;

    /**
     *
     * @return
     */
    public static BuildingCollection getBuildingsFromServer () {
        BuildingCollection buildingCollection = null;
        try {
            String url = URL + "buildings";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            buildingCollection = restTemplate.getForObject(url, BuildingCollection.class);
            BUILDING_LIST = makeBuildingList(buildingCollection);
        } catch (Exception e) {
            Log.e("LocationList", e.getMessage(), e);
        }
        return buildingCollection;
    }

    /**
     *
     * @param bc
     * @return
     */
    private static ArrayList<Building> makeBuildingList (BuildingCollection bc) {
        ArrayList<Building> bl = new ArrayList<Building>();

        for(Building b : bc.getBuildings())
        {
            bl.add(b);
        }

        Collections.sort(bl);

        return bl;
    }

    /**
     *
     * @return
     */
    public static ArrayList<Building> cloneBuildingList () {
        return new ArrayList<Building>(BUILDING_LIST);
    }

    /**
     *
     * @return
     */
    public static TourCollection getToursFromServer () {
        TourCollection tourCollection = null;
        try {
            String url = URL + "tours";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            tourCollection = restTemplate.getForObject(url, TourCollection.class);
            TOUR_LIST = makeTourList(tourCollection);
        } catch (Exception e) {
            Log.e("TourList", e.getMessage(), e);
        }
        return tourCollection;
    }

    /**
     *
     * @param tc
     * @return
     */
    private static ArrayList<Tour> makeTourList (TourCollection tc) {
        ArrayList<Tour> tl = new ArrayList<Tour>();

        for(Tour t : tc.getTours())
        {
            tl.add(t);
        }

        Collections.sort(tl);

        return tl;
    }

    /**
     *
     * @return
     */
    public static ArrayList<Tour> cloneTourList () {
        return new ArrayList<Tour>(TOUR_LIST);
    }
}


