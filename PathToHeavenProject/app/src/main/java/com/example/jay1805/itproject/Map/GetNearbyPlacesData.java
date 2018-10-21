//Nearby places, getting data from the URL. Not added to menu due to UI complications.//

package com.example.jay1805.itproject.Map;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class GetNearbyPlacesData extends AsyncTask<Object,String,String> {
    String googlePlacesData;
    Map mMap;
    String url;

    //Executed in the background of the execute operation.//
    @Override
    protected String doInBackground(Object... objects) {
        mMap = (Map) objects[0];
        url = (String) objects[1];

        DownloadURL downloadURL = new DownloadURL();
        try {
            googlePlacesData = downloadURL.readURL(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googlePlacesData;
    }

    //Used to execute displaying the Nearby places from MapsActivity.//
    @Override
    protected void onPostExecute(String s) {
        List<HashMap<String,String>> nearbyPlaceList = null;
        DataParser dataParser = new DataParser();
        nearbyPlaceList = dataParser.parse(s);
        showNearbyPlaces(nearbyPlaceList);
        super.onPostExecute(s);
    }

    //SHOW THE LOCATIONS.//
    private void showNearbyPlaces(List<HashMap<String,String>> nearbyPlaceList){
        for (int i=0;i<nearbyPlaceList.size();i++){
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String,String> googlePlace = nearbyPlaceList.get(i);

            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");
            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));

            LatLng latLng = new LatLng(lat,lng);
            markerOptions.position(latLng);
            markerOptions.title(placeName+" : "+vicinity);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

            mMap.addMarker(markerOptions,latLng, true);
        }
    }
}
