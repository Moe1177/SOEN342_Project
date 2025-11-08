package com.SOEN342.railway_network_system.database;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.SOEN342.railway_network_system.model.Ticket;

@Component
public class TicketsDB {
    private final Map<String, Map<String, Ticket>> routeIdToClassTicket = new HashMap<>();

    private final TicketRepository ticketRepository;

    @Autowired
    public TicketsDB(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    //add ticket to tickets database for a route and class name
    public void addTicket(String routeID, String classType, Ticket ticket){
        if (routeID == null || classType == null || ticket == null) return;
        routeIdToClassTicket
            .computeIfAbsent(routeID, k -> new HashMap<>())
            .put(classType.toLowerCase(), ticket);

        if (ticketRepository != null) {
            ticket.setRouteId(routeID);
            ticketRepository.save(ticket);
        }
    }
    
    //find ticket for specific route or class type
    public Ticket getTicketByRoute(String routeID, String classType){
        if (routeID == null || classType == null) return null;
        Map<String, Ticket> byClass = routeIdToClassTicket.get(routeID);
        if (byClass == null) return null;
        return byClass.get(classType.toLowerCase());
    }

    //returns all existing tickets (flattened list)
    public List<Ticket> getAllTickets(){
        List<Ticket> all = new ArrayList<>();
        for (Map<String, Ticket> m : routeIdToClassTicket.values()) {
            all.addAll(m.values());
        }
        return all; 
    }
}
