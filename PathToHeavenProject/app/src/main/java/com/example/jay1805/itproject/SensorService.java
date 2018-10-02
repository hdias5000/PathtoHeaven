package com.example.jay1805.itproject;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SensorService extends Service{

    Context appContext;
//    FirebaseDatabase database;
    private String sharingID;

    public SensorService(Context applicationContext) {
        super();
        appContext = applicationContext;
        Log.i("HERE", "here I am!");
    }
    public SensorService() {

    }

    private Location currentLocation;

    //    private static final String TAG = SensorService.class.getSimpleName();
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//    public int onStartCommand(Intent intent, int flags, int startId) {
//        super.onStartCommand(intent, flags, startId);
//        LocalBroadcastManager.getInstance(this).registerReceiver(
//                mMessageReceiver, new IntentFilter("SEND NUDES"));
//
//        currentLocation = null;
//        Log.d("LOCATION1","function call.");
//        getLocationUpdates();
//        return START_STICKY;
//    }
    @Override
    public void onCreate() {
        super.onCreate();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("SEND NUDES"));
        LocalBroadcastManager.getInstance(this).registerReceiver(
                startTracking, new IntentFilter("UPLOAD NUDES"));

        currentLocation = null;
        Log.d("LOCATION1","function call.");
        startFirebase();
        getLocationUpdates();

    }

    private BroadcastReceiver startTracking = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference().child("gps-sharing");
            DatabaseReference newRef = ref.push();
            sharingID = newRef.getKey();
            updateLocation();
        }
    };

    private void startFirebase(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("gps-sharing");
        DatabaseReference newRef = ref.push();
        sharingID = newRef.getKey();
//        updateLocation();
    }

    private void updateLocation(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("gps-sharing").child(sharingID);
        Map map = new HashMap<>();
        map.put("latitude", Double.toString(currentLocation.getLatitude()));

        map.put("longitude", Double.toString(currentLocation.getLongitude()));
        ref.updateChildren(map);
    }


    private void makeUseOfNewLocation(Location location){
        Log.d("LOCATION3","result");
        if (location != null) {
            Log.d("LOCATION","C'est bien.");
            currentLocation = location;
            sendMessageToActivity(currentLocation, "");
            updateLocation();
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            if (currentLocation != null) {
//                Log.d("BS","I don't believe it"+lastKnownLoc.getLongitude());
                sendMessageToActivity(currentLocation,"nudes");
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

    @SuppressLint("MissingPermission")
    public void getLocationUpdates(){
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

// Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                makeUseOfNewLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

// Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");
        Intent broadcastIntent = new Intent("HD.RestartSensor");
        sendBroadcast(broadcastIntent);
//        stoptimertask();
    }






    /////////////////////////////////////////////////////////////////////////////////////////////BS
    //    private TimerTask timerTask;
    //    long oldTime=0;
    //    public void startTimer() {
    //        //set a new Timer
    //        timer = new Timer();
    //
    //        //initialize the TimerTask's job
    //        initializeTimerTask();
    //
    //        //schedule the timer, to wake up every 1 second
//    private Timer timer;
    //        timer.schedule(timerTask, 1000, 1000); //
    //    }
    //    /**
    //     * it sets the timer to print the counter every x seconds
    //     */
    //
    //    public void initializeTimerTask() {
    //        timerTask = new TimerTask() {
    //            public void run() {
    //                Log.i("in timer", "in timer ++++  "+ (counter++));
    //            }
    //        };
    //    }
    //
    //    /**
    //     * not needed
    //     */
    //    public void stoptimertask() {
    //        //stop the timer, if it's not already null
    //        if (timer != null) {
    //            timer.cancel();
    //            timer = null;
    //        }
    //    @Nullable
    //    @Override
    //    public IBinder onBind(Intent intent) {
    //        return null;
    //    }
    //    }
}
