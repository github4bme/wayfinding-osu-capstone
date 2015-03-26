package com.osucse.utilities;

/**
 * Created by tjf3191 on 3/26/15.
 */
public class LocationTuple implements Comparable<LocationTuple>{

    /**
     * Private class data fields
     */
    private String name;
    private String id;

    /**
     * Constructor for building tuples
     * @param name  the name of the location
     * @param id    the id of the location
     */
    public LocationTuple(String name, String id) {
        this.name = name;
        this.id = id;
    }

    /**
     * @return the location name
     */
    public String getName () {
        return this.name;
    }

    /**
     * @return the location id
     */
    public String getId () {
        return this.id;
    }

    /**
     * Overides the default toString for displaying properly in a listView
     * @return the name of the location
     */
    @Override
    public String toString () {
        return this.name;
    }

    /**
     * For sorting an array list of LocationTuples alphabetically
     * @param comp the item that this is being compared to
     * @return an integer value (<(-1) for less than, 0 for equals, and >(1) for greater than)
     */
    @Override
    public int compareTo(LocationTuple comp) {
        return this.getName().toLowerCase().compareTo(comp.getName().toLowerCase());
    }
}
