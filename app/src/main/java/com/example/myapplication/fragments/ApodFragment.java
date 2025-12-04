package com.example.myapplication.fragments;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.example.myapplication.api.OpenAiApiService;
import com.example.myapplication.data.ApodResponse;
import com.example.myapplication.data.OpenAiRequest;
import com.example.myapplication.data.OpenAiResponse;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApodFragment extends Fragment {

    // API Key NASA
    private static final String API_KEY = "LncZN7CyIoUydGsawLhWfopIUD05vPA0bcLUuMOZ";
    private ImageView imageApod;
    private TextView textTitle, textExplanation, textDate;
    private Button btnChangeDate, btnAskAi;
    private ProgressBar progressBar;

    private LinearLayout layoutExpandable;
    private ImageView btnToggleInfo;
    private View layoutInfoContainer;
    private boolean isInfoExpanded = true;

    private Calendar calendar = Calendar.getInstance();
    private ApodResponse currentApodData;
    private Handler typingHandler = new Handler(Looper.getMainLooper());
    private Runnable typingRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apod, container, false);

        imageApod = view.findViewById(R.id.image_apod);
        textTitle = view.findViewById(R.id.text_apod_title);
        textExplanation = view.findViewById(R.id.text_apod_explanation);
        textDate = view.findViewById(R.id.text_apod_date);
        btnChangeDate = view.findViewById(R.id.btn_change_date);
        btnAskAi = view.findViewById(R.id.btn_view_full);
        progressBar = view.findViewById(R.id.progress_bar_apod);
        layoutExpandable = view.findViewById(R.id.layout_expandable_content);
        btnToggleInfo = view.findViewById(R.id.btn_toggle_info);
        layoutInfoContainer = view.findViewById(R.id.layout_info_container);
        btnChangeDate.setOnClickListener(v -> showDatePicker());
        btnAskAi.setOnClickListener(v -> explainApodWithAi());
        imageApod.setOnClickListener(v -> openOriginalContent());

        textExplanation.setOnClickListener(v -> {
            if (currentApodData != null) {
                typingHandler.removeCallbacks(typingRunnable);
                textExplanation.setText(currentApodData.getExplanation());
            }
        });
        btnToggleInfo.setOnClickListener(v -> toggleInfoVisibility());
        layoutInfoContainer.setOnClickListener(v -> toggleInfoVisibility());
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        fetchApodData(formatDate(calendar));

        return view;
    }

    private void toggleInfoVisibility() {
        if (isInfoExpanded) {
            layoutExpandable.setVisibility(View.GONE);
            btnToggleInfo.setImageResource(android.R.drawable.arrow_up_float); // Panah ke atas
        } else {
            layoutExpandable.setVisibility(View.VISIBLE);
            btnToggleInfo.setImageResource(android.R.drawable.arrow_down_float); // Panah ke bawah
        }
        isInfoExpanded = !isInfoExpanded;
    }

    private void startKenBurnsAnimation() {
        imageApod.setScaleX(1f);
        imageApod.setScaleY(1f);

        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f, 1.25f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f, 1.25f);

        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(imageApod, pvhX, pvhY);
        animator.setDuration(12000); // 12 detik
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
    }

    private void typeWriterEffect(String text) {
        textExplanation.setText("");
        typingHandler.removeCallbacksAndMessages(null);

        if (text == null) return;

        final int delay = 15;
        final char[] characters = text.toCharArray();
        final int length = characters.length;

        typingRunnable = new Runnable() {
            int i = 0;
            @Override
            public void run() {
                if (i < length) {
                    textExplanation.append(String.valueOf(characters[i]));
                    i++;
                    typingHandler.postDelayed(this, delay);
                }
            }
        };
        typingHandler.postDelayed(typingRunnable, delay);
    }

    private void fetchApodData(String date) {
        progressBar.setVisibility(View.VISIBLE);
        btnAskAi.setVisibility(View.GONE);
        typingHandler.removeCallbacksAndMessages(null);
        textExplanation.setText("Menghubungi satelit NASA...");

        NasaApiService apiService = ApiClient.getClient().create(NasaApiService.class);
        Call<ApodResponse> call = apiService.getApod(API_KEY, date);

        call.enqueue(new Callback<ApodResponse>() {
            @Override
            public void onResponse(Call<ApodResponse> call, Response<ApodResponse> response) {
                if (getContext() == null || !isAdded()) return;
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    currentApodData = response.body();

                    textTitle.setText(currentApodData.getTitle());
                    textDate.setText(date);
                    typeWriterEffect(currentApodData.getExplanation());
                    btnAskAi.setVisibility(View.VISIBLE);

                    if ("image".equals(currentApodData.getMediaType())) {
                        Glide.with(getContext()).load(currentApodData.getUrl()).into(imageApod);
                        startKenBurnsAnimation();
                    } else {
                        imageApod.setImageResource(R.drawable.ic_video);
                        Toast.makeText(getContext(), "Hari ini Video! Klik untuk putar.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    textExplanation.setText("Gagal mengambil data. Server NASA sibuk.");
                }
            }

            @Override
            public void onFailure(Call<ApodResponse> call, Throwable t) {
                if (getContext() == null || !isAdded()) return;
                progressBar.setVisibility(View.GONE);
                textExplanation.setText("Koneksi Error: " + t.getMessage());
            }
        });
    }

    private void explainApodWithAi() {
        if (currentApodData == null) return;

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_apod_ai, null);
        bottomSheetDialog.setContentView(sheetView);

        TextView textExplanationSheet = sheetView.findViewById(R.id.text_sheet_explanation);
        ProgressBar loadingSheet = sheetView.findViewById(R.id.progress_sheet);
        Button btnOpenOriginal = sheetView.findViewById(R.id.btn_open_original);

        loadingSheet.setVisibility(View.VISIBLE);
        textExplanationSheet.setText("AstroBot sedang membaca foto ini...");

        btnOpenOriginal.setOnClickListener(v -> openOriginalContent());
        bottomSheetDialog.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.a4f.co/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OpenAiApiService apiService = retrofit.create(OpenAiApiService.class);

        String userPrompt = "Jelaskan foto astronomi ini untuk anak SD dengan bahasa Indonesia yang seru.\n" +
                "Judul: " + currentApodData.getTitle() + "\n" +
                "Deskripsi: " + currentApodData.getExplanation();

        OpenAiRequest request = new OpenAiRequest("Kamu astronot ramah.", userPrompt);

        apiService.getChatCompletion(request).enqueue(new Callback<OpenAiResponse>() {
            @Override
            public void onResponse(Call<OpenAiResponse> call, Response<OpenAiResponse> response) {
                if (getContext() == null) return;
                loadingSheet.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    textExplanationSheet.setText(response.body().choices.get(0).message.content);
                } else {
                    textExplanationSheet.setText("Maaf, AI sedang sibuk.");
                }
            }
            @Override
            public void onFailure(Call<OpenAiResponse> call, Throwable t) {
                if (getContext() == null) return;
                loadingSheet.setVisibility(View.GONE);
                textExplanationSheet.setText("Gagal koneksi internet.");
            }
        });
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

        Toast.makeText(getContext(), "Maksimal tanggal kemarin ya!", Toast.LENGTH_SHORT).show();
        datePickerDialog.show();
    }

    private String formatDate(Calendar cal) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return sdf.format(cal.getTime());
    }

    private void openOriginalContent() {
        if (currentApodData == null) return;
        String url = currentApodData.getHdUrl() != null ? currentApodData.getHdUrl() : currentApodData.getUrl();
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Tidak ada aplikasi untuk membuka link ini.", Toast.LENGTH_SHORT).show();
        }
    }
}