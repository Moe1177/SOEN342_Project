package com.SOEN342.railway_network_system.database;

import org.springframework.data.jpa.repository.JpaRepository;

import com.SOEN342.railway_network_system.model.TripEntity;

public interface TripRepository extends JpaRepository<TripEntity, String> {
}


