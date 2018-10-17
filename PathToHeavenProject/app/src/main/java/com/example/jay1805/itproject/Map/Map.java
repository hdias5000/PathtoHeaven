package com.example.jay1805.itproject.Map;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
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
    private GoogleMap mMap;
    private Marker currentLocationMarker;
    private HashMap<Marker, String> listOfVolunteers;


    public Map(GoogleMap googleMap) {
        mMap = googleMap;
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

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        mMap.animateCamera(CameraUpdateFactory.zoomBy(1));
    }

    public Marker addMarker(MarkerOptions markerOptions, LatLng latLng){

        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
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
                    updateRequestedChild(m);
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

