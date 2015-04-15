package com.osucse.wayfinding_osu_capstone;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.osucse.wayfinding_api.Building;
import com.osucse.wayfinding_api.BuildingCollection;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by thomasforte on 4/14/15.
 */
public class BuildingListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<Building> buildings;
    private ArrayList<Boolean> favorite;

    public BuildingListAdapter (Activity activity, List<Building> buildings) {
        this.activity = activity;
        this.buildings = buildings;

        this.favorite = new ArrayList<Boolean>();
        for (Building b : this.buildings) {
            this.favorite.add(false);
        }
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