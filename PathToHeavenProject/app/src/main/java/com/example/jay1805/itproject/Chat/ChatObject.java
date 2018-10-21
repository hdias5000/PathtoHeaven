package com.example.jay1805.itproject.Chat;

/**
 * This object stores the chat ida of any particular user of the application
 */
public class ChatObject {

    private String chatId;

    public ChatObject(String chatId) {
        this.chatId = chatId;
    }

    public String getChatId() {
        return chatId;
    }
}
