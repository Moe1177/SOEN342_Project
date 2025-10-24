package com.SOEN342.railway_network_system.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Trip {
    private String tripId;       // unique alpha-numeric id
    private String routeID;      
    private Date departureDate;
    private List<Reservation> reservations = new ArrayList<>();

    public void addReservation(Reservation r){
        reservations.add(r);
    }
}
