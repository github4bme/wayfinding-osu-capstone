package com.osucse.wayfinding_api;

import com.osucse.utilities.Coordinate;

import java.util.List;

public class Route {
    private Door startDoor;
    private Door endDoor;
    private double lengthInFeet;
    private List<Coordinate> route; // need to remove final

    public List<Coordinate> getRoute(){
        return route;
    }

    public Door getStartDoor() {
        return startDoor;
    }

    public Door getEndDoor() {
        return endDoor;
    }

    public double getLengthInFeet() {
        return lengthInFeet;
    }
}