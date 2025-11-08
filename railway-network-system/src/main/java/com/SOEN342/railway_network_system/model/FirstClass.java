package com.SOEN342.railway_network_system.model;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Entity
@DiscriminatorValue("First Class")
@NoArgsConstructor
public class FirstClass extends Ticket {

    private double ticketRate;
    private String ticketID;

    public FirstClass(String routeId, double ticketRate) {
        setRouteId(routeId);      // from CSV
        setTicketRate(ticketRate); // from CSV
    }

    @Override
    public double getTicketRate(){
        return ticketRate;
    }

    @Override
    public String getTicketId() {
        ticketID = "TKT-" + UUID.randomUUID().toString().substring(0, 8);
        return ticketID;
    }

    @Override
    @Transient
    public String displayTicketInfo(){
        return "First Class Ticket - Rate: $ " + ticketRate + " Id: " + ticketID;
    }
}
