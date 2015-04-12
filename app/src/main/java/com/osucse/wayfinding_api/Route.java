package com.osucse.wayfinding_api;

import java.util.List;

public class Route {
    private Door startDoor;
    private Door endDoor;
    private double lengthInFeet;
    private List<Node> route; // need to remove final

    public List<Node> getRoute(){
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