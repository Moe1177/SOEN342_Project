package com.SOEN342.railway_network_system.model;

import java.util.UUID;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FirstClass extends Ticket {

    private String ticketID;

    public FirstClass(String routeId, double ticketRate) {
        setRouteId(routeId);
        setTicketRate(ticketRate);
    }

    @Override
    public String getTicketId() {
        ticketID = "TKT-" + UUID.randomUUID().toString().substring(0, 8);
        return ticketID;
    }

    @Override
    public String displayTicketInfo(){
        return "First Class Ticket - Rate: $ " + getTicketRate() + " Id: " + ticketID;
    }
}
