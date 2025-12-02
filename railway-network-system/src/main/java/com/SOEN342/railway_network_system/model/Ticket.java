package com.SOEN342.railway_network_system.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class Ticket {

    private String routeId;
    private double ticketRate;

    public Ticket(String routeID, double ticketRate){
        this.ticketRate = ticketRate;
        this.routeId = routeID;
    }

    public double getTicketRate(){
        return ticketRate;
    }

    public String getTicketId(){
        return null; 
    }

    public String displayTicketInfo(){
        return null; 
    }

}
