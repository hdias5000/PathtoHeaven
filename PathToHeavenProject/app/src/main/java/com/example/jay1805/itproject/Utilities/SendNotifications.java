package com.example.jay1805.itproject.Utilities;

import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Defining JSON objects that create notifications for messages and help. This class also send the
 * notification to the person who has the notificationKey mentioned in the JSON object.
 */
public class SendNotifications {
    public SendNotifications(HashMap<String,String> nInformation) {
        String type = nInformation.get("type");
        String notificationKey = nInformation.get("notificationKey");
        String message,heading;
        switch (type){
            case "message":
                message = nInformation.get("message");
                heading = nInformation.get("heading");
                String chatID = nInformation.get("chatID");

                try {
                    JSONObject notificationContent = new JSONObject(
                            "{'contents':{'en':'" + message + "'},"+
                                    "'include_player_ids':['" + notificationKey + "']," +
                                    "'android_channel_id':'8f72db5e-f2dc-4bb1-b5c3-f81d42c181ac'," +
                                    "'data':{'chatID':'" + chatID + "', 'type':'" + "message" + "'}," +
                                    "'headings':{'en': '" + heading + "'}}");
                    OneSignal.postNotification(notificationContent, null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case "help":
                String shareID = nInformation.get("shareID");
                heading = new String(nInformation.get("name")+" needs your Help!!");
                message = nInformation.get("message");
                String uid = nInformation.get("userID");

                try {
                    JSONObject notificationContent = new JSONObject(
                            "{'contents':{'en':'" + message + "'},"+
                                    "'include_player_ids':['" + notificationKey + "']," +
                                    "'android_channel_id':'8f72db5e-f2dc-4bb1-b5c3-f81d42c181ac'," +
                                    "'data':{'shareID':'" + shareID + "', 'type':'" + "help" + "','userID':'" + uid + "'}," +
                                    "'headings':{'en': '" + heading + "'}}");
                    OneSignal.postNotification(notificationContent, null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;



        }





    }
}
