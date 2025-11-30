package com.example.myapplication.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.ExploreQuizActivity; // Pastikan ini di-import
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.adapter.FeatureAdapter;
import com.example.myapplication.data.FeatureItem;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private FeatureAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- 1. LOGIKA TOMBOL EXPLORE (KUIS) ---
        // Menghubungkan tombol di banner ke Activity Kuis
        Button btnBannerExplore = view.findViewById(R.id.btn_banner_explore);

        btnBannerExplore.setOnClickListener(v -> {
            // Membuka ExploreQuizActivity saat tombol diklik
            Intent intent = new Intent(getActivity(), ExploreQuizActivity.class);
            startActivity(intent);
        });

        // --- 2. LOGIKA DAFTAR FITUR (RECYCLERVIEW) ---
        recyclerView = view.findViewById(R.id.recycler_features);

        // Konfigurasi LayoutManager Vertikal
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        // Agar scroll lancar di dalam NestedScrollView
        recyclerView.setNestedScrollingEnabled(false);

        // Siapkan Data Fitur
        List<FeatureItem> features = new ArrayList<>();

        features.add(new FeatureItem(
                "APOD",
                "Melihat apa yang terjadi di angkasa hari ini",
                R.drawable.bg_apod_placeholder,
                R.id.nav_apod
        ));

        features.add(new FeatureItem(
                "NEO Tracker",
                "Pantau asteroid yang mendekati Bumi",
                R.drawable.bg_asteroid_placeholder,
                R.id.nav_neo
        ));

        features.add(new FeatureItem(
                "Solar System",
                "Jelajahi model interaktif tata surya",
                R.drawable.bg_solar_placeholder,
                R.id.nav_solar_system
        ));

        // Setup Adapter dan Klik Listener untuk pindah Tab
        adapter = new FeatureAdapter(features, fragmentId -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragmentFromHome(fragmentId);
            }
        });

        recyclerView.setAdapter(adapter);
    }
}