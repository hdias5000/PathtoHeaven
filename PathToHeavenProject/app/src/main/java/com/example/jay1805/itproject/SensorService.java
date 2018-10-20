package com.example.jay1805.itproject;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SensorService extends Service implements com.google.android.gms.location.LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private LocationRequest locationRequest;
    private Location lastSharedGPSLoc;
    private Location lastSharedForVolunteer;
    private GoogleApiClient client;
    private String sharingID;
    private boolean share = false;

    private String currentUserId;
    private DatabaseReference userRef;

    private Location currentLocation = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("SEND GPS"));
        LocalBroadcastManager.getInstance(this).registerReceiver(
                startTracking, new IntentFilter("UPLOAD GPS"));
        LocalBroadcastManager.getInstance(this).registerReceiver(
                stopTracking, new IntentFilter("STOP GPS"));
        lastSharedForVolunteer = null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 2. Create the GoogleApi object
        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        client.connect();

        return START_STICKY;
    }

    private BroadcastReceiver startTracking = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (currentLocation!=null){
                startFirebase();
            }else{
                makeToast("GPS is not activated!!\nPlease activate GPS and try again!");
            }
        }
    };

    private void makeToast(String toast){
        Intent intent = new Intent("Make Toast");
        intent.putExtra("message",toast);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private BroadcastReceiver stopTracking = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            removeSharingFromFirebase();
        }
    };

    private void removeSharingFromFirebase(){
        if (share){
            share = false;
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference().child("gps-sharing").child(sharingID);
            ref.removeValue();
        }
    }

    private void startFirebase(){
        lastSharedGPSLoc = null;
        removeSharingFromFirebase();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("gps-sharing");
        DatabaseReference newRef = ref.push();
        sharingID = newRef.getKey();
        Intent intent = new Intent("GPS ID");
        intent.putExtra("ID", sharingID);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        updateLocation();
        share = true;
    }

    private Location updateLocation(){
        if (lastSharedGPSLoc!=null){
            if (lastSharedGPSLoc.distanceTo(currentLocation)<5){
                return null;
            }
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("gps-sharing").child(sharingID);
        Map map = new HashMap<>();

        lastSharedGPSLoc = currentLocation;

        map.put("latitude", Double.toString(currentLocation.getLatitude()));

        map.put("longitude", Double.toString(currentLocation.getLongitude()));

        ref.updateChildren(map);
        return null;
    }

    private Location uploadLocation(){
        if (lastSharedForVolunteer!=null){
            if (lastSharedForVolunteer.distanceTo(currentLocation)<10){
                return null;
            }
        }

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("user").child(currentUserId);


        Map map = new HashMap<>();
        lastSharedForVolunteer = currentLocation;
        map.put("latitude", Double.toString(currentLocation.getLatitude()));
        map.put("longitude", Double.toString(currentLocation.getLongitude()));

        userRef.updateChildren(map);

        return null;
    }


    private void makeUseOfNewLocation(Location location){
        if (location != null) {
            currentLocation = location;
            uploadLocation();
            sendMessageToActivity(currentLocation, "");
            if (share) {
                updateLocation();
            }
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            if (currentLocation != null) {
                sendMessageToActivity(currentLocation,"gps");
            }
        }
    };

    private void sendMessageToActivity(Location l, String msg) {
        Intent intent = new Intent("GPSLocationUpdates");
        // You can also include some extra data.
        intent.putExtra("Status", msg);
        Bundle b = new Bundle();
        b.putParcelable("Location", l);
        intent.putExtra("Location", b);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent broadcastIntent = new Intent("HD.RestartSensor");
        sendBroadcast(broadcastIntent);
        if (client!=null){
            LocationServices.FusedLocationApi.removeLocationUpdates(client,  this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (currentLocation==null){
            makeUseOfNewLocation(location);
        } else if ((location.getLatitude() != currentLocation.getLatitude()) || (location.getLongitude() != currentLocation.getLongitude())){
            makeUseOfNewLocation(location);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // 3. Create the LocationRequest
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000); // in milliseconds
        locationRequest.setFastestInterval(1000); // in milliseconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    // 4. Register your listener
        LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}
