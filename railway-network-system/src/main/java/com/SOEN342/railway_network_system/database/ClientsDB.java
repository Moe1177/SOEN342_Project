package com.SOEN342.railway_network_system.database;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.SOEN342.railway_network_system.model.Client;

@Component
public class ClientsDB {

    private final ClientRepository repo;

    @Autowired
    public ClientsDB(ClientRepository repo) {
        this.repo = repo;
    }
    private final Map<String, Client> clients = new HashMap<>();

    private String key(String lastName, String govId){
        return (lastName == null ? "" : lastName.trim().toLowerCase()) + "|" + 
               (govId == null ? "" : govId.trim().toLowerCase());
    }

    public Client upsertClient(String firstName, String lastName, String govId, int age) {
        Optional<Client> existing =
            repo.findByLastNameIgnoreCaseAndGovIdIgnoreCase(lastName, govId);

        if (existing.isPresent()) {
            Client c = existing.get();
            if (firstName != null && !firstName.isBlank()) {
                c.setFirstName(firstName);
            }
            return repo.save(c); // update
        } else {
            Client c = new Client(null, firstName, lastName, govId, age);
            return repo.save(c); // insert
        }
    }

    public Optional<Client> find(String lastName, String govId) {
        return repo.findByLastNameIgnoreCaseAndGovIdIgnoreCase(lastName, govId);
    }

    public Collection<Client> allClients() {
        return repo.findAll();
    }
}
