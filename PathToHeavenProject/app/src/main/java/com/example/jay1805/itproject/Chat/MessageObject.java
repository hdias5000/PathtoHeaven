package com.example.jay1805.itproject.Chat;

import java.util.ArrayList;

public class MessageObject {

    String messageId, message, senderId, senderUid;
    ArrayList<String> mediaUrlList;

    public MessageObject(String messageId, String senderId, String senderUid, String message, ArrayList<String> mediaUrlList) {
        this.senderUid = senderUid;
        this.messageId = messageId;
        this.senderId = senderId;
        this.message = message;
        this.mediaUrlList = mediaUrlList;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public ArrayList<String> getMediaUrlList() {
        return mediaUrlList;
    }

    public String getMessage() {
        return message;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getSenderId() {
        return senderId;
    }
}
