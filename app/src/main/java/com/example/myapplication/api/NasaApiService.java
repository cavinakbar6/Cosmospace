package com.example.myapplication.api;

import com.example.myapplication.data.ApodResponse;
import com.example.myapplication.data.NeoResponse;
import com.example.myapplication.data.MarsPhotoResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NasaApiService {

    // APOD: Astronomy Picture of the Day
    @GET("planetary/apod")
    Call<ApodResponse> getApod(
            @Query("api_key") String apiKey,
            @Query("date") String date // Format: YYYY-MM-DD
    );

    // NEO: Near Earth Objects
    @GET("neo/rest/v1/feed")
    Call<NeoResponse> getNearEarthObjects(
            @Query("start_date") String startDate,
            @Query("end_date") String endDate,
            @Query("api_key") String apiKey
    );

    // Mars Rover Photos
    @GET("mars-photos/api/v1/rovers/curiosity/photos")
    Call<MarsPhotoResponse> getMarsRoverPhotos(
            @Query("sol") int sol,
            @Query("api_key") String apiKey
    );

}

