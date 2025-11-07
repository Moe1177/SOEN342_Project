package com.SOEN342.railway_network_system.database;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.SOEN342.railway_network_system.model.Train;

@Component
public class TrainsDB {
    private final Map<String, Train> routeIdToTrain = new HashMap<>();
     private final TrainRepository trainRepository;

    @Autowired
    public TrainsDB(TrainRepository trainRepository) {
        this.trainRepository = trainRepository;
    }

    //add train to train database and associate with a routeId
    public void addTrain(String routeID, Train train){
        if (routeID == null || train == null) return;
        routeIdToTrain.put(routeID, train);
        if (trainRepository != null) {
            train.setRouteId(routeID);
            trainRepository.save(train);
        }
    }

    //find train for specific route 
    public Train getTrainByRoute(String routeID){
        if (routeID == null) return null;
        return routeIdToTrain.get(routeID);
    }

    //returns all existing trains from database
    public List<Train> getAllTrains(){
        return new ArrayList<>(routeIdToTrain.values());
    }
}
