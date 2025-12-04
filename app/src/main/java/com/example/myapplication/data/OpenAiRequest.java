package com.example.myapplication.data;

import java.util.ArrayList;
import java.util.List;

public class OpenAiRequest {

    public String model = "provider-5/gpt-4o-mini";
    // ---------------------------------------------------

    public List<Message> messages;

    public OpenAiRequest(String systemPrompt, String userPrompt) {
        this.messages = new ArrayList<>();
        this.messages.add(new Message("system", systemPrompt));
        this.messages.add(new Message("user", userPrompt));
    }

    public static class Message {
        public String role;
        public String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}