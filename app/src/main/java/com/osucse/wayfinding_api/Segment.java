package com.osucse.wayfinding_api;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by Jim on 2/13/2015.
 */
public class Segment {
    private int id;
    private int weight;
    private int accessible;
    private String streetCrossing;
    private String description;
    private String hazard;
    private Location toNode;
    private Location fromNode;
    private List<Location> intermediateNodes;


    public int getId(){
        return id;
    }

    public String getStreetCrossing()
    {
        return streetCrossing;
    }

    public String getDescription()
    {
        return description;
    }

    public String getHazard()
    {
        return hazard;
    }

    public int getWeight() {return weight;}

    public int getAccessible() {return accessible;}

    public Location getToNode()
    {
        return toNode;
    }

    public Location getFromNode()
    {
        return fromNode;
    }

    public int getNeighbourIndex(int nodeIndex) {
        if (this.toNode.getId() == nodeIndex) {
            return this.toNode.getId();
        } else {
            return this.fromNode.getId();
        }
    }

}