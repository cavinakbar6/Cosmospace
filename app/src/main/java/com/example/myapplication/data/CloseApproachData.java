package com.example.myapplication.data;

import com.google.gson.annotations.SerializedName;

public class CloseApproachData {
    @SerializedName("relative_velocity")
    private RelativeVelocity relativeVelocity;

    @SerializedName("miss_distance")
    private MissDistance missDistance;

    // Getters
    public RelativeVelocity getRelativeVelocity() { return relativeVelocity; }
    public MissDistance getMissDistance() { return missDistance; }

    public static class RelativeVelocity {
        @SerializedName("kilometers_per_hour")
        private String kilometersPerHour;

        public String getKilometersPerHour() { return kilometersPerHour; }
    }

    public static class MissDistance {
        @SerializedName("kilometers")
        private String kilometers;

        public String getKilometers() { return kilometers; }
    }
}