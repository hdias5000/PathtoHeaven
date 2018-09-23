package com.example.jay1805.itproject.Map;

import android.location.Location;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class CurrentLocation {
    private Location lastLocation;
    private Map mMap;

    public CurrentLocation(Map mMap) {
        this.mMap = mMap;
    }


    public void changeCurrentLocation(Location location) {
        lastLocation = location;
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        LatLng latLng = new LatLng(latitude, longitude);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

        mMap.changeCurrentLocationMarker(markerOptions, latLng);
    }

    public double getLatitude(){
        return lastLocation.getLatitude();
    }

    public double getLongitude(){
        return lastLocation.getLongitude();
    }
}
