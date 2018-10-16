package com.example.jay1805.itproject.Utilities;

import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

public class SendNotifications {
    public SendNotifications(String message, String heading, String notificationKey, String chatID) {

        System.out.println("ChatID is : " + chatID);

        try {
            JSONObject notificationContent = new JSONObject(
                    "{'contents':{'en':'" + message + "'},"+
                            "'include_player_ids':['" + notificationKey + "']," +
                            "'android_channel_id':'8f72db5e-f2dc-4bb1-b5c3-f81d42c181ac'," +
                            "'data':{'chatID':'" + chatID + "'}," +
                            "'headings':{'en': '" + heading + "'}}");
            OneSignal.postNotification(notificationContent, null);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
