package com.osucse.wayfinding_osu_capstone;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
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
public class BuildingListAdapter extends BaseAdapter implements Filterable {

    private LayoutInflater inflater;
    private ArrayList<Building> buildings;

    private static SharedPreferences sharedPreferences;
    private static final String SHARED_FAVORITES = "SHARED_FAVORITES";

    private ArrayList<Building>filteredData;
    private BuildingFilter mFilter;

    /* ========================================================
       Constructor
       ======================================================== */

    public BuildingListAdapter (Activity activity) {

        this.buildings = cloneBuildingList();

        this.sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);

        this.inflater = LayoutInflater.from(activity.getApplicationContext());

        Set<String> favoritesFromMemory = new HashSet<>();//sharedPreferences.getStringSet(SHARED_FAVORITES, new HashSet<String>());
        favoritesFromMemory.add("Hitchcock Hall");
        favoritesFromMemory.add("Arps Hall");
        favoritesFromMemory.add("Watts Hall");


        //O(n * m) where m is the amount of favorites
        for (String s : favoritesFromMemory){

            // really sloppy but works for now
            for (Building b : buildings) {
                if (b.getName().equals(s)){
                    b.favorited = true;
                }
            }
        }

        // sort list O(n)
        sortbuildings(this.buildings);

        this.filteredData = new ArrayList<Building>(this.buildings);
    }

    /* ========================================================
       inheirted methods
       ======================================================== */

    @Override
    public int getCount() {
        return filteredData.size();
    }

    @Override
    public Building getItem(int position) {
        return filteredData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listitem, null);

            holder = new ViewHolder();
            holder.buildingName = (TextView) convertView.findViewById(R.id.buildingName);
            holder.favoriteStar = (ImageView) convertView.findViewById(R.id.favorited);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Building b = (Building) getItem(position);

        holder.buildingName.setText(b.getName());
        if (!b.favorited){
            holder.favoriteStar.setVisibility(View.GONE);
        }


        return convertView;
    }

    static class ViewHolder {
        TextView buildingName;
        ImageView favoriteStar;
    }

    private void changeBuildingState (String name){

    }

    /* ========================================================
       Filtering methods
       ======================================================== */

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new BuildingFilter();
        }
        return this.mFilter;
    }

    private class BuildingFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<Building> list = buildings;

            int count = list.size();
            final ArrayList<Building> nlist = new ArrayList<Building>(count);

            Building filterableBuilding;

            for (int i = 0; i < count; i++) {
                filterableBuilding = list.get(i);
                if (filterableBuilding.getName().toLowerCase().contains(filterString)) {
                    nlist.add(filterableBuilding);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<Building>) results.values;
            notifyDataSetChanged();
        }

    }

    /* ========================================================
       Sorting (assumes already in alphabetical order)
       ======================================================== */

    private static void sortbuildings (ArrayList<Building> buildings) {
        ArrayList<Building> favoritedBuildings = new ArrayList<>();

        int i = buildings.size()-1;
        while (i > 0) {
            if (buildings.get(i).favorited) {
                favoritedBuildings.add(0,buildings.remove(i));
            }
            i--;
        }

        Collections.sort(buildings);

        buildings.addAll(0,favoritedBuildings);
    }

    /* ========================================================
       Getting data from server
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
        ArrayList<Building> buildings = new ArrayList<Building>();

        for(Building b : bc.getBuildings())
        {
            b.favorited = false;
            buildings.add(b);
        }

        Collections.sort(buildings);

        return buildings;
    }

    /**
     *
     * @return
     */
    public static ArrayList<Building> cloneBuildingList () {
        return new ArrayList<Building>(BUILDING_LIST);
    }

    public void saveItemToFavorites(String newItemName) {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();

        Set<String> set = new HashSet<String>();

        for (Building b : buildings) {
            if (b.favorited) {
                set.add(b.getName());
            }
        }

        set.add(newItemName);

        editor.putStringSet(SHARED_FAVORITES, set);
        editor.commit();
    }
}
