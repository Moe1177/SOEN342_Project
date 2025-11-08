package com.SOEN342.railway_network_system.database;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.SOEN342.railway_network_system.model.Reservation;
import com.SOEN342.railway_network_system.model.ReservationEntity;
import com.SOEN342.railway_network_system.model.TicketPrice;

@Component
public class ReservationsDB {
    private final ReservationRepository reservationRepository;
    private final TicketRepository ticketRepository;
    private final AtomicLong ticketSeq = new AtomicLong(100000); // ticket numbers

    @Autowired
    public ReservationsDB(ReservationRepository reservationRepository, TicketRepository ticketRepository) {
        this.reservationRepository = reservationRepository;
        this.ticketRepository = ticketRepository;
    }

    // create and persist reservation. For uniqueness, enforce per route per (lastName, govId)
    public synchronized Reservation createReservation(String fn, String ln, int age, String govId, String routeID, String ticketClass, String tripId, Integer clientId){
        boolean exists = reservationRepository
            .findByPassengerLastNameIgnoreCaseAndPassengerGovIdIgnoreCase(ln, govId)
            .stream()
            .anyMatch(r -> r.getRouteId().equals(routeID));
        if(exists){
            throw new IllegalStateException("Reservation already exists for passenger " + ln + " (" + govId + ") on route " + routeID);
        }

        Long ticketId = ticketRepository
            .findByRouteIdAndTicketType(routeID, ticketClass == null ? null : ticketClass.toLowerCase(Locale.ROOT))
            .map(TicketPrice::getTicketId)
            .orElse(null);

        long tno = ticketSeq.getAndIncrement();
        ReservationEntity entity = new ReservationEntity(null, tripId, routeID, clientId, ticketId,
            fn, ln, govId, age, ticketClass == null ? "" : ticketClass.toLowerCase(Locale.ROOT), tno);
        ReservationEntity saved = reservationRepository.save(entity);

        return new Reservation(saved.getPassengerFirstName(), saved.getPassengerLastName(), saved.getPassengerAge(),
                               saved.getPassengerGovId(), saved.getRouteId(), saved.getTicketClass(), saved.getTicketNumber());
    }

    public List<Reservation> findByPassenger(String lastName, String govId){
        return reservationRepository
            .findByPassengerLastNameIgnoreCaseAndPassengerGovIdIgnoreCase(lastName, govId)
            .stream()
            .map(r -> new Reservation(r.getPassengerFirstName(), r.getPassengerLastName(), r.getPassengerAge(), r.getPassengerGovId(), r.getRouteId(), r.getTicketClass(), r.getTicketNumber()))
            .collect(Collectors.toList());
    }

    public List<Reservation> findByTrip(String tripId){
        return reservationRepository.findByTripId(tripId).stream()
            .map(r -> new Reservation(r.getPassengerFirstName(), r.getPassengerLastName(), r.getPassengerAge(), r.getPassengerGovId(), r.getRouteId(), r.getTicketClass(), r.getTicketNumber()))
            .collect(Collectors.toList());
    }

    public List<Reservation> allReservations(){
        return reservationRepository.findAll().stream()
            .map(r -> new Reservation(r.getPassengerFirstName(), r.getPassengerLastName(), r.getPassengerAge(), r.getPassengerGovId(), r.getRouteId(), r.getTicketClass(), r.getTicketNumber()))
            .collect(Collectors.toList());
    }
}
