package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.api.OpenAiApiService;
import com.example.myapplication.data.OpenAiRequest;
import com.example.myapplication.data.OpenAiResponse;
import com.example.myapplication.data.QuizQuestion;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ExploreQuizActivity extends AppCompatActivity {

    private TextView textQuestion, textFeedback, textTitleResult;
    private Button btnOptionA, btnOptionB, btnContinue;
    private LinearLayout layoutResult;
    private ProgressBar progressBar;

    private List<QuizQuestion> questionList = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int scoreTypeA = 0;
    private int scoreTypeB = 0;
    private String finalTargetFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_quiz);
        textQuestion = findViewById(R.id.text_question);
        textFeedback = findViewById(R.id.text_feedback);
        btnOptionA = findViewById(R.id.btn_option_a);
        btnOptionB = findViewById(R.id.btn_option_b);
        btnContinue = findViewById(R.id.btn_continue);
        layoutResult = findViewById(R.id.layout_result);
        progressBar = findViewById(R.id.quiz_progress);

        fetchQuizFromChatGPT();
    }

    private void fetchQuizFromChatGPT() {
        textQuestion.setText("Menghubungi Markas Pusat...");
        btnOptionA.setVisibility(View.GONE);
        btnOptionB.setVisibility(View.GONE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.a4f.co/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        OpenAiApiService apiService = retrofit.create(OpenAiApiService.class);

        String systemPrompt = "Kamu adalah Astronot senior yang ramah. Tugasmu adalah mewawancarai calon kadet cilik (anak SD).";

        String userPrompt = "Buatlah daftar 5 pertanyaan kuis psikologi seru untuk menentukan 'Gaya Penjelajah' anak." +
                "\nFormat pertanyaan harus imajinatif tentang luar angkasa." +
                "\nPilihan A harus mencerminkan anak yang suka melihat keindahan, foto, warna, dan planet (Tipe Visual)." +
                "\nPilihan B harus mencerminkan anak yang suka aksi, bahaya, asteroid, dan kecepatan (Tipe Petualang)." +
                "\nOutput WAJIB berupa JSON ARRAY murni tanpa markdown:" +
                "\n[" +
                "\n  {\"question\": \"Pertanyaan 1...\", \"option_a\": \"Jawaban Visual...\", \"option_b\": \"Jawaban Aksi...\"}," +
                "\n  ..." +
                "\n]";

        OpenAiRequest request = new OpenAiRequest(systemPrompt, userPrompt);

        apiService.getChatCompletion(request).enqueue(new Callback<OpenAiResponse>() {
            @Override
            public void onResponse(Call<OpenAiResponse> call, Response<OpenAiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String jsonContent = response.body().choices.get(0).message.content;
                        // Bersihkan Markdown
                        jsonContent = jsonContent.replace("```json", "").replace("```", "").trim();

                        // Parsing JSON Array ke List
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<QuizQuestion>>(){}.getType();
                        List<QuizQuestion> quizzes = gson.fromJson(jsonContent, listType);

                        startQuiz(quizzes);

                    } catch (Exception e) {
                        Log.e("QUIZ_ERROR", "Parsing gagal: " + e.getMessage());
                        startFallbackQuiz();
                    }
                } else {
                    startFallbackQuiz();
                }
            }

            @Override
            public void onFailure(Call<OpenAiResponse> call, Throwable t) {
                startFallbackQuiz();
            }
        });
    }

    private void startFallbackQuiz() {
        List<QuizQuestion> dummyList = new ArrayList<>();

        QuizQuestion q1 = new QuizQuestion();
        q1.question = "Jika kamu punya roket, kamu mau pergi ke mana?";
        q1.optionA = "Ke Nebula yang warna-warni";
        q1.optionB = "Mengejar Komet yang cepat";
        dummyList.add(q1);

        QuizQuestion q2 = new QuizQuestion();
        q2.question = "Apa yang ingin kamu bawa ke luar angkasa?";
        q2.optionA = "Kamera untuk foto";
        q2.optionB = "Laser untuk hancurkan batu";
        dummyList.add(q2);

        QuizQuestion q3 = new QuizQuestion();
        q3.question = "Alien seperti apa yang ingin kamu temui?";
        q3.optionA = "Alien yang cantik bersinar";
        q3.optionB = "Alien yang kuat dan cepat";
        dummyList.add(q3);

        startQuiz(dummyList);
    }

    private void startQuiz(List<QuizQuestion> questions) {
        this.questionList = questions;
        this.currentQuestionIndex = 0;
        this.scoreTypeA = 0;
        this.scoreTypeB = 0;

        btnOptionA.setVisibility(View.VISIBLE);
        btnOptionB.setVisibility(View.VISIBLE);

        showNextQuestion();
    }

    private void showNextQuestion() {
        if (currentQuestionIndex >= questionList.size()) {
            showFinalResult();
            return;
        }

        int progress = (int) (((float) currentQuestionIndex / questionList.size()) * 100);
        progressBar.setProgress(progress);

        QuizQuestion current = questionList.get(currentQuestionIndex);

        textQuestion.setText(current.question);
        btnOptionA.setText(current.optionA);
        btnOptionB.setText(current.optionB);

        btnOptionA.setOnClickListener(v -> {
            scoreTypeA++; // Poin untuk Visual
            nextStep();
        });

        btnOptionB.setOnClickListener(v -> {
            scoreTypeB++; // Poin untuk Aksi
            nextStep();
        });
    }

    private void nextStep() {
        currentQuestionIndex++;
        showNextQuestion();
    }

    private void showFinalResult() {
        textQuestion.setVisibility(View.GONE);
        btnOptionA.setVisibility(View.GONE);
        btnOptionB.setVisibility(View.GONE);
        progressBar.setProgress(100);
        layoutResult.setVisibility(View.VISIBLE);
        layoutResult.setAlpha(0f);
        layoutResult.animate().alpha(1f).setDuration(500).start();

        String titleResult;
        String descResult;

        if (scoreTypeA > scoreTypeB) {
            titleResult = "Kamu Penjelajah Artistik! 🎨✨";
            descResult = "Kamu suka keindahan alam semesta. Misi pertamamu adalah melihat foto-foto menakjubkan di galeri harian NASA.";
            finalTargetFragment = "nav_apod";
            btnContinue.setText("Buka Galeri APOD 🚀");
        } else {
            titleResult = "Kamu Penjaga Bumi! 🛡️☄️";
            descResult = "Kamu berani dan suka tantangan. Misi pertamamu adalah memantau asteroid yang mendekati planet kita!";
            finalTargetFragment = "nav_neo";
            btnContinue.setText("Pantau Asteroid 🔭");
        }

        textFeedback.setText(titleResult + "\n\n" + descResult);

        btnContinue.setOnClickListener(v -> {
            Intent intent = new Intent(ExploreQuizActivity.this, MainActivity.class);
            intent.putExtra("TARGET_FRAGMENT", finalTargetFragment);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}