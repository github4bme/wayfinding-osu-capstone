package com.osucse.wayfinding_api;

public class Location extends Node implements Comparable<Location>{
    private String name;

    private int id;

    public int getId() {
        return id;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public String toString()
    {
        return getName(); // for the array adapter
    }

    /**
     * Must have override for my adding of the Comparable<Location> implementation
     * @param comp the item that this is being compared to
     * @return an integer value (<(-1) for less than, 0 for equals, and >(1) for greater than)
     */
    @Override
    public int compareTo(Location comp) {
        return this.getName().toLowerCase().compareTo(comp.getName().toLowerCase());
    }
}