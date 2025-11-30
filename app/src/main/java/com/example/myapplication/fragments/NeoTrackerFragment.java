package com.example.myapplication.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.NeoAdapter;
import com.example.myapplication.api.ApiClient;
import com.example.myapplication.api.NasaApiService;
import com.example.myapplication.api.OpenAiApiService; // Import untuk AI
import com.example.myapplication.data.NearEarthObject;
import com.example.myapplication.data.NeoResponse;
import com.example.myapplication.data.OpenAiRequest; // Import Request AI
import com.example.myapplication.data.OpenAiResponse; // Import Response AI
import com.google.android.material.bottomsheet.BottomSheetDialog; // Import BottomSheet

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NeoTrackerFragment extends Fragment {

    // API Key NASA Anda
    private static final String NASA_API_KEY = "LncZN7CyIoUydGsawLhWfopIUD05vPA0bcLUuMOZ";

    private RecyclerView recyclerView;
    private NeoAdapter neoAdapter;
    private List<NearEarthObject> neoList = new ArrayList<>();
    private TextView textCurrentDate;
    private ProgressBar progressBar;
    private Button btnChangeDate;
    private Calendar calendar = Calendar.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_neo_tracker, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_neo);
        textCurrentDate = view.findViewById(R.id.text_neo_date);
        progressBar = view.findViewById(R.id.progress_bar_neo);
        btnChangeDate = view.findViewById(R.id.btn_change_date_neo);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // --- Setup Adapter dengan Listener Klik ---
        neoAdapter = new NeoAdapter(getContext(), neoList, neo -> {
            // Saat item diklik, panggil fungsi penjelasan AI
            askAiAboutAsteroid(neo);
        });
        recyclerView.setAdapter(neoAdapter);

        btnChangeDate.setOnClickListener(v -> showDatePicker());

        fetchNeoData(formatDate(calendar));

        return view;
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            fetchNeoData(formatDate(calendar));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private String formatDate(Calendar cal) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return sdf.format(cal.getTime());
    }

    private void fetchNeoData(String date) {
        progressBar.setVisibility(View.VISIBLE);
        textCurrentDate.setText("Asteroids near Earth on: " + date);

        NasaApiService apiService = ApiClient.getClient().create(NasaApiService.class);
        Call<NeoResponse> call = apiService.getNearEarthObjects(date, date, NASA_API_KEY);

        call.enqueue(new Callback<NeoResponse>() {
            @Override
            public void onResponse(Call<NeoResponse> call, Response<NeoResponse> response) {
                if (getContext() == null || !isAdded()) return;

                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, List<NearEarthObject>> neoMap = response.body().getNearEarthObjects();
                    neoList.clear();
                    if (neoMap != null && neoMap.containsKey(date)) {
                        neoList.addAll(neoMap.get(date));
                    }
                    neoAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Failed to fetch NEO data.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<NeoResponse> call, Throwable t) {
                if (getContext() == null || !isAdded()) return;
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- LOGIKA AI TENTANG ASTEROID (UPDATE A4F) ---
    private void askAiAboutAsteroid(NearEarthObject neo) {
        // 1. Tampilkan Bottom Sheet Loading
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_neo_ai, null);
        bottomSheetDialog.setContentView(sheetView);

        TextView title = sheetView.findViewById(R.id.text_sheet_title);
        TextView content = sheetView.findViewById(R.id.text_sheet_explanation);
        ProgressBar loading = sheetView.findViewById(R.id.progress_sheet);

        title.setText("Analisis: " + neo.getName());
        content.setText("Sedang berpikir...");
        loading.setVisibility(View.VISIBLE);

        bottomSheetDialog.show();

        // 2. Siapkan Data untuk Prompt
        double diameter = neo.getEstimatedDiameter().getMeters().getEstimatedDiameterMax();
        String speed = "N/A";
        if (!neo.getCloseApproachData().isEmpty()) {
            speed = neo.getCloseApproachData().get(0).getRelativeVelocity().getKilometersPerHour();
        }

        // 3. Buat Prompt Kreatif
        String systemPrompt = "Kamu adalah guru sains anak-anak yang lucu. Jelaskan fakta dengan analogi benda sehari-hari.";
        String userPrompt = "Jelaskan asteroid bernama " + neo.getName() + ".\n" +
                "- Diameternya " + (int)diameter + " meter.\n" +
                "- Kecepatannya " + speed + " km/jam.\n" +
                "Berikan perbandingan yang mudah dibayangkan anak SD (misal: lapangan bola, monas, pesawat). Jangan terlalu teknis. Pakai bahasa santai.";

        // 4. Panggil Provider a4f (UPDATED)
        Retrofit retrofit = new Retrofit.Builder()
                // --- PERUBAHAN: Base URL diarahkan ke a4f ---
                .baseUrl("https://api.a4f.co/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // -------------------------------------------

        OpenAiApiService apiService = retrofit.create(OpenAiApiService.class);
        OpenAiRequest request = new OpenAiRequest(systemPrompt, userPrompt);

        apiService.getChatCompletion(request).enqueue(new Callback<OpenAiResponse>() {
            @Override
            public void onResponse(Call<OpenAiResponse> call, Response<OpenAiResponse> response) {
                if (getContext() == null) return;
                loading.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    String aiReply = response.body().choices.get(0).message.content;
                    content.setText(aiReply);
                } else {
                    content.setText("Maaf, AstroBot sedang tidur (Gagal koneksi AI).");
                }
            }

            @Override
            public void onFailure(Call<OpenAiResponse> call, Throwable t) {
                if (getContext() == null) return;
                loading.setVisibility(View.GONE);
                content.setText("Gagal terhubung ke internet.");
            }
        });
    }
}