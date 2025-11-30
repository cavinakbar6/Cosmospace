package com.example.myapplication.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.MarsPhotoAdapter;
import com.example.myapplication.api.ApiClient;
import com.example.myapplication.api.NasaApiService;
import com.example.myapplication.data.MarsPhoto;
import com.example.myapplication.data.MarsPhotoResponse;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MarsRoverFragment extends Fragment {

    // !!! GANTI DENGAN API KEY ANDA !!!
    private static final String API_KEY = "LncZN7CyIoUydGsawLhWfopIUD05vPA0bcLUuMOZ";
    private static final int LATEST_SOL = 4000; // Ambil foto dari Sol (hari Mars) sekitar ini

    private RecyclerView recyclerView;
    private MarsPhotoAdapter adapter;
    private List<MarsPhoto> photoList = new ArrayList<>();
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mars_rover, container, false);

        progressBar = view.findViewById(R.id.progress_bar_mars);
        recyclerView = view.findViewById(R.id.recycler_view_mars_photos);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // Tampilan grid 2 kolom
        adapter = new MarsPhotoAdapter(getContext(), photoList);
        recyclerView.setAdapter(adapter);

        fetchMarsPhotos();

        return view;
    }

    private void fetchMarsPhotos() {
        progressBar.setVisibility(View.VISIBLE);
        NasaApiService apiService = ApiClient.getClient().create(NasaApiService.class);
        Call<MarsPhotoResponse> call = apiService.getMarsRoverPhotos(LATEST_SOL, API_KEY);

        call.enqueue(new Callback<MarsPhotoResponse>() {
            @Override
            public void onResponse(Call<MarsPhotoResponse> call, Response<MarsPhotoResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    photoList.clear();
                    photoList.addAll(response.body().getPhotos());
                    adapter.notifyDataSetChanged();
                    if (photoList.isEmpty()) {
                        Toast.makeText(getContext(), "No photos found for the latest Sol. Try again later.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to fetch Mars photos.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MarsPhotoResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}