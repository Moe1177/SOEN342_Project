package com.SOEN342.railway_network_system.model;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FirstClass implements Ticket {
    private double ticketRate;
    private String ticketID;

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
    public String displayTicketInfo(){
        return "First Class Ticket - Rate: $ " + ticketRate;
    }
}
