package com.example.jay1805.itproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class takes care of any notification opened. There are two types of notifications.
 * When the message notification is opened, they are redirected to the particular chat activity.
 * When the help notification is opened they are connected with the elderly person through maps.
 */
class NotificationIsOpened implements OneSignal.NotificationOpenedHandler {
    private final Context context;

    public NotificationIsOpened(Context applicationContext) {
        this.context = applicationContext;
    }

    @Override
    public void notificationOpened(OSNotificationOpenResult result) {
        OSNotificationAction.ActionType actionType = result.action.type;
        JSONObject data = result.notification.payload.additionalData;

        String type = "";

        try {
            type = data.getString("type");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        switch (type){
            case "message":
                String chatID = null;

                try {
                    chatID = data.getString("chatID");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(chatID != null) {
                    Intent intent = new Intent(context, ChatActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("chatID", chatID);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }

                String customKey;

                if (data != null) {
                    customKey = data.optString("customkey", null);
                    if (customKey != null)
                        Log.i("OneSignalExample", "customkey set with value: " + customKey);
                }

                if (actionType == OSNotificationAction.ActionType.ActionTaken)
                    Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);

                break;

            case "help":
                String shareID = "";
                String uid = "";
                try {
                    shareID = data.getString("shareID");
                    uid = data.getString("userID");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(context, MapsActivity.class);
                intent.putExtra("Share ID", shareID);
                intent.putExtra("userID",uid);
                context.startActivity(intent);
                break;
        }

    }
}
