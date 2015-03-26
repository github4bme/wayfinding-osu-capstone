package com.osucse.wayfinding_osu_capstone;

/**
 * Created by tjf3191 on 3/26/15.
 */
public class LocationTuple implements Comparable<LocationTuple>{
    private String name;
    private String id;

    LocationTuple(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName () {
        return this.name;
    }

    public String getId () {
        return this.id;
    }

    public String toString () {
        return this.name;
    }

    /**
     * Must have override for my adding of the Comparable<Location> implementation
     * @param comp the item that this is being compared to
     * @return an integer value (<(-1) for less than, 0 for equals, and >(1) for greater than)
     */
    @Override
    public int compareTo(LocationTuple comp) {
        return this.getName().toLowerCase().compareTo(comp.getName().toLowerCase());
    }
}
