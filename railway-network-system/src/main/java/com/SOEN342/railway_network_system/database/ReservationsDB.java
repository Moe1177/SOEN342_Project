package com.SOEN342.railway_network_system.database;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.SOEN342.railway_network_system.model.Reservation;
import com.SOEN342.railway_network_system.model.ReservationEntity;
import com.SOEN342.railway_network_system.model.PurchasedTicketEntity;
import com.SOEN342.railway_network_system.model.TicketPrice;

@Component
public class ReservationsDB {
    private final ReservationRepository reservationRepository;
    private final TicketRepository ticketRepository;
    private final PurchasedTicketRepository purchasedTicketRepository;
    private final AtomicLong ticketSeq = new AtomicLong(100000); // ticket numbers

    @Autowired
    public ReservationsDB(ReservationRepository reservationRepository, TicketRepository ticketRepository, PurchasedTicketRepository purchasedTicketRepository) {
        this.reservationRepository = reservationRepository;
        this.ticketRepository = ticketRepository;
        this.purchasedTicketRepository = purchasedTicketRepository;
    }

    private long nextTicketNumber() {
        // Initialize from DB max if needed to avoid duplicates across restarts
        long current = ticketSeq.get();
        long maxPurchased = purchasedTicketRepository.findTopByOrderByTicketNumberDesc()
            .map(PurchasedTicketEntity::getTicketNumber)
            .orElse(0L);
        long maxReserved = reservationRepository.findTopByOrderByTicketNumberDesc()
            .map(ReservationEntity::getTicketNumber)
            .orElse(0L);
        long maxDb = Math.max(maxPurchased, maxReserved);
        long desiredBase = Math.max(current, maxDb + 1);
        if (desiredBase > current) {
            ticketSeq.compareAndSet(current, desiredBase);
        }
        return ticketSeq.getAndIncrement();
    }

    // create and persist reservation. For uniqueness, enforce per route per (lastName, govId)
    public synchronized Reservation createReservation(String fn, String ln, int age, String govId, String routeID, String ticketClass, String tripId, Integer clientId){
        // Only prevent duplicates for the same passenger on the same trip (date), not across different days
        boolean exists = reservationRepository
            .findByPassengerLastNameIgnoreCaseAndPassengerGovIdIgnoreCase(ln, govId)
            .stream()
            .anyMatch(r -> r.getRouteId().equals(routeID) && r.getTripId().equals(tripId));
        if(exists){
            throw new IllegalStateException("Reservation already exists for passenger " + ln + " (" + govId + ") on this trip " + tripId + " (route " + routeID + ")");
        }

        Long ticketId = ticketRepository
            .findByRouteIdAndTicketType(routeID, ticketClass == null ? null : ticketClass.toLowerCase(Locale.ROOT))
            .map(TicketPrice::getTicketId)
            .orElse(null);

        long tno = nextTicketNumber();
        ReservationEntity entity = new ReservationEntity(null, tripId, routeID, clientId, ticketId,
            fn, ln, govId, age, ticketClass == null ? "" : ticketClass.toLowerCase(Locale.ROOT), tno);
        ReservationEntity saved = reservationRepository.save(entity);

        // persist purchased ticket record
        double price = 0.0;
        if (ticketId != null) {
            price = ticketRepository.findById(ticketId).map(TicketPrice::getTicketRate).orElse(0.0);
        }
        PurchasedTicketEntity purchase = new PurchasedTicketEntity(
            null,
            saved.getReservationId(),
            saved.getTripId(),
            saved.getRouteId(),
            saved.getClientId(),
            ticketId,
            saved.getTicketClass(),
            saved.getTicketNumber(),
            price,
            new Date()
        );
        purchasedTicketRepository.save(purchase);

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
