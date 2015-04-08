package com.osucse.wayfinding_api;

import java.util.List;

/**
 * Created by thomasforte on 3/31/15.
 */
public class BuildingCollection {
    private List<Building> buildings;

    public List<Building> getBuildings() {
        return buildings;
    }

    public void add(int i, Building b){
        if(!buildings.contains(b)){
            buildings.add(i, b);
        }
    }
}
