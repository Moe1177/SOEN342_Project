// package com.SOEN342.railway_network_system;

// import static org.junit.jupiter.api.Assertions.assertEquals;

// import java.text.SimpleDateFormat;
// import java.util.Date;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;

// import com.SOEN342.railway_network_system.database.RoutesDB;
// import com.SOEN342.railway_network_system.model.Route;

// class RoutesDBTest {
//     private RoutesDB db;

//     private static Date t(String hhmm) throws Exception {
//         return new SimpleDateFormat("HH:mm").parse(hhmm);
//     }

//     @BeforeEach
//     void setup() throws Exception {
//         db = new RoutesDB();
//         db.addRoute(new Route("R1", "A", "B", t("09:00"), t("10:00"), null));
//         db.addRoute(new Route("R2", "B", "C", t("10:15"), t("11:30"), null));
//         db.addRoute(new Route("R3", "A", "C", t("09:30"), t("12:00"), null));
//     }

//     @Test
//     void searchRoutes_byCities() {
//         Map<String,String> criteria = new HashMap<>();
//         criteria.put("departureCity", "A");
//         criteria.put("arrivalCity", "C");
//         List<Route> r = db.searchRoutes(criteria);
//         assertEquals(1, r.size());
//         assertEquals("R3", r.get(0).getRouteID());
//     }

//     @Test
//     void findIndirectRoutes_oneStop() {
//         List<Route> list = db.findIndirectRoutes("A", "C", 1);
//         // We added R1 then R2, so the combined list length should be 2 (R1,R2)
//         assertEquals(2, list.size());
//         assertEquals("R1", list.get(0).getRouteID());
//         assertEquals("R2", list.get(1).getRouteID());
//     }
// }
