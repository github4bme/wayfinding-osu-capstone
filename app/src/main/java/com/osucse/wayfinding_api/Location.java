package com.osucse.wayfinding_api;

import com.osucse.utilities.Coordinate;

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
        return this.name;
    }


    @Override
    public int compareTo(Location comp) {
        return this.getName().compareTo(comp.getName());
    }

}