package com.SOEN342.railway_network_system.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tickets")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "ticket_type", length = 31) 
@Data
@NoArgsConstructor
public abstract class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private int ticketId;                 

    @Column(name = "route_id", nullable = false)
    private String routeId;                

    @Column(name = "ticket_rate", nullable = false)
    private double ticketRate; 

    public Ticket(String routeID, double ticketRate){
        this.ticketRate = ticketRate;
        this.routeId = routeID;
    }

    public double getTicketRate(){
        return ticketRate;
    }

    public String getTicketId(){
        return null; 
    }

    public String displayTicketInfo(){
        return null; 
    }

}
