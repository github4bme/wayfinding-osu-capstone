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

    public String getName () { return this.name.trim(); }

    public List<Door> getDoors () { return this.doors; }

    public int getId () { return this.id; }

    public String toString () { return this.name; }

    public int getBuildingId () { return this.buildingId; }

    /**
     * Must have override for my adding of the Comparable<Location> implementation
     * @param o the item that this is being compared to
     * @return an integer value (<(-1) for less than, 0 for equals, and >(1) for greater than)
     */
    @Override
    public int compareTo(Building o) {
        String o1 = this.getName();
        String o2 = o.getName();

        if (o1.charAt(0) < 58 && o2.charAt(0) < 58) {
            return Character.getNumericValue(o1.charAt(0)) - Character.getNumericValue(o2.charAt(0));
        } else if (o1.charAt(0) < 58 && o2.charAt(0) > 58) {
            return 100;
        } else if (o1.charAt(0) > 58 && o2.charAt(0) < 58) {
            return -100;
        } else {
            return o1.toLowerCase().compareTo(o2.toLowerCase());
        }
    }
}

