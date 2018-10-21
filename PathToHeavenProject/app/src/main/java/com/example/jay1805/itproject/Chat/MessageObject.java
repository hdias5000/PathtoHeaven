package com.example.jay1805.itproject.Chat;

import java.util.ArrayList;

/**
 * The message object stores all the relevant information of any particular messages. Especially
 * the information that is also being stored int he database about the user.
 */
public class MessageObject {

    Boolean isGPSShared;
    String messageId, message, senderId, senderUid;
    ArrayList<String> mediaUrlList;

    public MessageObject(String messageId, String senderId, String senderUid, String message, ArrayList<String> mediaUrlList, Boolean isGPSShared) {
        this.senderUid = senderUid;
        this.messageId = messageId;
        this.senderId = senderId;
        this.message = message;
        this.mediaUrlList = mediaUrlList;
        this.isGPSShared = isGPSShared;
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
