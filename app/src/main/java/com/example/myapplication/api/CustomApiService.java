package com.example.myapplication.api;

import com.example.myapplication.data.ApodResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CustomApiService {

    @GET("v1/apod")
    Call<ApodResponse> getApod(
            @Query("date") String date,
            @Query("concept_tags") boolean conceptTags // Tambahkan ini
    );

}