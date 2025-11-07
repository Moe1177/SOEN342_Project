package com.SOEN342.railway_network_system.database;

import com.SOEN342.railway_network_system.model.Ticket;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long>{
    List<Ticket> findByRouteId(String routeId);
}
