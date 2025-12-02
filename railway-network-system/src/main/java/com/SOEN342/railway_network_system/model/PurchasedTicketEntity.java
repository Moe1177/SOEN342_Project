package com.SOEN342.railway_network_system.model;

import java.util.Date;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "purchased_tickets")
@NoArgsConstructor
@AllArgsConstructor
public class PurchasedTicketEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "reservation_id", nullable = false)
    private Long reservationId;

    @Column(name = "trip_id", nullable = false)
    private String tripId;

    @Column(name = "route_id", nullable = false)
    private String routeId;

    @Column(name = "client_id")
    private Integer clientId;

    @Column(name = "ticket_id")
    private Long priceTicketId; // references TicketPrice.ticketId

    @Column(name = "ticket_class", nullable = false)
    private String ticketClass;

    @Column(name = "ticket_number", nullable = false, unique = true)
    private Long ticketNumber;

    @Column(name = "ticket_price", nullable = false)
    private double ticketPrice;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "purchase_time", nullable = false)
    private Date purchaseTime;
}


