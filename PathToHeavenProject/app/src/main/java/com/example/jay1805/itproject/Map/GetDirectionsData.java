//Used to display the route corresponding to the given URL.//

package com.example.jay1805.itproject.Map;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.example.jay1805.itproject.OnEventListener;
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
    RouteData routeData;

    LatLng latLng;
    private List<Polyline> route;
    private boolean success;
    private OnEventListener<String> mCallBack;
    private Exception mException;

    public GetDirectionsData(RouteData currentRouteData,OnEventListener callback) {
        this.routeData = currentRouteData;
        this.success = false;
        this.mCallBack = callback;
    }

    //Url request sent and response found in background of the execute.//
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

    //Called to execute from MapsActivity.//
    @Override
    protected void onPostExecute(String s) {
        route = null;
        String[] directionsList;
        DataParser parser = new DataParser(routeData);
        Log.d("URL",s);
        System.out.println(s);
        route = new ArrayList<>();

        directionsList = parser.parseDirections(s);
        try {
            displayDirection(directionsList);
        } catch (Exception e){
            mException = e;
        }

        if (mException==null){
            mCallBack.onSuccess(s);
        }else{
            mCallBack.onFailure(mException);
        }
    }


    //Displays the route on the map.//
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

    //Return the displayed route.//
    public List<Polyline> getRoute() {
        return route;
    }
}
