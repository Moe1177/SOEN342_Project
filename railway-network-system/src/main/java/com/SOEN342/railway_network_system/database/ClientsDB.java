package com.SOEN342.railway_network_system.database;

import java.util.*;

import org.springframework.stereotype.Component;

import com.SOEN342.railway_network_system.model.Client;

@Component
public class ClientsDB {
    private final Map<String, Client> clients = new HashMap<>();

    private String key(String lastName, String govId){
        return (lastName == null ? "" : lastName.trim().toLowerCase()) + "|" + 
               (govId == null ? "" : govId.trim().toLowerCase());
    }

    public Client upsertClient(String firstName, String lastName, String govId){
        String k = key(lastName, govId);
        Client c = clients.get(k);
        if(c == null){
            c = new Client(firstName, lastName, govId);
            clients.put(k, c);
        }else{
            if(firstName != null && !firstName.isBlank()){
                c.setFirstName(firstName);
            }
        }
        return c;
    }

    public Collection<Client> allClients(){
        return Collections.unmodifiableCollection(clients.values());
    }
}
