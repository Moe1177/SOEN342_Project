package com.SOEN342.railway_network_system.model;

public class Train {
    private String trainType; 
    private String daysOfOperation;

    //constructor
    public Train(String trainType, String daysOfOperation) {
        this.trainType = trainType;
        this.daysOfOperation = daysOfOperation;
    }

    //getters and setters 
    public String getTrainType() {
        return trainType;
    }
    public void setTrainType(String trainType) {
        this.trainType = trainType;
    }
    public String getDaysOfOperation() {
        return daysOfOperation;
    }
    public void setDaysOfOperation(String daysOfOperation) {
        this.daysOfOperation = daysOfOperation;
    } 

    //check if if train runs on a specific day 
    public boolean operatesOn(String day) {
        return false; 
    }

    //check if train type matches user's criteria 
    public boolean matchesType(String type){
        return false; 
    }


    public String displayTrainInfo(){
        return "";
    }


}
