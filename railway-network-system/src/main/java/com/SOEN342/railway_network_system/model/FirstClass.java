package com.SOEN342.railway_network_system.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FirstClass implements Ticket {
    private double ticketRate;

    @Override
    public double getTicketRate(){
        return ticketRate;
    }

    @Override
    public String displayTicketInfo(){
        return "First Class Ticket - Rate: $ " + ticketRate;
    }
}
