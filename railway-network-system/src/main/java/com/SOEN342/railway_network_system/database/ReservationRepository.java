package com.SOEN342.railway_network_system.database;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.SOEN342.railway_network_system.model.ReservationEntity;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {
    List<ReservationEntity> findByPassengerLastNameIgnoreCaseAndPassengerGovIdIgnoreCase(String lastName, String govId);
    List<ReservationEntity> findByTripId(String tripId);
    java.util.Optional<ReservationEntity> findTopByOrderByTicketNumberDesc();
}


