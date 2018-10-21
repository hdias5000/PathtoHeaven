//This class stores and returns data regarding the current location.//

package com.example.jay1805.itproject.Map;

import android.location.Location;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class CurrentLocation {


    private Location lastLocation;
    private Map mMap;
    private boolean showCurrentLocation;

    public CurrentLocation(Map mMap) {

        this.mMap = mMap;
        this.showCurrentLocation = true;
        this.lastLocation = null;
    }


    public void changeCurrentLocation(Location location) {
        if (location!=null) {
            lastLocation = location;
        }

        if (showCurrentLocation) {
            double latitude = lastLocation.getLatitude();
            double longitude = lastLocation.getLongitude();

            LatLng latLng = new LatLng(latitude, longitude);

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Location");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

            mMap.changeCurrentLocationMarker(markerOptions, latLng);
        }


    }

    public void hideCurrentLocation(){
        showCurrentLocation = false;
    }

    public float getBearing(){
        return lastLocation.getBearing();
    }

    public void showCurrentLocation(){
        showCurrentLocation = true;
        changeCurrentLocation(null);
    }

    public LatLng getCurrentLocation() {
        if (lastLocation!=null){

            return new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
        }
        return null;
    }

    public double getLatitude(){
        if (lastLocation!=null){

            return lastLocation.getLatitude();
        }
        return Double.parseDouble(null);
    }

    public double getLongitude(){
        if (lastLocation!=null){

            return lastLocation.getLongitude();
        }
        return Double.parseDouble(null);
    }
}
