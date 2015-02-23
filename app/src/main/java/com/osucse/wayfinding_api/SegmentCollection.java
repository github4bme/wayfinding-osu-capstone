package com.osucse.wayfinding_api;

import java.util.*;

public class SegmentCollection {
    private  List<com.osucse.wayfinding_api.Segment> segments;


    public List<com.osucse.wayfinding_api.Segment> getSegments(){
        return segments;
    }

    public void add(int i, com.osucse.wayfinding_api.Segment s){
        if(!segments.contains(s)){
            segments.add(i, s);
        }
    }

}
