package com.SOEN342.railway_network_system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.SOEN342.railway_network_system.model.Route;

class RouteTest {
    private static Date time(String hhmm) throws Exception {
        return new SimpleDateFormat("HH:mm").parse(hhmm);
    }

    @Test
    void calculateDuration_sameDay() throws Exception {
        Route r = new Route("R1", "A", "B", time("10:00"), time("12:30"), null);
        r.calculateDuration();
        assertEquals("02:30", r.getTotalDuration());
    }

    @Test
    void calculateDuration_nextDay() throws Exception {
        Route r = new Route("R2", "A", "B", time("22:15"), time("01:05"), null);
        r.calculateDuration();
        assertEquals("02:50", r.getTotalDuration());
    }

    @Test
    void matchesCriteria_basic() throws Exception {
        Route r = new Route("R3", "Paris", "London", time("09:00"), time("11:30"), null);
        r.calculateDuration();
        Map<String, String> criteria = new HashMap<>();
        criteria.put("departureCity", "Paris");
        criteria.put("arrivalCity", "London");
        assertTrue(r.matchesCriteria(criteria));

        criteria.put("departureCity", "Rome");
        assertFalse(r.matchesCriteria(criteria));
    }

}
