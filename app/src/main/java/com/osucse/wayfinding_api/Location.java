package com.osucse.wayfinding_api;

import java.util.ArrayList;

public class Location{
    private final int id;
    private final String name;
    private final double spcx;
    private final double spcy;

    public Location(int id, String name, double spcx, double spcy) {
        this.id = id;
        this.name = name;
        this.spcx = spcx;
        this.spcy = spcy;
    }

    public int getId() {return id;}

    public String getName()
    {
        return name;
    }

    public double getSpcx(){ return spcx; }

    public double getSpcy() {  return spcy; }

}