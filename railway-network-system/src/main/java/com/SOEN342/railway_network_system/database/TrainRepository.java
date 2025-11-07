package com.SOEN342.railway_network_system.database;

import com.SOEN342.railway_network_system.model.Train;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


public interface TrainRepository extends JpaRepository<Train, Long>{
    Optional<Train> findByRouteId(String routeId);

}
