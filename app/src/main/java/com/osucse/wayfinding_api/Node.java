package com.osucse.wayfinding_api;

import com.osucse.wayfinding_api.utilities.Coordinate;

public class Node {
    private Coordinate coordinate;

    public Node(Coordinate coord)
    {
        this.coordinate = coord;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }
}
