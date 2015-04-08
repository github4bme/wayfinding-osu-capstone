package com.osucse.wayfinding_api;

public class Location extends Node{
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
        return "Location: " + this.name + " -- Lat: " + this.getCoordinate().getLatitude() + " Long: " + this.getCoordinate().getLongitude();
    }
}