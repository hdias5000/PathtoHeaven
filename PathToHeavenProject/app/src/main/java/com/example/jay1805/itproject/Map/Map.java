package com.example.jay1805.itproject.Map;

import android.annotation.SuppressLint;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class Map implements GoogleMap.OnMarkerDragListener, GoogleMap.OnMarkerClickListener {
    private GoogleMap mMap;
    private Marker currentLocationMarker;


    public Map(GoogleMap googleMap) {
        mMap = googleMap;
        enableMyLocation();

        mMap.setOnMarkerClickListener(this);
        mMap.setOnMarkerDragListener(this);
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
        mMap.animateCamera(CameraUpdateFactory.zoomBy(1));
    }

    public void addMarker(MarkerOptions markerOptions, LatLng latLng){
        mMap.addMarker(markerOptions);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
    }

    public void addRoute(PolylineOptions options) {
        mMap.addPolyline(options);
    }

    public void clearMap(){
        mMap.clear();
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
        return false;
    }


}
