package com.example.myapplication.data;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MarsPhotoResponse {
    @SerializedName("photos")
    private List<MarsPhoto> photos;

    public List<MarsPhoto> getPhotos() {
        return photos;
    }
}