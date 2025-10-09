package com.SOEN342.railway_network_system.model;

public class FirstClass implements Ticket {
    private double ticketRate;

    public FirstClass(double ticketRate) {
        this.ticketRate = ticketRate;
    } 

    @Override
    public double getTicketRate(){
        return ticketRate;
    }

    @Override
    public String displayTicketInfo(){
        return "First Class Ticket - Rate: $ " + ticketRate;
    }
}
