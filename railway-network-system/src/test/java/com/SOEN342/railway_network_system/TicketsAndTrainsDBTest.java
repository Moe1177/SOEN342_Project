// package com.SOEN342.railway_network_system;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertFalse;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertTrue;

// import org.junit.jupiter.api.Test;

// import com.SOEN342.railway_network_system.database.TicketsDB;
// import com.SOEN342.railway_network_system.database.TrainsDB;
// import com.SOEN342.railway_network_system.model.FirstClass;
// import com.SOEN342.railway_network_system.model.SecondClass;
// import com.SOEN342.railway_network_system.model.Ticket;
// import com.SOEN342.railway_network_system.model.Train;

// class TicketsAndTrainsDBTest {
//         @Test
//     void ticketsDb_storeAndRetrieve() {
//         TicketsDB tickets = new TicketsDB();
//         tickets.addTicket("R1", "first", new FirstClass(100));
//         tickets.addTicket("R1", "second", new SecondClass(60));

//         Ticket f = tickets.getTicketByRoute("R1", "first");
//         Ticket s = tickets.getTicketByRoute("R1", "second");
//         assertNotNull(f);
//         assertNotNull(s);
//         assertEquals(100.0, f.getTicketRate());
//         assertEquals(60.0, s.getTicketRate());
//     }

//     @Test
//     void trainsDb_storeAndRetrieve() {
//         TrainsDB trains = new TrainsDB();
//         Train t = new Train("TGV", "Mon-Fri");
//         trains.addTrain("R1", t);

//         Train fetched = trains.getTrainByRoute("R1");
//         assertNotNull(fetched);
//         assertTrue(fetched.matchesType("tgv"));
//         assertTrue(fetched.operatesOn("Mon"));
//         assertFalse(fetched.operatesOn("Sun"));
//     }
// }
