package com.example.myapplication.fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.api.ApiClient;
import com.example.myapplication.api.NasaApiService;
import com.example.myapplication.api.OpenAiApiService; // Import API AI
import com.example.myapplication.data.ApodResponse;
import com.example.myapplication.data.OpenAiRequest; // Import Request AI
import com.example.myapplication.data.OpenAiResponse; // Import Response AI
import com.google.android.material.bottomsheet.BottomSheetDialog; // Import BottomSheet

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApodFragment extends Fragment {

    private static final String API_KEY = "LncZN7CyIoUydGsawLhWfopIUD05vPA0bcLUuMOZ";

    private ImageView imageApod;
    private TextView textTitle, textExplanation, textDate;
    private Button btnChangeDate, btnAskAi; // Ganti nama variabel biar jelas
    private ProgressBar progressBar;
    private Calendar calendar = Calendar.getInstance();

    // Variabel Global untuk data APOD saat ini
    private ApodResponse currentApodData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apod, container, false);

        imageApod = view.findViewById(R.id.image_apod);
        textTitle = view.findViewById(R.id.text_apod_title);
        textExplanation = view.findViewById(R.id.text_apod_explanation);
        textDate = view.findViewById(R.id.text_apod_date);
        btnChangeDate = view.findViewById(R.id.btn_change_date);

        // Tombol yang tadinya "Lihat Lebih Jelas" sekarang jadi tombol AI
        btnAskAi = view.findViewById(R.id.btn_view_full);
        progressBar = view.findViewById(R.id.progress_bar_apod);

        btnChangeDate.setOnClickListener(v -> showDatePicker());

        // --- LOGIKA BARU: TANYA AI ---
        btnAskAi.setOnClickListener(v -> explainApodWithAi());

        // Agar gambar tetap bisa diklik untuk melihat fullscreen (jalan pintas)
        imageApod.setOnClickListener(v -> openOriginalContent());

        calendar.add(Calendar.DAY_OF_MONTH, -1);
        fetchApodData(formatDate(calendar));

        return view;
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year1, month1, dayOfMonth) -> {
            calendar.set(year1, month1, dayOfMonth);
            fetchApodData(formatDate(calendar));
        }, year, month, day);

        long yesterday = System.currentTimeMillis() - 86400000;
        datePickerDialog.getDatePicker().setMaxDate(yesterday);
        Toast.makeText(getContext(), "Pilih tanggal (Maksimal kemarin)", Toast.LENGTH_SHORT).show();
        datePickerDialog.show();
    }

    private String formatDate(Calendar cal) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return sdf.format(cal.getTime());
    }

    private void fetchApodData(String date) {
        progressBar.setVisibility(View.VISIBLE);
        btnAskAi.setVisibility(View.GONE);
        currentApodData = null; // Reset data

        NasaApiService apiService = ApiClient.getClient().create(NasaApiService.class);
        Call<ApodResponse> call = apiService.getApod(API_KEY, date);

        call.enqueue(new Callback<ApodResponse>() {
            @Override
            public void onResponse(Call<ApodResponse> call, Response<ApodResponse> response) {
                if (getContext() == null || !isAdded()) return;
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    currentApodData = response.body(); // Simpan data ke variabel global

                    textTitle.setText(currentApodData.getTitle());
                    textExplanation.setText(currentApodData.getExplanation());
                    textDate.setText("Date: " + date);

                    // Tampilkan tombol AI
                    btnAskAi.setVisibility(View.VISIBLE);
                    btnAskAi.setText("Jelasin dong ke aku! 🤖"); // Ubah teks tombol

                    if ("image".equals(currentApodData.getMediaType())) {
                        Glide.with(getContext()).load(currentApodData.getUrl()).into(imageApod);
                    } else {
                        imageApod.setImageResource(R.drawable.ic_video);
                        Toast.makeText(getContext(), "Hari ini video! Klik gambar untuk memutar.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Data belum tersedia.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApodResponse> call, Throwable t) {
                if (getContext() == null || !isAdded()) return;
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- FUNGSI BARU: TANYA AI (A4F) ---
    private void explainApodWithAi() {
        if (currentApodData == null) return;

        // 1. Tampilkan Bottom Sheet
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_apod_ai, null);
        bottomSheetDialog.setContentView(sheetView);

        TextView textExplanation = sheetView.findViewById(R.id.text_sheet_explanation);
        ProgressBar loading = sheetView.findViewById(R.id.progress_sheet);
        Button btnOpenOriginal = sheetView.findViewById(R.id.btn_open_original);

        loading.setVisibility(View.VISIBLE);
        textExplanation.setText("AstroBot sedang membaca foto ini...");

        // Tombol di dalam sheet untuk buka gambar asli/video
        btnOpenOriginal.setOnClickListener(v -> openOriginalContent());

        bottomSheetDialog.show();

        // 2. Siapkan Prompt
        String systemPrompt = "Kamu adalah pemandu wisata antariksa yang ramah untuk anak-anak. Jelaskan dengan bahasa Indonesia yang seru, mudah dimengerti, dan singkat.";
        String userPrompt = "Jelaskan foto astronomi ini:\n" +
                "Judul: " + currentApodData.getTitle() + "\n" +
                "Deskripsi Asli (Inggris): " + currentApodData.getExplanation() + "\n\n" +
                "Tolong ceritakan apa yang terjadi di foto ini seolah-olah kamu sedang bercerita ke anak SD. Gunakan analogi jika perlu.";

        // 3. Panggil API AI (A4F)
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.a4f.co/v1/") // URL Provider A4F
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OpenAiApiService apiService = retrofit.create(OpenAiApiService.class);
        OpenAiRequest request = new OpenAiRequest(systemPrompt, userPrompt);

        apiService.getChatCompletion(request).enqueue(new Callback<OpenAiResponse>() {
            @Override
            public void onResponse(Call<OpenAiResponse> call, Response<OpenAiResponse> response) {
                if (getContext() == null) return;
                loading.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    String aiReply = response.body().choices.get(0).message.content;
                    textExplanation.setText(aiReply);
                } else {
                    textExplanation.setText("Maaf, AstroBot sedang pusing (Gagal koneksi AI).");
                }
            }

            @Override
            public void onFailure(Call<OpenAiResponse> call, Throwable t) {
                if (getContext() == null) return;
                loading.setVisibility(View.GONE);
                textExplanation.setText("Batas Per Menit habis silahkan tunggu 1 menit");
            }
        });
    }

    // --- FUNGSI: BUKA CONTENT ASLI (HD/VIDEO) ---
    private void openOriginalContent() {
        if (currentApodData == null) return;

        String url = currentApodData.getHdUrl() != null ? currentApodData.getHdUrl() : currentApodData.getUrl();

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Tidak bisa membuka link.", Toast.LENGTH_SHORT).show();
        }
    }
}