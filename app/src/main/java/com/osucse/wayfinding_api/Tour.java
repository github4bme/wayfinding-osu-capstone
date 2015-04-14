package com.osucse.wayfinding_api;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

/**
 * Created by JasonDesktop on 4/13/2015.
 */
public class Tour implements Comparable<Tour>
{
    private String name;
    private int id;
    private Route route;

    public String getName()
    {
        return name;
    }

    public int getId()
    {
        return id;
    }

    @JsonIgnore
    public Route getRoute() { return route; }

    /**
     * Must have override for my adding of the Comparable<Location> implementation
     * @param comp the item that this is being compared to
     * @return an integer value (<(-1) for less than, 0 for equals, and >(1) for greater than)
     */
    @Override
    public int compareTo(Tour comp) {
        return this.getName().toLowerCase().compareTo(comp.getName().toLowerCase());
    }

    @Override
    public String toString(){
        return this.name;
    }
}
