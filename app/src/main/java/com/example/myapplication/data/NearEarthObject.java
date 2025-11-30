package com.example.myapplication.data;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class NearEarthObject {
    @SerializedName("name")
    private String name;

    @SerializedName("estimated_diameter")
    private EstimatedDiameter estimatedDiameter;

    @SerializedName("close_approach_data")
    private List<CloseApproachData> closeApproachData;

    // Getters
    public String getName() { return name; }
    public EstimatedDiameter getEstimatedDiameter() { return estimatedDiameter; }
    public List<CloseApproachData> getCloseApproachData() { return closeApproachData; }
}