package com.SOEN342.railway_network_system.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Client {
    private String firstName;
    private String lastName;
    private String govId;   
}
