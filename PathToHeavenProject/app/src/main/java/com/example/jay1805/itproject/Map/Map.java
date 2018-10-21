//Implements all functionalities related to Google Maps.//

package com.example.jay1805.itproject.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

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
    }

    //Enables current location marker.//
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


    //Shows entire route in camera of map.//
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

    //Implemented for next iteration.//
    public void updateCameraBearing(float bearing) {
        if (mMap==null) throw new AssertionError("map was not created");

        Log.d("CameraMap","Bearings");

        CameraPosition camPos = CameraPosition
                .builder(
                        mMap.getCameraPosition() // current Camera
                )
                .bearing(bearing)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
    }


    //Zooms to marker.//
    public void zoomToLocation(LatLng location){
        Log.d("CameraMap","Zoom to marker.");
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 17.0f));
    }

    //Zooms into location for enroute.//
    public void currentLocationZoom(LatLng location){
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 20.0f));
    }

    //Adds marker to map.//
    public Marker addMarker(MarkerOptions markerOptions, LatLng latLng, boolean zoom){
        if (zoom){
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }
        return mMap.addMarker(markerOptions);
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
    }



    public Polyline addRoute(PolylineOptions options) {

        return mMap.addPolyline(options);
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

    //Allows user to clicks on volunteer and a timer starts for the volunteer to accept.//
    @Override
    public boolean onMarkerClick(Marker marker) {
        if (listOfVolunteers==null) throw new AssertionError("listOfVolunteers is null");

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

    //Initiates the volunteer connection.//
    private void updateRequestedChild(Marker m) {
        final Marker volunteer = m;
        FirebaseDatabase.getInstance().getReference().child("user").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childsnapshot : dataSnapshot.getChildren()) {
                    if(childsnapshot.getKey().equals(listOfVolunteers.get(volunteer))){
                        for(DataSnapshot volunteersnapshot : childsnapshot.getChildren()){
                            if(volunteersnapshot.getKey().equals("Requested"))
                            {
                                java.util.Map map = new HashMap<>();
                                final DatabaseReference userDB = FirebaseDatabase.getInstance().getReference().child("user").child(childsnapshot.getKey());
                                System.out.println("^^^^^^^^^^^^"+FirebaseDatabase.getInstance().getReference().child("user").child(childsnapshot.getKey()).toString());
                                map.put("Requested", "True");
                                map.put("ElderlyIDRequested", FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
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

