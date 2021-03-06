package com.osucse.wayfinding_osu_capstone;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Filter;

import com.osucse.wayfinding_api.Building;
import com.osucse.wayfinding_api.BuildingCollection;
import com.osucse.wayfinding_api.Tour;
import com.osucse.wayfinding_api.TourCollection;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

/**
 * Created by thomasforte on 4/14/15.
 */
public class BuildingListAdapter extends ArrayAdapter<Building> implements Filterable{

    private ArrayList<Building> originalList;   // the original list
    private ArrayList<Building> buildingList;   // the filtered list
    private BuildingFilter filter;              // filter holder

    /* ========================================================
       Main Constructor
       ======================================================== */

    public BuildingListAdapter (Context context, ArrayList<Building> buildingList) {
        super(context, R.layout.list_item_display, buildingList);
        this.originalList = new ArrayList<Building>();
        this.originalList.addAll(buildingList);
        this.buildingList = new ArrayList<Building>();
        this.buildingList.addAll(buildingList);
    }

    /* ========================================================
       ArrayAdapter overridden methods
       ======================================================== */

    @Override
    public  int getCount() { return buildingList.size(); }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = vi.inflate(R.layout.list_item_display, null);

            holder = new ViewHolder();
            holder.buildingName = (TextView) convertView.findViewById(R.id.list_item_building_name);
            holder.favoriteIndicator = (ImageView) convertView.findViewById(R.id.list_item_favorite_indicator);
            convertView.setTag(holder);


        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Building building = buildingList.get(position);

        holder.buildingName.setText(building.getName());

        if (!building.isfavorite) {
            holder.favoriteIndicator.setVisibility(View.GONE);
        } else {
            holder.favoriteIndicator.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    // private class for speed up and containment
    private class ViewHolder {
        TextView buildingName;
        ImageView favoriteIndicator;
    }

    /* ========================================================
       Filtering methods
       ======================================================== */

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new BuildingFilter();
        }
        return filter;
    }

    // a private filter class to search and change the list
    private class BuildingFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            if (constraint != null && constraint.toString().length() > 0) {

                ArrayList<Building> filteredItems = new ArrayList<Building>();

                for (int i = 0, l = originalList.size(); i < l; i++) {

                    Building building = originalList.get(i);

                    if (building.getName().toLowerCase().contains(constraint)){
                        filteredItems.add(building);
                    }
                }

                results.count = filteredItems.size();
                results.values = filteredItems;

            } else {
              synchronized (this) {
                  results.values = originalList;
                  results.count = originalList.size();
              }
            }
            return results;
        }

        // updated the list on changes
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            buildingList = (ArrayList<Building>) results.values;
            notifyDataSetChanged();
            clear();
            for(int i = 0, l = buildingList.size(); i < l; i++){
                add(buildingList.get(i));
            }
            notifyDataSetInvalidated();
        }

    }


    /* ========================================================
       Getting data from server
       ======================================================== */

    private static final String URL = "http://54.200.238.22:9000/";
    private static ArrayList<Building> BUILDING_LIST = null;

    /**
     *  Pulls the building collection from the server, then sets the
     *  internal ArrayList to contain all the Buildings.
     * @return the BuildingCollection as per the AsyncTask
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
     *  Takes a BuildingCollection and initializes the isFavorite value to false.
     *  It then loads the marked favorites in memory to the ArrayList<>. It then sorts the items.
     * @param bc the BuildingCollection that was pulled from the server
     * @return a sorted and initialized ArrayList of Buildings
     */
    private static ArrayList<Building> makeBuildingList (BuildingCollection bc) {
        ArrayList<Building> buildings = new ArrayList<Building>();

        for(Building b : bc.getBuildings())
        {
            b.isfavorite = false;
            buildings.add(b);
        }

        markFavoriteBuildings(buildings);

        sortbuildings(buildings);

        return buildings;
    }

    /**
     *  Creates a new ArrayList of the same objects as BUILDING_LIST
     *  This is because if you change the order of the copy, or delete
     *  an item, the original order and object are preserved.
     * @return An ArrayList of pointers to Buildings
     */
    public static ArrayList<Building> cloneBuildingList () {
        return new ArrayList<Building>(BUILDING_LIST);
    }

    /* ========================================================
       Sorting (assumes already in alphabetical order)
       ======================================================== */

    private static void sortbuildings (ArrayList<Building> buildings) {
        ArrayList<Building> favoritedBuildings = new ArrayList<>();

        // pulls out all the favorited items
        int i = buildings.size()-1;
        while (i > 0) {
            if (buildings.get(i).isfavorite) {
                favoritedBuildings.add(0,buildings.remove(i));
            }
            i--;
        }

        // sorts the remaining items
        Collections.sort(buildings);

        // adds the favorites back to the front of the list
        buildings.addAll(0,favoritedBuildings);
    }


    private static void markFavoriteBuildings (ArrayList<Building> buildings) {

        // gets the set of favorite items from memory
        Set<String> favoritesFromMemory = Settings.getFavoritesFromSettings();

        // marks them as true in the ArrayList
        //O(n * m) where m is the amount of favorites
        for (String s : favoritesFromMemory){
            for (Building b : buildings) {
                if (b.getName().equals(s)){
                    b.isfavorite = true;
                }
            }
        }
    }

    /* ========================================================
       Tours, not a good spot but they needed a home
       ======================================================== */

    private static ArrayList<Tour> TOUR_LIST = null;

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
