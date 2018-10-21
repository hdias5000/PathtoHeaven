//This class sets all the data for a particular route that could be accessed by MapsActivity.//
package com.example.jay1805.itproject.Map;

import java.util.ArrayList;
import java.util.HashMap;

public class RouteData {
    private HashMap routeInformation;
    private ArrayList stepInformation;

    public ArrayList getStepInformation() {
        return stepInformation;
    }

    public HashMap<String, String> getRouteInformation() {
        return routeInformation;
    }

    public void setRouteInformation(HashMap routeInformation) {
        this.routeInformation = routeInformation;
    }

    public void setStepInformation(ArrayList stepInformation) {
        this.stepInformation = stepInformation;
    }
}
