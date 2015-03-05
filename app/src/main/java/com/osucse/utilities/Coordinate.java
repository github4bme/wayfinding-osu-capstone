package com.osucse.utilities;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by tjf3191 on 2/16/15.
 */
public class Coordinate {

    /**
     *  The max distance for points to be considered as the same point (in feet)
     */
    private static double EPSILON = 1.5;

    /**
     * TYPE is for the constructor to differ which data is the original data.
     */
    public static enum TYPE {
        NAD_27,
        GCS
    };

    /**
     * The private class data fields.
     */
    private double eastling;
    private double northling;
    private double latitude;
    private double longitude;

    /**
     * @return the eastling value
     */
    @JsonIgnore
    public double getEastling(){
        return this.eastling;
    }

    /**
     * @return the northling value
     */
    @JsonIgnore
    public double getNorthling(){
        return this.northling;
    }

    /**
     * @return the latitude value
     */
    public double getLatitude(){
        return this.latitude;
    }

    /**
     * @return the longitude value
     */
    public double getLongitude(){
        return this.longitude;
    }

    /**
     * Calculates the distance between two points in feet.
     * @param c1 a coordinate
     * @param c2 another coordinate
     * @return the double value of the distance between the two points
     */
    public static double distance (Coordinate c1, Coordinate c2) {
        return Math.sqrt(((c1.getEastling() - c2.getEastling()) * (c1.getEastling() - c2.getEastling())) +
                ((c1.getNorthling() - c2.getNorthling()) * (c1.getNorthling() - c2.getNorthling())));
    }

    /**
     * Compares two points by distance, and determines if the points are
     *   close enough to be considered as the same point.
     * @param c1 a coordinate
     * @param c2 another coordinate
     * @return true if the points are within EPSILON and false otherwise
     */
    public static boolean isSamePoint (Coordinate c1, Coordinate c2) {
        return distance(c1, c2) < EPSILON;
    }

    /**
     * Overides the toString method for pretty output.
     * @return a string representation of the data within
     */
    @Override
    public String toString () {
        String nad27 = String.format("Eastling:  %7.5f\nNorthling: %7.5f\n", this.getEastling(), this.getNorthling());
        String gcs = String.format("Latitude:  %7.5f\nLongitude: %7.5f", this.getLatitude(), this.getLongitude());
        return nad27 + gcs;
    }
}
