package com.SOEN342.railway_network_system.database;

import java.util.*;
import com.SOEN342.railway_network_system.model.Ticket;

public class TicketsDB {
    private List<Ticket> tickets = new ArrayList<>();

    //add ticket to tickets database
    public void addTicket(Ticket ticket){

    }
    
    //find ticket for specific route or classtype
    public Ticket getTicketByRoute(String routeID, String classType){
        return null;
    }

    //returns all existing tickets
    public List<Ticket> getAllTickets(){
        return tickets; 
    }
}
