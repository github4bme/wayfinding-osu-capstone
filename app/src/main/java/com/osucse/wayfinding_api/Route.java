package com.osucse.wayfinding_api;

import java.util.List;

public class Route {
    private Location startLocation;
    private Location endLocation;
    private double lengthInFeet;
    private List<Node> route; // need to remove final

    public List<Node> getRoute(){
        return route;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public Location getEndLocation() {
        return endLocation;
    }

    public double getLengthInFeet() {
        return lengthInFeet;
    }
}