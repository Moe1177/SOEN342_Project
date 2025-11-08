package com.SOEN342.railway_network_system.model;

import java.beans.Transient;
import java.util.UUID;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Entity
@DiscriminatorValue("Second class")
public class SecondClass extends Ticket{
    private double ticketRate;
    private String ticketId;

    public SecondClass(String routeId, double ticketRate) {
        setRouteId(routeId);      // from CSV
        setTicketRate(ticketRate); // from CSV
    }


    @Override
    public double getTicketRate(){
        return ticketRate;
    }

    @Override
    @Transient
    public String displayTicketInfo(){
        return "Second Class Ticket - Rate: $ " + ticketRate + " Id: " + ticketId;
    }
}
