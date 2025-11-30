package com.example.myapplication.data;

import com.google.gson.annotations.SerializedName;

public class MarsPhoto {
    @SerializedName("id")
    private int id;

    @SerializedName("sol")
    private int sol;

    @SerializedName("img_src")
    private String imgSrc;

    @SerializedName("earth_date")
    private String earthDate;

    @SerializedName("rover")
    private Rover rover;

    // Getters
    public int getId() { return id; }
    public String getImgSrc() { return imgSrc; }
    public String getEarthDate() { return earthDate; }
    public Rover getRover() { return rover; }

    public static class Rover {
        @SerializedName("name")
        private String name;

        public String getName() { return name; }
    }
}