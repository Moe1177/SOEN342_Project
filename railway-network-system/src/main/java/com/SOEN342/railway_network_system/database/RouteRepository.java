package com.SOEN342.railway_network_system.database;

import org.springframework.data.jpa.repository.JpaRepository;
import com.SOEN342.railway_network_system.model.Route;
import java.util.List;


public interface RouteRepository extends JpaRepository<Route, String> {
    List<Route> findByDepartureCityIgnoreCaseAndArrivalCityIgnoreCase(String departureCity, String arrivalCity);
}
