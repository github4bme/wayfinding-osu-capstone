package com.osucse.wayfinding_api;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by delta on 4/1/2015.
 */
public class SegmentCollection {
    private static List<Segment> segments;

    public List<Segment> getSegments(){
        return segments;
    }

    public void add(int i, Segment s){
        if(!segments.contains(s)){
            segments.add(i, s);
        }
    }
}
