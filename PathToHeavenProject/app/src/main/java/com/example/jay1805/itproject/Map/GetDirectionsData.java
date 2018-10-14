package com.example.jay1805.itproject.Map;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GetDirectionsData extends AsyncTask<Object,String,String> {

    Map mMap;
    String url;
    String googleDirectionsData;

    String duration,distance;

    LatLng latLng;
    private List<Polyline> route;

    @Override
    protected String doInBackground(Object... objects) {
        mMap = (Map) objects[0];
        url = (String) objects[1];
        latLng = (LatLng) objects[2];

        DownloadURL downloadURL = new DownloadURL();
        try {
            googleDirectionsData = downloadURL.readURL(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googleDirectionsData;
    }

    @Override
    protected void onPostExecute(String s) {
        route = null;
        String[] directionsList;
        DataParser parser = new DataParser();
        Log.d("URL",s);
        System.out.println(s);
        route = new ArrayList<>();

        directionsList = parser.parseDirections(s);
        if (directionsList != null) {
            displayDirection(directionsList);
        }
//        }else {
//            MapsActivity.makeToast("Route Not Found");
//        }


//
//        if (directionsList!=null){
//
//            duration = directionsList.get("duration");
//            distance = directionsList.get("distance");
//            address = directionsList.get("address");
//
//        }
//        mMap.clear();
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(latLng);
//        markerOptions.draggable(true);
//        markerOptions.title(address);
//        markerOptions.snippet("Duration = "+duration +"; "+"Distance = "+distance);
//
//        mMap.addMarker(markerOptions);

    }

    public void displayDirection(String[] directionsList){
        int count = directionsList.length;
        for (int i=0;i<count;i++) {
            PolylineOptions options = new PolylineOptions();
            options.color(Color.RED);
            options.width(10);
            options.addAll(PolyUtil.decode(directionsList[i]));

            route.add(mMap.addRoute(options));
        }
    }

    public List<Polyline> getRoute() {
        return route;
    }
}
