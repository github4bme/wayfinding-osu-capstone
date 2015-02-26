package com.osucse.wayfinding_api;

public class Location {
    private int id;
    private String name;
    private int distanceFromStart;
    private boolean visited;
    private double latitude;
    private double longitude;

    public int getDistanceFromStart() {
        return distanceFromStart;
    }

    public void setDistanceFromStart(int distanceFromStart) {
        this.distanceFromStart = distanceFromStart;
    }

    public String getName()
    {
        return name;
    }

    public int getId() {return id;}

    public boolean isVisited()
    {
        return this.visited;
    }

    public void setVisited(boolean visited)
    {
        this.visited = visited;
    }

    public double getLatitude(){ return latitude; }

    public double getLongitude() {  return longitude; }

}