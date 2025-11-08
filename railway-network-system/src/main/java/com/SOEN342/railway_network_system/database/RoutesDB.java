package com.SOEN342.railway_network_system.database;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.SOEN342.railway_network_system.model.Route;

@Component
public class RoutesDB {
    private final List<Route> routes = new ArrayList<>();
    private final List<Route> results = new ArrayList<Route>();

    
    private final RouteRepository routeRepository;

    @Autowired
    public RoutesDB(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    //add route to routes database
    public void addRoute(Route route){
        if (route == null) return;
        // Ensure duration is set if times are provided
        route.calculateDuration();
        routes.add(route);
        routeRepository.save(route);
    }

    //return all routes (defensive copy)
    public List<Route> getAllRoutes(){
        return new ArrayList<>(routes);
    }

    //return routes that match search criteria 
    public List<Route> searchRoutes(Map<String, String> criteria){
        List<Route> matches;
        if (criteria == null || criteria.isEmpty()) {
            matches = getAllRoutes();
        } else {
            matches = routes.stream()
                .filter(r -> r.matchesCriteria(criteria))
                .collect(Collectors.toList());
        }
        results.clear();
        results.addAll(matches);
        return matches;
    }

    //find routes with connections (1 or 2 stops)
    public List<Route> findIndirectRoutes(String departureCity, String arrivalCity, int numStops){
        List<Route> result = new ArrayList<>();
        if (departureCity == null || arrivalCity == null) return result;

        List<Route> fromRoutes = routes.stream()
            .filter(r -> departureCity.equalsIgnoreCase(r.getDepartureCity()))
            .collect(Collectors.toList());
        List<Route> toRoutes = routes.stream()
            .filter(r -> arrivalCity.equalsIgnoreCase(r.getArrivalCity()))
            .collect(Collectors.toList());

        if (numStops == 1) {
            for (Route first : fromRoutes) {
                for (Route second : toRoutes) {
                    if (first.getArrivalCity() != null && first.getArrivalCity().equalsIgnoreCase(second.getDepartureCity())) {
                        if (canConnect(first, second)) {
                            result.add(first);
                            result.add(second);
                        }
                        
                    }
                }
            }
        } else if (numStops == 2) {
            for (Route first : fromRoutes) {
                for (Route middle : routes) {
                    if (first.getArrivalCity() != null && first.getArrivalCity().equalsIgnoreCase(middle.getDepartureCity())) {
                        if (!canConnect(first, middle)) continue;
                        for (Route third : toRoutes) {
                            if (middle.getArrivalCity() != null && middle.getArrivalCity().equalsIgnoreCase(third.getDepartureCity())) {
                                if (canConnect(middle, third)) {
                                    result.add(first);
                                    result.add(middle);
                                    result.add(third);
                                }
                            }
                        }
                    }
                }
            }
        }
        results.clear();
        results.addAll(result);
        return result;
    }

    private boolean canConnect(Route a, Route b) {
        if (a == null || b == null) return false;
        if (a.getArrivalCity() == null || b.getDepartureCity() == null) return false;
        if (!a.getArrivalCity().equalsIgnoreCase(b.getDepartureCity())) return false;
        if (a.getArrivalTime() == null || b.getDepartureTime() == null) return false;
        long diff = b.getDepartureTime().getTime() - a.getArrivalTime().getTime();
        if (diff < 0) {
            // handle next-day departures
            diff += java.util.concurrent.TimeUnit.DAYS.toMillis(1);
        }
        // must be non-negative and at most 6 hours
        long maxLayover = java.util.concurrent.TimeUnit.HOURS.toMillis(6);
        return diff >= 0 && diff <= maxLayover;
    }

    // expose last search results (defensive copy)
    public List<Route> getResults(){
        return new ArrayList<>(results);
    }
}
