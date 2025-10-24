package com.SOEN342.railway_network_system.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    private String passengerFirstName;
    private String passengerLastName;
    private int passengerAge;
    private String passengerGovId;

    private String routeID;           
    private String ticketClass;       
    private long ticketNumber;       
}
