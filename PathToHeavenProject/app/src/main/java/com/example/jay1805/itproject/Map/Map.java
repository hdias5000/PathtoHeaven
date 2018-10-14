package com.example.jay1805.itproject.Map;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

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
                    marker.showInfoWindow();
                    return true;
                }
            }
        }
        return false;
    }


}
