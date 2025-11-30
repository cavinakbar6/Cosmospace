package com.example.myapplication.data;

import com.google.gson.annotations.SerializedName;

public class ApodResponse {
    @SerializedName("title")
    private String title;

    @SerializedName("explanation")
    private String explanation;

    @SerializedName("url")
    private String url;

    @SerializedName("hdurl")
    private String hdUrl;

    @SerializedName("media_type")
    private String mediaType; // "image" or "video"

    // Getters
    public String getTitle() { return title; }
    public String getExplanation() { return explanation; }
    public String getUrl() { return url; }
    public String getHdUrl() { return hdUrl; }
    public String getMediaType() { return mediaType; }
}