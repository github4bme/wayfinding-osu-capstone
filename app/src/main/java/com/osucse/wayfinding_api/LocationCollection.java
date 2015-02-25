//package com.osucse.wayfinding_api;
//
//
//import java.util.*;
//
//
//public class LocationCollection
//{
//    private List<Location> locations;
//
//    public List<Location> getLocations() {
//        return locations;
//    }
//
//    public void add(int i, Location l){
//        if(!locations.contains(l)){
//            locations.add(i, l);
//        }
//    }
//}

package com.osucse.wayfinding_api;;

import java.util.*;

public class LocationCollection
{
    private List<Location> locations;

    public List<Location> getLocations() {
        return locations;
    }

    public void add(int i, Location l){
        if(!locations.contains(l)){
            locations.add(i, l);
        }
    }
}