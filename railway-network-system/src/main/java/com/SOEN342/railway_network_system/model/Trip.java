package com.SOEN342.railway_network_system.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public 

class Trip {
    private final String tripId;       // unique alpha-numeric id
    private final String routeID;      
    private final Date departureDate;
    private final List<Reservation> reservations = new ArrayList<>();

    public void addReservation(Reservation r){
        reservations.add(r);
    }
}
