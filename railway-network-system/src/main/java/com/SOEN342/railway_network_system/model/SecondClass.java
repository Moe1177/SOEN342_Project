package com.SOEN342.railway_network_system.model;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecondClass implements Ticket{
    private double ticketRate;
    private String ticketId;

    @Override
    public double getTicketRate(){
        return ticketRate;
    }

    @Override 
    public String getTicketId(){
        ticketId = "TKT-" + UUID.randomUUID().toString().substring(0, 8);
        return ticketId;
    }

    @Override
    public String displayTicketInfo(){
        return "Second Class Ticket - Rate: $ " + ticketRate + " Id: " + ticketId;
    }
}
