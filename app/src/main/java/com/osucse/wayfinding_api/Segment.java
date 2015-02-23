package com.osucse.wayfinding_api;

import java.util.*;

public class Segment {
    private int id;
    private double distance;
    private Location Node1;
    private Location Node2;


    public int getId() { return id; }

    public Location getNode1()
    {
        return Node1;
    }

    public Location getNode2()
    {
        return Node2;
    }

}
