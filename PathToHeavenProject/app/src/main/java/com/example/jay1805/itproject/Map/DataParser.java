//This class extracts information regarding a path to be used/displayed by MapsActivity//

package com.example.jay1805.itproject.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser {
    private RouteData routeData;
    private HashMap<String,String> routeInformation;
    private ArrayList<HashMap> routeStepInformation;

    public DataParser(RouteData routeData) {
        this.routeData = routeData;
        this.routeInformation = new HashMap<>();
        this.routeStepInformation = new ArrayList<>();
    }

    public DataParser(){

    }

    //Extracting data about each place for NearbyPlaces.//
    private HashMap<String,String> getPlace(JSONObject googlePlaceJson){
        HashMap<String,String> googlePlaceMap = new HashMap<>();
        String placeName = "-NA-";
        String vicinity = "-NA-";
        String latitude = "";
        String longitude = "";
        String reference = "";
        try {
            if (!googlePlaceJson.isNull("name")) {

                placeName = googlePlaceJson.getString("name");
            }

            if (!googlePlaceJson.isNull("vicinity")){
                vicinity = googlePlaceJson.getString("vicinity");
            }

            latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");

            reference = googlePlaceJson.getString("reference");

            googlePlaceMap.put("place_name",placeName);
            googlePlaceMap.put("vicinity",vicinity);
            googlePlaceMap.put("lat",latitude);
            googlePlaceMap.put("lng",longitude);
            googlePlaceMap.put("reference",reference);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return googlePlaceMap;
    }

    //Extracting each place from a JSONARRAY of places.//
    private List<HashMap<String,String>> getPlaces(JSONArray jsonArray){
        int count = jsonArray.length();
        List<HashMap<String,String>> placesList = new ArrayList<>();
        HashMap<String,String> placeMap = null;

        for (int i = 0; i<count;i++){
            try {
                placeMap = getPlace((JSONObject) jsonArray.get(i));
                placesList.add(placeMap);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return placesList;
    }

    //Getting data about directions.//
    public List<HashMap<String,String>> parse (String jsonData){
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getPlaces(jsonArray);
    }

    //Getting all basic information about a route from directions response parsing into a Hashmap.//
    public String[] parseDirections(String jsonData){
        JSONArray jsonArray = null;
        JSONObject jsonObject;
        JSONObject jsonRoute;
        JSONObject jsonLegs;

        try {
            jsonObject = new JSONObject(jsonData);
            jsonRoute = jsonObject.getJSONArray("routes").getJSONObject(0);
            routeInformation.put("Summary", jsonRoute.getString("summary"));
            System.out.println(routeInformation.get("Summary"));
            jsonLegs = jsonRoute.getJSONArray("legs").getJSONObject(0);
            routeInformation.put("Distance", jsonLegs.getJSONObject("distance").getString("text"));
            routeInformation.put("Duration", jsonLegs.getJSONObject("duration").getString("text"));
            routeData.setRouteInformation(routeInformation);
            jsonArray = jsonLegs.getJSONArray("steps");
            return getPaths(jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    //Getting all the data required for an entire path/route.//
    public String[] getPaths(JSONArray jsonArray){
        int count = jsonArray.length();
        String[] polylines = new String[count];

        for (int i = 0;i<count;i++){
            try {
                polylines[i] = getPath(jsonArray.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        routeData.setStepInformation(routeStepInformation);
        return polylines;
    }

    //Extracting information regarding each leg/straight line of the route.//
    public String getPath(JSONObject jsonObject){
        String polyline = "";
        HashMap<String,String> stepInfo = new HashMap<>();
        try {
            stepInfo.put("Instructions",jsonObject.getString("html_instructions"));
            stepInfo.put("Distance",jsonObject.getJSONObject("distance").getString("text"));
            stepInfo.put("Duration",jsonObject.getJSONObject("duration").getString("text"));
            if (!jsonObject.isNull("maneuver")) {
                stepInfo.put("Maneuver", jsonObject.getString("maneuver"));
            }else{
                stepInfo.put("Maneuver","NULL");
            }
            polyline = jsonObject.getJSONObject("polyline").getString("points");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        routeStepInformation.add(stepInfo);
        return polyline;
    }

}
