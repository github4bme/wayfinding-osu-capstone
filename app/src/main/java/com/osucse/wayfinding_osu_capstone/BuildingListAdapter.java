package com.osucse.wayfinding_osu_capstone;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.osucse.wayfinding_api.Building;
import com.osucse.wayfinding_api.BuildingCollection;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by thomasforte on 4/14/15.
 */
public class BuildingListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<Building> buildings;
    private ArrayList<Boolean> favorite;

    private static SharedPreferences sharedPreferences;
    private static final String SHARED_FAVORITES = "SHARED_FAVORITES";

    private static SharedPreferences.Editor editor;

    public BuildingListAdapter (Activity activity, SharedPreferences sharedPreferences) {
        this.activity = activity;
        this.buildings = cloneBuildingList();
        this.sharedPreferences = sharedPreferences;

        this.favorite = new ArrayList<Boolean>();
        for (Building b : this.buildings) {
            this.favorite.add(false);
        }

        Set<String> favoritesFromMemory = new HashSet<String>();//= sharedPreferences.getStringSet(SHARED_FAVORITES, new HashSet<String>());
        favoritesFromMemory.add("Converse Hall");
        favoritesFromMemory.add("Riffe Building");

        //O(n * m) where m is the amount of favorites
        for (String s : favoritesFromMemory){

            // really sloppy but works for now
            for (Building b : buildings) {
                if (b.getName().equals(s)){
                    favorite.set(buildings.indexOf(b), true);
                }
            }
        }

        // sort list O(n)
        sortbuildings(this.buildings, this.favorite);
    }

    private static void sortbuildings (ArrayList<Building> buildings, ArrayList<Boolean> favorite) {
        ArrayList<Building> favoritedBuildings = new ArrayList<>();
        ArrayList<Boolean> favoritedFavorites = new ArrayList<>();

        int i = favorite.size()-1;
        while (i > 0) {
            if (favorite.get(i)) {
                favoritedBuildings.add(0,buildings.remove(i));
                favoritedFavorites.add(0,favorite.remove(i));
            }
            i--;
        }

        buildings.addAll(0,favoritedBuildings);
        favorite.addAll(0,favoritedFavorites);
    }

    @Override
    public int getCount() {
        return buildings.size();
    }

    @Override
    public Object getItem(int position) {
        return buildings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null) {
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listitem, null);
        }

        TextView buildingName = (TextView) convertView.findViewById(R.id.buildingName);
        CheckBox favoriteBox = (CheckBox) convertView.findViewById(R.id.favoriteCheckBox);

        Building b = (Building) getItem(position);

        buildingName.setText(b.getName());
        favoriteBox.setChecked(favorite.get(position));

        return convertView;
    }

    /* ========================================================
       Get data from server
       ======================================================== */

    private static final String URL = "http://54.200.238.22:9000/";
    private static ArrayList<Building> BUILDING_LIST = null;

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
}
