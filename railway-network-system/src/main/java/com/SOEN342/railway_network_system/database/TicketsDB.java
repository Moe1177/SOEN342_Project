package com.SOEN342.railway_network_system.database;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.SOEN342.railway_network_system.model.FirstClass;
import com.SOEN342.railway_network_system.model.SecondClass;
import com.SOEN342.railway_network_system.model.Ticket;
import com.SOEN342.railway_network_system.model.TicketPrice;

@Component
public class TicketsDB {
    private final TicketRepository ticketRepository;

    @Autowired
    public TicketsDB(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    // add or update ticket price for a route and class name
    public void addTicket(String routeID, String classType, Ticket ticket){
        if (routeID == null || classType == null || ticket == null) return;
        String klass = classType.toLowerCase(Locale.ROOT);
        Optional<TicketPrice> existing = ticketRepository.findByRouteIdAndTicketType(routeID, klass);
        TicketPrice price = existing.orElseGet(() -> new TicketPrice(null, routeID, klass, 0.0));
        price.setTicketRate(ticket.getTicketRate());
        ticketRepository.save(price);
    }
    
    // find ticket for specific route or class type (returns a lightweight value object)
    public Ticket getTicketByRoute(String routeID, String classType){
        if (routeID == null || classType == null) return null;
        String klass = classType.toLowerCase(Locale.ROOT);
        return ticketRepository.findByRouteIdAndTicketType(routeID, klass)
            .map(tp -> "first".equals(klass)
                ? (Ticket) new FirstClass(routeID, tp.getTicketRate())
                : (Ticket) new SecondClass(routeID, tp.getTicketRate()))
            .orElse(null);
    }

    // returns all existing tickets as value objects
    public List<Ticket> getAllTickets(){
        List<Ticket> out = new ArrayList<>();
        for (TicketPrice tp : ticketRepository.findAll()) {
            if ("first".equalsIgnoreCase(tp.getTicketType())) {
                out.add(new FirstClass(tp.getRouteId(), tp.getTicketRate()));
            } else {
                out.add(new SecondClass(tp.getRouteId(), tp.getTicketRate()));
            }
        }
        return out; 
    }
}
