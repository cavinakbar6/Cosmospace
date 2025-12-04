package com.example.myapplication.api;

import com.example.myapplication.data.OpenAiRequest;
import com.example.myapplication.data.OpenAiResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface OpenAiApiService {

    @Headers({
            "Content-Type: application/json",
            "Authorization: Bearer ddc-a4f-6dfaec42b2914655ad7d5634eaaef031"
    })
    @POST("chat/completions")
    Call<OpenAiResponse> getChatCompletion(@Body OpenAiRequest request);
}