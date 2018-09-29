package com.example.jay1805.itproject;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jay1805.itproject.Map.CurrentLocation;
import com.example.jay1805.itproject.Map.GetDirectionsData;
import com.example.jay1805.itproject.Map.GetNearbyPlacesData;
import com.example.jay1805.itproject.Map.Map;
import com.example.jay1805.itproject.Map.URLCreator;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private CurrentLocation currentLocation;
    private Map map;
    private Location lastKnownLoc;

    private URLCreator urlCreator;

    LatLng currentDestination;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        urlCreator = new URLCreator();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("GPSLocationUpdates"));
        lastKnownLoc = null;

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("Status");
            Bundle b = intent.getBundleExtra("Location");
            lastKnownLoc = (Location) b.getParcelable("Location");
            if (lastKnownLoc != null) {
                Log.d("BS","I don't believe it"+lastKnownLoc.getLongitude());
                currentLocation.changeCurrentLocation(lastKnownLoc);
            }
        }
    };

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map = new Map(googleMap);
            currentLocation = new CurrentLocation(map);
        }


    }

    public void onClick(View v) {
        double latitude = currentLocation.getLatitude();
        double longitude = currentLocation.getLongitude();

        switch (v.getId()) {
            case R.id.B_SEARCH:
                map.clearMap();
                EditText tf_location = (EditText) findViewById(R.id.TF_LOCATION);
                String location = tf_location.getText().toString();
                List<Address> addressList = null;
                MarkerOptions mo = new MarkerOptions();

                if (!location.equals("")) {
                    Geocoder geocoder = new Geocoder(this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 5);
                        for (int i = 0; i < addressList.size(); i++) {
                            Address myAddress = addressList.get(i);
                            currentDestination = new LatLng(myAddress.getLatitude(), myAddress.getLongitude());
                            mo.position(currentDestination);
                            mo.title("Your search results");

                            map.addMarker(mo,currentDestination);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                break;
            case R.id.B_Hospital:
                showNearbyPlaces("hospital",latitude,longitude);
                break;

            case R.id.B_Restaurant:
                showNearbyPlaces("restaurant",latitude,longitude);
                break;

            case R.id.B_School:
                showNearbyPlaces("school",latitude,longitude);
                break;

            case R.id.B_to:
//                mMap.clear();
//
//                MarkerOptions markerOptions = new MarkerOptions();
//                markerOptions.position(new LatLng(endLatitude,endLongitude));
//                markerOptions.title("Destination");
//
//
//                float results[] = new float[10];
//                Location.distanceBetween(latitude,longitude,endLatitude,endLongitude,results);
//

//                markerOptions.snippet("Distance = " +results[0]);
//                mMap.addMarker(markerOptions);
                Object dataTransfer[] = new Object[3];
                String url = urlCreator.getDirectionsUrl(latitude, longitude, currentDestination.latitude, currentDestination.longitude);
                Log.d("LOL",url);
                GetDirectionsData getDirectionsData = new GetDirectionsData();
                dataTransfer[0] = map;
                dataTransfer[1] = url;
                dataTransfer[2] = currentDestination;
                getDirectionsData.execute(dataTransfer);
                break;

//            case R.id.LATDOWN:
//                latitude--;
//                changeCurrentLocation(latitude, longitude);
//                break;
//            case R.id.LATUP:
//                latitude++;
//                changeCurrentLocation(latitude, longitude);
//                break;
//            case R.id.LNGDOWN:
//                longitude--;
//                changeCurrentLocation(latitude, longitude);
//                break;
//            case R.id.LNGUP:
//                longitude++;
//                changeCurrentLocation(latitude, longitude);
//                break;
        }

    }

    private void showNearbyPlaces(String tag, double latitude, double longitude){
        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
        Object dataTransfer[] = new Object[2];
        map.clearMap();
        String url = urlCreator.getUrl(latitude, longitude, tag);
        dataTransfer[0] = map;
        dataTransfer[1] = url;

        getNearbyPlacesData.execute(dataTransfer);
        makeToast("Showing Nearby "+tag);

    }

    public void makeToast(String message){
        Toast.makeText(MapsActivity.this, message, Toast.LENGTH_LONG).show();
    }



}