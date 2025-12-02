package com.SOEN342.railway_network_system.model;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SecondClass extends Ticket{
    private String ticketId;

    public SecondClass(String routeId, double ticketRate) {
        setRouteId(routeId);
        setTicketRate(ticketRate);
    }

    @Override
    public String displayTicketInfo(){
        return "Second Class Ticket - Rate: $ " + getTicketRate() + " Id: " + ticketId;
    }
}
