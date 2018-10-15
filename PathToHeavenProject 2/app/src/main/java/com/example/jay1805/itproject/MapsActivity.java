package com.example.jay1805.itproject;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.jay1805.itproject.Map.CurrentLocation;
import com.example.jay1805.itproject.Map.GetDirectionsData;
import com.example.jay1805.itproject.Map.GetNearbyPlacesData;
import com.example.jay1805.itproject.Map.Map;
import com.example.jay1805.itproject.Map.URLCreator;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private CurrentLocation currentLocation;
    private Map map;
    private Location lastKnownLoc;
    private double volLat=0;
    private double volLongi=0;
    private String currentVolunteerName;

    private PlaceAutocompleteFragment placeAutocompleteFragment;
    private URLCreator urlCreator;

    LatLng currentDestination;
    Marker marker;
    Marker markerOfElderly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        placeAutocompleteFragment = (PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        placeAutocompleteFragment.setFilter(new AutocompleteFilter.Builder().setCountry("AU").build());

        placeAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                final LatLng latLngLoc = place.getLatLng();
                MarkerOptions mo = new MarkerOptions();
                if(marker!=null){
                    marker.remove();
                }
                map.clearMap();
                currentDestination = latLngLoc;

                mo.position(currentDestination);
                mo.title("Your search results");
                mo.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                map.addMarker(mo,currentDestination);
            }

            @Override
            public void onError(Status status) {
                Toast.makeText(MapsActivity.this, ""+status.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        urlCreator = new URLCreator();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("GPSLocationUpdates"));
        lastKnownLoc = null;

        String shareID;
        Intent intent = getIntent();

        if (intent.hasExtra("Share ID") && intent.getExtras().containsKey("Share ID")) {

            shareID = intent.getExtras().getString("Share ID");
            System.out.println("Share ID is: " + shareID);
            Log.d("SHAREID", shareID);
            // Get a reference to our posts
//            if (FirebaseDatabase.getInstance().getReference().child("gps-sharing").child(shareID) != null) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("gps-sharing").child(shareID);
//                if ((ref.child("latitude") != null) && (ref.child("longitude") != null)){
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            double newLatitude = 0;
                            double newLongitude = 0;
                            for (DataSnapshot childSnapshot: dataSnapshot.getChildren()){
                                if (childSnapshot.getKey().toString().equals("latitude")){
                                    newLatitude = Double.parseDouble(childSnapshot.getValue().toString());
                                }
                                if (childSnapshot.getKey().toString().equals("longitude")){
                                    newLongitude = Double.parseDouble(childSnapshot.getValue().toString());
                                }
                            }

                            if (markerOfElderly!=null){
                                markerOfElderly.remove();
                            }

                            Log.d("Coord", "lat is: " +newLatitude);
                            Log.d("Coord", "long is: " +newLongitude);
                            LatLng latLng = new LatLng(newLatitude,newLongitude);
                            MarkerOptions mo = new MarkerOptions();
                            mo.position(latLng);
                            mo.title("Location of Elderly");
                            mo.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                            markerOfElderly = map.addMarker(mo,latLng);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            makeToast("Tracking has Stopped");
                        }
                    });
//                } else{
//                    makeToast("missing coordinates");
//                }
//            } else{
//                makeToast("String don't exist.");
//            }

        }


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
            askForCurrentLocation();
        }


    }

    public void onClick(View v) {
        double latitude = currentLocation.getLatitude();
        double longitude = currentLocation.getLongitude();


        switch (v.getId()) {

            case R.id.B_Hospital:
                showNearbyPlaces("hospital",latitude,longitude);
                break;

            case R.id.B_Restaurant:
                showNearbyPlaces("restaurant",latitude,longitude);
                break;

            case R.id.B_School:
                showNearbyPlaces("school",latitude,longitude);
                break;

            case R.id.B_Volunteer:
                PlaceVolunteerMarkerOnMap();
                break;

            case R.id.B_to:

                Object dataTransfer[] = new Object[3];
                String url = urlCreator.getDirectionsUrl(latitude, longitude, currentDestination.latitude, currentDestination.longitude);
                Log.d("LOL",url);
                GetDirectionsData getDirectionsData = new GetDirectionsData();
                dataTransfer[0] = map;
                dataTransfer[1] = url;
                dataTransfer[2] = currentDestination;
                getDirectionsData.execute(dataTransfer);
                sendMessageToActivity(url,currentDestination);
                break;

        }

    }

    private void PlaceVolunteerMarkerOnMap() {

        FirebaseDatabase.getInstance().getReference().child("user").addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for (DataSnapshot childsnapshot : dataSnapshot.getChildren()) {
                for(DataSnapshot volunteersnapshot : childsnapshot.getChildren())
                {

                    // getting volunteers' coordinates
                    if (volunteersnapshot.getKey().equals("User Type") && volunteersnapshot.getValue().toString().equals("Helper")) {
                        for(DataSnapshot volunteersnapshot2 : childsnapshot.getChildren())
                        {
                            if (volunteersnapshot2.getKey().equals("latitude")) {
                                volLat = Double.parseDouble(volunteersnapshot2.getValue().toString());
                            }
                            if (volunteersnapshot2.getKey().equals("longitude")) {
                                volLongi = Double.parseDouble(volunteersnapshot2.getValue().toString());

                            }
                            if (volunteersnapshot2.getKey().equals("name")) {
                                currentVolunteerName = volunteersnapshot2.getValue().toString();
                            }


                            if(( volLat!=0 && volLongi!=0 && currentVolunteerName!=""))
                            {
                                System.out.println("current vol: "+currentVolunteerName);
                                MarkerOptions mo = new MarkerOptions();
                                LatLng volLatLng = new LatLng(volLat,volLongi);

                                mo.position(volLatLng);
                                mo.title(currentVolunteerName);
                                mo.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                                //mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_person));

                                map.addMarker(mo,volLatLng);
                                currentVolunteerName = "";
                            }
                        }
                    }
                }

            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
    }

    private void sendMessageToActivity(String url, LatLng dest) {
        Intent intent = new Intent("New Route");
        // You can also include some extra data.
        intent.putExtra("url", url);
        Bundle b = new Bundle();
        b.putParcelable("dest", dest);
        intent.putExtra("dest", b);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
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


    private void askForCurrentLocation() {
        Intent intent = new Intent("SEND GPS");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


}