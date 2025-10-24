package com.SOEN342.railway_network_system.database;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.SOEN342.railway_network_system.model.Reservation;

@Component
public class ReservationsDB {
    private final List<Reservation> reservations = new ArrayList<>();
    private final AtomicLong ticketSeq = new AtomicLong(100000); // ticket numbers

    //checks for uniqueness: for a given route, a client (last name + id) may only have a single reservation
    public synchronized Reservation createReservation(String fn, String ln, int age, String govId, String routeID, String ticketClass){
        // enforce uniqueness per route per (lastName, govId)
        boolean exists = reservations.stream().anyMatch(r -> 
            r.getRouteID().equals(routeID) &&
            r.getPassengerLastName().equalsIgnoreCase(ln) &&
            r.getPassengerGovId().equalsIgnoreCase(govId)
        );
        if(exists){
            throw new IllegalStateException("Reservation already exists for passenger " + ln + " (" + govId + ") on route " + routeID);
        }
        long tno = ticketSeq.getAndIncrement();
        Reservation r = new Reservation(fn, ln, age, govId, routeID, ticketClass, tno);
        reservations.add(r);
        return r;
    }

    public List<Reservation> findByPassenger(String lastName, String govId){
        return reservations.stream()
            .filter(r -> r.getPassengerLastName().equalsIgnoreCase(lastName) 
                      && r.getPassengerGovId().equalsIgnoreCase(govId))
            .collect(Collectors.toList());
    }

    public List<Reservation> findByTrip(String tripId, Map<String, List<Reservation>> tripIndex){
        return tripIndex.getOrDefault(tripId, Collections.emptyList());
    }

    public List<Reservation> allReservations(){
        return Collections.unmodifiableList(reservations);
    }
}
