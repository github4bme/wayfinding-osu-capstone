package com.osucse.wayfinding_api;

import java.util.ArrayList;
import java.util.List;

public class TourCollection
{
    private List<Tour> tours;

    public TourCollection()
    {
        tours = new ArrayList<Tour>();
    }

    public TourCollection(List<Tour> tours)
    {
        this.tours = tours;
    }

    public List<Tour> getTours() {
        return tours;
    }

    public void add(int i, Tour t){
        if(!tours.contains(t)){
            tours.add(i, t);
        }
    }

    public Tour getTour(int id)
    {
        for(Tour t : tours)
        {
            if(t.getId() == id)
            {
                return t;
            }
        }

        return null;
    }
}
