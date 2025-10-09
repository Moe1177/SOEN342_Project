package com.SOEN342.railway_network_system.database;

import java.util.*;
import com.SOEN342.railway_network_system.model.Train;

public class TrainsDB {
    private List<Train> trains = new ArrayList<>();

    //add train to train database
    public void addTrain(Train train){
        
    }

    //find train for specific route 
    public Train getTrainByRoute(String routeID){
        return new Train(null, null);
    }

    //returns all existing trains from database
    public List<Train> getAllTrains(){
        return trains;
    }
}
