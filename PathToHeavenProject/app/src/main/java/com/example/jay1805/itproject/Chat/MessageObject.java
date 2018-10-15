package com.example.jay1805.itproject.Chat;

import java.util.ArrayList;

public class MessageObject {

    Boolean isGPSShared;
    String messageId, message, senderId, senderUid, receiverUid;
    ArrayList<String> mediaUrlList;

    public MessageObject(String messageId, String senderId, String senderUid, String receiverUid, String message, ArrayList<String> mediaUrlList, Boolean isGPSShared) {
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.messageId = messageId;
        this.senderId = senderId;
        this.message = message;
        this.mediaUrlList = mediaUrlList;
        this.isGPSShared = isGPSShared;
    }

    public String getReceiverUid() {
        return receiverUid;
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

    public Boolean getGPSShared() {
        return isGPSShared;
    }
}
