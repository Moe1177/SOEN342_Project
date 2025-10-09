package com.SOEN342.railway_network_system.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.SOEN342.railway_network_system.database.*;
import com.SOEN342.railway_network_system.model.*;

@Component
public class Console {
    private RoutesDB routesDB;
    private TrainsDB trainsDB;
    private TicketsDB ticektsDB; 

    //displays results of the user's search
    public void displayRoutes(List<Route> routes){

    }

    //gets the user input and maps the associated parameter to the user's input
    public Map<String, String> getUserInput(){
        return null;
    }

    public List<Route> sortRoutes(List<Route> routes, String sortBy){
        return routes;
    }

    



}
