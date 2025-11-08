package com.SOEN342.railway_network_system.model;

import java.util.Date;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "trips")
@NoArgsConstructor
@AllArgsConstructor
public class TripEntity {

    @Id
    @Column(name = "trip_id")
    private String tripId; // keep same alpha-numeric id used by CLI

    @Column(name = "route_id", nullable = false)
    private String routeId;

    @Temporal(TemporalType.DATE)
    @Column(name = "departure_date", nullable = false)
    private Date departureDate;
}


