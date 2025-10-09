package com.SOEN342.railway_network_system.model;

public class SecondClass implements Ticket{
    private double ticketRate;

    public SecondClass(double ticketRate) {
        this.ticketRate = ticketRate;
    }

    @Override
    public double getTicketRate(){
        return ticketRate;
    }

    @Override
    public String displayTicketInfo(){
        return "Second Class Ticket - Rate: $ " + ticketRate;
    }
}
