package com.example.myapplication.data;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

public class NeoResponse {
    @SerializedName("near_earth_objects")
    private Map<String, List<NearEarthObject>> nearEarthObjects;

    public Map<String, List<NearEarthObject>> getNearEarthObjects() {
        return nearEarthObjects;
    }
}