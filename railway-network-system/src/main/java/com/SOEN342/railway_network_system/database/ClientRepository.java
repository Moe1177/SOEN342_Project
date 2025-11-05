package com.SOEN342.railway_network_system.database;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.SOEN342.railway_network_system.model.Client;


public interface ClientRepository extends JpaRepository<Client, Long>{
    Optional<Client> findByLastNameIgnoreCaseAndGovIdIgnoreCase(String lastName, String govId);
}
