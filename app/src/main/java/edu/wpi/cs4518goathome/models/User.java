package edu.wpi.cs4518goathome.models;

/**
 * Created by Kevin O'Brien on 2/22/2018.
 */

public class User {
    public String name;
    public String major;
    public String phoneNumber;


    public User() {
    }

    public User(String name, String major, String phoneNumber) {
        this.name = name;
        this.major = major;
        this.phoneNumber = phoneNumber;
    }

    public static String getProfilePic(String uid) {
        return "/" + uid +"/profilePic.png";
    }
}
