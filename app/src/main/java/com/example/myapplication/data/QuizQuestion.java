package com.example.myapplication.data;

import com.google.gson.annotations.SerializedName;

public class QuizQuestion {
    @SerializedName("question")
    public String question;

    @SerializedName("option_a")
    public String optionA;

    @SerializedName("option_b")
    public String optionB;

    // Kita tidak butuh target_a/target_b di sini lagi,
    // karena logika penentuan fitur ada di Java nanti.
}