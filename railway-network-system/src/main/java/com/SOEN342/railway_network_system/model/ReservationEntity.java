package com.SOEN342.railway_network_system.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "reservations")
@NoArgsConstructor
@AllArgsConstructor
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long reservationId;

    @Column(name = "trip_id", nullable = false)
    private String tripId;

    @Column(name = "route_id", nullable = false)
    private String routeId;

    @Column(name = "client_id")
    private Integer clientId; // optional: passenger may or may not be linked

    @Column(name = "ticket_id")
    private Long ticketId; // optional link to tickets table

    @Column(name = "passenger_first_name", nullable = false)
    private String passengerFirstName;

    @Column(name = "passenger_last_name", nullable = false)
    private String passengerLastName;

    @Column(name = "passenger_gov_id", nullable = false)
    private String passengerGovId;

    @Column(name = "passenger_age", nullable = false)
    private int passengerAge;

    @Column(name = "ticket_class", nullable = false)
    private String ticketClass;

    @Column(name = "ticket_number", nullable = false)
    private Long ticketNumber;
}


