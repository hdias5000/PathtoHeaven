package com.example.jay1805.itproject.User;

public class UserObject {

    String name, phone, uid,notificationKey;

    public UserObject(String name, String phone, String uid, String myNotificationKey){
        this.name = name;
        this.phone = phone;
        this.uid = uid;
        this.notificationKey = myNotificationKey;
    }

    public String getNotificationKey() {
        return notificationKey;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public void setName(String name) {
        this.name = name;
    }
}
