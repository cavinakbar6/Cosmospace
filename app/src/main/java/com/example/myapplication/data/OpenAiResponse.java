package com.example.myapplication.data;

import java.util.List;

public class OpenAiResponse {
    public List<Choice> choices;

    public static class Choice {
        public Message message;
    }

    public static class Message {
        public String content; // Ini isinya JSON kuis nanti
    }
}