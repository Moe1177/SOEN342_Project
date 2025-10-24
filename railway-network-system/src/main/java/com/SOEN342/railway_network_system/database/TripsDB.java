package com.SOEN342.railway_network_system.database;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.SOEN342.railway_network_system.model.Reservation;
import com.SOEN342.railway_network_system.model.Route;
import com.SOEN342.railway_network_system.model.Trip;

@Component
public class TripsDB {
    private final Map<String, Trip> trips = new LinkedHashMap<>(); 
    private final Map<String, List<Reservation>> tripToReservations = new HashMap<>();

    private final SecureRandom rng = new SecureRandom();
    private final SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");

    @Autowired
    private ReservationsDB reservationsDB;

    //generate unique alphanumeric trip id, prefixed with date for readability
    private String genTripId(){
        String date = fmt.format(new Date());
        String alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // avoid ambiguous chars
        StringBuilder sb = new StringBuilder(date).append("-");
        for(int i=0;i<6;i++){
            sb.append(alphabet.charAt(rng.nextInt(alphabet.length())));
        }
        String id = sb.toString();
        while(trips.containsKey(id)){
            id = sb.append(alphabet.charAt(rng.nextInt(alphabet.length()))).toString();
        }
        return id;
    }

    public synchronized Trip createTrip(Route connection, List<Reservation> reservationsForTrip){
        if(connection == null) throw new IllegalArgumentException("Connection is required");
        String tripId = genTripId();
        Trip t = new Trip(tripId, connection.getRouteID(), new Date());
        if(reservationsForTrip != null){
            for(Reservation r: reservationsForTrip){
                t.addReservation(r);
            }
        }
        trips.put(tripId, t);
        tripToReservations.put(tripId, new ArrayList<>(t.getReservations()));
        return t;
    }

    public List<Trip> getTripsForClient(String lastName, String govId){
        //find all reservations for this client, then map to their trips
        Set<String> ticketOnRoute = reservationsDB.findByPassenger(lastName, govId)
                                                  .stream()
                                                  .map(Reservation::getRouteID)
                                                  .collect(Collectors.toSet());

        //collects trips whose reservations contain that passenger or route booked by them
        List<Trip> res = new ArrayList<>();
        for(Trip t: trips.values()){
            boolean match = t.getReservations().stream().anyMatch(r -> 
                r.getPassengerLastName().equalsIgnoreCase(lastName) && r.getPassengerGovId().equalsIgnoreCase(govId)
            );
            if(!match && ticketOnRoute.contains(t.getRouteID())){
                match = true;
            }
            if(match) res.add(t);
        }
        return res;
    }

    public Map<String, List<Reservation>> getTripReservationIndex(){
        return Collections.unmodifiableMap(tripToReservations);
    }

    public Collection<Trip> allTrips(){
        return Collections.unmodifiableCollection(trips.values());
    }
}



