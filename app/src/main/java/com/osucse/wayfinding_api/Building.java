package com.osucse.wayfinding_api;

import java.util.List;

/**
 * Created by thomasforte on 3/31/15.
 */
public class Building implements Comparable<Building>{

    private String name;
    private int id;
    private int buildingId;
    private List<Door> doors;

    public boolean favorited;

    public String getName () { return this.name; }

    public List<Door> getDoors () { return this.doors; }

    public int getId () { return this.id; }

    public String toString () { return this.name; }

    public int getBuildingId () { return this.buildingId; }

    /**
     * Must have override for my adding of the Comparable<Location> implementation
     * @param comp the item that this is being compared to
     * @return an integer value (<(-1) for less than, 0 for equals, and >(1) for greater than)
     */
    @Override
    public int compareTo(Building comp) {
        return this.getName().toLowerCase().compareTo(comp.getName().toLowerCase());
    }
}

