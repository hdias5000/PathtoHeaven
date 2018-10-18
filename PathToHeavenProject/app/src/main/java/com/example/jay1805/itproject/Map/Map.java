package com.example.jay1805.itproject.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import android.view.View;
import android.widget.Toast;


import com.example.jay1805.itproject.ChatActivity;
import com.example.jay1805.itproject.MapsActivity;
import com.example.jay1805.itproject.MyProfileActivity;
import com.example.jay1805.itproject.SinchService;
import com.example.jay1805.itproject.TimerActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class Map implements GoogleMap.OnMarkerDragListener, GoogleMap.OnMarkerClickListener {
    private final Context context;
    private GoogleMap mMap;
    private Marker currentLocationMarker;
    private HashMap<Marker, String> listOfVolunteers;



    public Map(GoogleMap googleMap, Context applicationContext) {
        mMap = googleMap;
        this.context = applicationContext;
        enableMyLocation();

        mMap.setOnMarkerClickListener(this);
//        mMap.setOnMarkerDragListener(this);
    }

    @SuppressLint("MissingPermission")  // checked before object is created
    public void enableMyLocation(){
        mMap.setMyLocationEnabled(true);
    }


    public void changeCurrentLocationMarker(MarkerOptions markerOptions, LatLng latLng){
        if (currentLocationMarker != null) {
            currentLocationMarker.remove();
        }
        currentLocationMarker = mMap.addMarker(markerOptions);

    }

    public void showLocation(LatLng pos){
        mMap.animateCamera(CameraUpdateFactory.newLatLng(pos));
    }



    public void showEntireRoute(LatLng loc1, LatLng loc2){
        Double loc1Lat = loc1.latitude;
        Double loc1Lon = loc1.longitude;
        Double loc2Lat = loc2.latitude;
        Double loc2Lon = loc2.longitude;

        LatLng northEast = new LatLng(Math.max(loc1Lat,loc2Lat),Math.max(loc1Lon,loc2Lon));
        LatLng southWest = new LatLng(Math.min(loc1Lat,loc2Lat),Math.min(loc1Lon,loc2Lon));
        LatLngBounds bounds = new LatLngBounds(southWest,northEast);
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,100));
    }

    public void updateCameraBearing(float bearing) {
        if ( mMap == null) return;
        Log.d("CameraMap","work pwleeeeeeease");

        CameraPosition camPos = CameraPosition
                .builder(
                        mMap.getCameraPosition() // current Camera
                )
                .bearing(bearing)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
    }



    public void zoomToLocation(LatLng location){

        Log.d("CameraMap","work pwease");
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
//        mMap.animateCamera(CameraUpdateFactory.zoomBy(1));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 17.0f));
    }

    public void currentLocationZoom(LatLng location){
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 20.0f));
    }

    public Marker addMarker(MarkerOptions markerOptions, LatLng latLng, boolean zoom){
        if (zoom){

            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }
        return mMap.addMarker(markerOptions);
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
    }



    public void removeMarker(Marker marker){
        marker.remove();
    }

    public Polyline addRoute(PolylineOptions options) {

        return mMap.addPolyline(options);
    }

    public void removeRoute(List<Polyline> route){
        int count = route.size();
        for (int i=0;i<count;i++) {
            route.get(i).remove();
        }
    }

    public void clearMap(){
        mMap.clear();
    }

    public void setListOfVolunteers(HashMap volunteers){
        this.listOfVolunteers = volunteers;
    }






    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        if(listOfVolunteers!=null){
            for(Marker m:listOfVolunteers.keySet()){
                if (m.equals(marker)){
                    // set "Requested" attribute for the volunteer as "True"
                    //Start timer intent here
                    updateRequestedChild(m);
                    context.startActivity(new Intent(context, TimerActivity.class));
                    //updateRequestedChild(m);
                    marker.showInfoWindow();

                    return true;
                }
            }
        }
        return false;
    }

    private void updateRequestedChild(Marker m) {
        final Marker volunteer = m;
        FirebaseDatabase.getInstance().getReference().child("user").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childsnapshot : dataSnapshot.getChildren()) {
                    System.out.println("USER ID:  "+childsnapshot.getKey());
                    if(childsnapshot.getKey().equals(listOfVolunteers.get(volunteer))){
                        for(DataSnapshot volunteersnapshot : childsnapshot.getChildren()){

                            System.out.println("Volunteer keys: "+volunteersnapshot.getKey());
                            if(volunteersnapshot.getKey().equals("Requested"))
                            {
                                System.out.println("@@@@@@@@@@@ inside requested");
                                java.util.Map map = new HashMap<>();
                                final DatabaseReference userDB = FirebaseDatabase.getInstance().getReference().child("user").child(childsnapshot.getKey());
                                System.out.println("^^^^^^^^^^^^"+FirebaseDatabase.getInstance().getReference().child("user").child(childsnapshot.getKey()).toString());
                                map.put("Requested", "True");
                                map.put("ElderlyIDRequested", FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
                                System.out.println("ELDERLY WOO############ :"+childsnapshot.getKey().toString());
                                userDB.updateChildren(map);

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
    }

