package com.SOEN342.railway_network_system.database;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.SOEN342.railway_network_system.model.TicketPrice;

public interface TicketRepository extends JpaRepository<TicketPrice, Long> {
    Optional<TicketPrice> findByRouteIdAndTicketType(String routeId, String ticketType);
    List<TicketPrice> findByRouteId(String routeId);
}


