package com.osucse.wayfinding_api;

import java.util.*;

public class Segment {
    private int id;
    private double distance;
    private Location node1;
    private Location node2;


    public int getId() { return id; }

    public Location getNode1()
    {
        return node1;
    }

    public Location getNode2()
    {
        return node2;
    }

}
