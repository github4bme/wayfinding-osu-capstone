package com.osucse.wayfinding_api;

//import java.util.*;
//
//public class SegmentCollection {
//    private  List<com.osucse.wayfinding_api.Segment> segments;
//
//
//    public List<com.osucse.wayfinding_api.Segment> getSegments(){
//        return segments;
//    }
//
//    public void add(int i, com.osucse.wayfinding_api.Segment s){
//        if(!segments.contains(s)){
//            segments.add(i, s);
//        }
//    }
//
//}

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jim on 2/13/2015.
 */
public class SegmentCollection {
    private List<Segment> segments;

    public List<Segment> getSegments(){
        return segments;
    }
}