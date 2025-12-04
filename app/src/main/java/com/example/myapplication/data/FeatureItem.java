package com.example.myapplication.data;

public class FeatureItem {
    private String title;
    private String description;
    private int imageResId;
    private int targetFragmentId;

    public FeatureItem(String title, String description, int imageResId, int targetFragmentId) {
        this.title = title;
        this.description = description;
        this.imageResId = imageResId;
        this.targetFragmentId = targetFragmentId;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getImageResId() { return imageResId; }
    public int getTargetFragmentId() { return targetFragmentId; }
}