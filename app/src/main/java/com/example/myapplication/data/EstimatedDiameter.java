package com.example.myapplication.data;

import com.google.gson.annotations.SerializedName;

public class EstimatedDiameter {
    @SerializedName("meters")
    private Diameter meters;

    public Diameter getMeters() { return meters; }

    public static class Diameter {
        @SerializedName("estimated_diameter_max")
        private double estimatedDiameterMax;

        public double getEstimatedDiameterMax() { return estimatedDiameterMax; }
    }
}