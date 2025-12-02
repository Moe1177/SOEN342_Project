package com.SOEN342.railway_network_system.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "ticket_prices")
@NoArgsConstructor
@AllArgsConstructor
public class TicketPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Long ticketId;

    @Column(name = "route_id", nullable = false)
    private String routeId;

    @Column(name = "ticket_type", nullable = false)
    private String ticketType; // first | second

    @Column(name = "ticket_rate", nullable = false)
    private double ticketRate;
}


