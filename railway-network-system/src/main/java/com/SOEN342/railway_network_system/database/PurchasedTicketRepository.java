package com.SOEN342.railway_network_system.database;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.SOEN342.railway_network_system.model.PurchasedTicketEntity;

public interface PurchasedTicketRepository extends JpaRepository<PurchasedTicketEntity, Long> {
    Optional<PurchasedTicketEntity> findByTicketNumber(Long ticketNumber);
    Optional<PurchasedTicketEntity> findTopByOrderByTicketNumberDesc();
}


