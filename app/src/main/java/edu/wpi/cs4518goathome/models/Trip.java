package edu.wpi.cs4518goathome.models;

/**
 * Created by Kevin O'Brien on 2/23/2018.
 */

public class Trip {
    public String name;
    public String desc;
    public String driver;
    public double latitude;
    public double longitude;
    public String date;
    public double cost;

    public Trip(String name, String desc, String driver, double latitude, double longitude, String date, double cost) {
        this.name = name;
        this.desc = desc;
        this.driver = driver;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.cost = cost;
    }
    public Trip() {}
}
