package com.SOEN342.railway_network_system.database;

import java.util.*;
import com.SOEN342.railway_network_system.model.Route;

public class RoutesDB {
    private List<Route> routes = new ArrayList<>();

    //add route to rotes database
    public void addRoute(Route route){
        routes.add(route);
    }

    //return all routes
    public List<Route> getAllRoutes(){
        return routes;
    }

    //return routes that match search criteria 
    public List<Route> searchRoutes(Map<String, String> criteria){
        return new ArrayList<>(); 
    }

    //find routes with connections
    public List<Route> findIndirectRoutes(String departureCity, String arrivalCity, int numStops){
        return new ArrayList<>();
    }


}
