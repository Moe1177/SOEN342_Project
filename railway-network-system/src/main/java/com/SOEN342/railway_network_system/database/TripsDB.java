package com.SOEN342.railway_network_system.database;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.SOEN342.railway_network_system.model.Reservation;
import com.SOEN342.railway_network_system.model.Route;
import com.SOEN342.railway_network_system.model.Trip;
import com.SOEN342.railway_network_system.model.TripEntity;

@Component
public class TripsDB {
    private final TripRepository tripRepository;

    private final SecureRandom rng = new SecureRandom();
    private final SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");

    @Autowired
    private ReservationsDB reservationsDB;

    @Autowired
    public TripsDB(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    // generate unique alphanumeric trip id, prefixed with date for readability
    private String genTripId(){
        String date = fmt.format(new Date());
        String alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // avoid ambiguous chars
        StringBuilder sb = new StringBuilder(date).append("-");
        for(int i=0;i<6;i++){
            sb.append(alphabet.charAt(rng.nextInt(alphabet.length())));
        }
        return sb.toString();
    }

    public synchronized Trip createTrip(Route connection, List<Reservation> reservationsForTrip, Date departureDate){
        if(connection == null) throw new IllegalArgumentException("Connection is required");
        String tripId = genTripId();
        TripEntity entity = new TripEntity(tripId, connection.getRouteID(), departureDate == null ? new Date() : departureDate);
        tripRepository.save(entity);

        Trip t = new Trip(tripId, entity.getRouteId(), entity.getDepartureDate());
        if(reservationsForTrip != null){
            for(Reservation r: reservationsForTrip){
                t.addReservation(r);
            }
        }
        return t;
    }

    public List<Trip> getTripsForClient(String lastName, String govId){
        // derive trips by querying reservations for the passenger, then loading trips
        List<String> tripIds = reservationsDB.findByPassenger(lastName, govId).stream()
            .collect(Collectors.groupingBy(Reservation::getRouteID)) // group not necessary; just map to their tripIds later
            .keySet() // placeholder; we'll fetch all trips and filter by having any reservation for passenger
            .stream().collect(Collectors.toList());

        // fallback: simply load all trips and then attach reservations matching the passenger
        List<Trip> all = tripRepository.findAll().stream()
            .map(e -> new Trip(e.getTripId(), e.getRouteId(), e.getDepartureDate()))
            .collect(Collectors.toList());

        Map<String, List<Reservation>> byTrip = tripRepository.findAll().stream()
            .collect(Collectors.toMap(TripEntity::getTripId, e -> reservationsDB.findByTrip(e.getTripId())));

        List<Trip> mine = new ArrayList<>();
        for (Trip t : all) {
            List<Reservation> rs = byTrip.getOrDefault(t.getTripId(), new ArrayList<>());
            for (Reservation r : rs) {
                if (r.getPassengerLastName().equalsIgnoreCase(lastName) && r.getPassengerGovId().equalsIgnoreCase(govId)) {
                    for (Reservation r2 : rs) t.addReservation(r2);
                    mine.add(t);
                    break;
                }
            }
        }
        return mine;
    }

    public Collection<Trip> allTrips(){
        return tripRepository.findAll().stream()
            .map(e -> new Trip(e.getTripId(), e.getRouteId(), e.getDepartureDate()))
            .collect(Collectors.toList());
    }
}



