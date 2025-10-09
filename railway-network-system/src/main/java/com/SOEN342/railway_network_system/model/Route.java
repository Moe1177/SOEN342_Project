package com.SOEN342.railway_network_system.model;

import java.util.Date;
import java.util.Map;


public class Route {
    private String routeID; 
    private String departureCity;
    private String arrivalCity; 
    private Date departureTime;
    private Date arrivalTime; 
    private String totalDuration; 

    //constructor
    public Route(String routeID, String departureCity, String arrivalCity, Date departureTime, Date arrivalTime) {
        this.routeID = routeID; 
        this.departureCity = departureCity; 
        this.arrivalCity = arrivalCity; 
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;        
    }

    //getters and setters
    public String getRouteID() {
        return routeID;
    }

    public void setRouteID(String routeID) {
        this.routeID = routeID;
    }

    public String getDepartureCity() {
        return departureCity;
    }

    public void setDepartureCity(String departureCity) {
        this.departureCity = departureCity;
    }

    public String getArrivalCity() {
        return arrivalCity;
    }

    public void setArrivalCity(String arrivalCity) {
        this.arrivalCity = arrivalCity;
    }

    public Date getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(String totalDuration) {
        this.totalDuration = totalDuration;
    }

    //calculate total duration of train ride 
    public void calculateDuration(){

    }

    //check to see if map matches user's criteria
    public boolean matchesCriteria(Map<String, String> criteria){
            return false;

    }

    //display route info 
    public String displayRouteInfo(){
        return "";
    }


}
