package com.SOEN342.railway_network_system.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import com.SOEN342.railway_network_system.database.*;
import com.SOEN342.railway_network_system.model.*;

import jakarta.annotation.PostConstruct;

@Component
public class Console {
    private final RoutesDB routesDB;
    private final TrainsDB trainsDB;
    private final TicketsDB ticektsDB; 

    @Autowired
    public Console(RoutesDB routesDB, TrainsDB trainsDB, TicketsDB ticketsDB) {
        this.routesDB = routesDB;
        this.trainsDB = trainsDB;
        this.ticektsDB = ticketsDB;
    }

    @PostConstruct
    public void loadCsvData() {
        try {
            InputStream in = getClass().getClassLoader().getResourceAsStream("eu_rail_network.csv");
            if (in == null) return;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                String line;
                boolean first = true;
                while ((line = reader.readLine()) != null) {
                    if (first) { first = false; continue; } // skip header
                    // split CSV respecting simple commas; input has quotes around some fields with commas
                    List<String> cols = parseCsvLine(line);
                    if (cols.size() < 9) continue;
                    String routeId = cols.get(0);
                    String depCity = cols.get(1);
                    String arrCity = cols.get(2);
                    String depTimeStr = cols.get(3);
                    String arrTimeStr = cols.get(4);
                    String trainType = cols.get(5);
                    String days = cols.get(6);
                    double firstRate = safeParseDouble(cols.get(7));
                    double secondRate = safeParseDouble(cols.get(8));

                    Date depTime = parseTime(depTimeStr);
                    Date arrTime = parseTime(arrTimeStr);

                    Route route = new Route(routeId, depCity, arrCity, depTime, arrTime, null);
                    route.calculateDuration();
                    routesDB.addRoute(route);

                    trainsDB.addTrain(routeId, new Train(trainType, normalizeDays(days)));
                    ticektsDB.addTicket(routeId, "first", new FirstClass(firstRate));
                    ticektsDB.addTicket(routeId, "second", new SecondClass(secondRate));
                }
            }
        } catch (Exception e) {
            // swallow; in a real app we would log this
        }
    }

    //displays results of the user's search
    public void displayRoutes(List<Route> routes){
        if (routes == null || routes.isEmpty()) {
            System.out.println("No routes found.");
            return;
        }
        Route prev = null;
        for (Route r : routes) {
            System.out.println(r.displayRouteInfo());
            Train tr = trainsDB.getTrainByRoute(r.getRouteID());
            if (tr != null) {
                System.out.println("  " + tr.displayTrainInfo());
            }
            Ticket second = ticektsDB.getTicketByRoute(r.getRouteID(), "second");
            Ticket first = ticektsDB.getTicketByRoute(r.getRouteID(), "first");
            if (second != null) {
                System.out.println("  " + second.displayTicketInfo());
            }
            if (first != null) {
                System.out.println("  " + first.displayTicketInfo());
            }
            if (prev != null && canConnect(prev, r)) {
                String change = formatChangeTime(prev.getArrivalTime(), r.getDepartureTime());
                System.out.println("  Change time: " + change);
            } else if (prev != null) {
                System.out.println("--------------------");
            }
            prev = r;
        }
    }

    //gets the user input and maps the associated parameter to the user's input
    public Map<String, String> getUserInput(){
        return new HashMap<>();
    }

    public List<Route> sortRoutes(List<Route> routes, String sortBy){
        if (routes == null) return new ArrayList<>();
        List<Route> copy = new ArrayList<>(routes);
        if ("price".equalsIgnoreCase(sortBy)) {
            copy.sort(Comparator.comparingDouble(this::getSecondClassPrice));
        } else if ("trip duration".equalsIgnoreCase(sortBy) || "duration".equalsIgnoreCase(sortBy)) {
            copy.sort(Comparator.comparingInt(this::getDurationMinutes));
        }
        return copy;
    }

    // search using free-form criteria; if includeIndirect and departure/arrival provided, also build 1- and 2-stop options
    public List<Route> searchForConnection(Map<String, String> criteria, boolean includeIndirect){
        List<Route> direct = routesDB.searchRoutes(criteria);
        List<Route> results = new ArrayList<>(direct);
        if (includeIndirect) {
            String dep = criteria.get("departureCity");
            String arr = criteria.get("arrivalCity");
            if (dep != null && arr != null) {
                results.addAll(routesDB.findIndirectRoutes(dep, arr, 1));
                results.addAll(routesDB.findIndirectRoutes(dep, arr, 2));
            }
        }
        return results;
    }

    private int getDurationMinutes(Route r) {
        if (r.getTotalDuration() == null) r.calculateDuration();
        String d = r.getTotalDuration();
        if (d == null || !d.contains(":")) return Integer.MAX_VALUE;
        String[] parts = d.split(":");
        try {
            int h = Integer.parseInt(parts[0]);
            int m = Integer.parseInt(parts[1]);
            return h * 60 + m;
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE;
        }
    }

    private double getSecondClassPrice(Route r) {
        Ticket t = ticektsDB.getTicketByRoute(r.getRouteID(), "second");
        if (t != null) return t.getTicketRate();
        Ticket f = ticektsDB.getTicketByRoute(r.getRouteID(), "first");
        return f != null ? f.getTicketRate() : Double.MAX_VALUE;
    }

    private String normalizeDays(String days) {
        if (days == null) return null;
        return days.replace("\"", "").replace(" ", "");
    }

    private Date parseTime(String time) throws ParseException {
        boolean nextDay = time.contains("(+1d)");
        String cleaned = time.replace(" (+1d)", "").trim();
        SimpleDateFormat fmt = new SimpleDateFormat("HH:mm", Locale.US);
        Date base = fmt.parse(cleaned);
        if (nextDay) {
            return new Date(base.getTime() + TimeUnit.DAYS.toMillis(1));
        }
        return base;
    }

    private boolean canConnect(Route a, Route b) {
        if (a.getArrivalCity() == null || b.getDepartureCity() == null) return false;
        if (!a.getArrivalCity().equalsIgnoreCase(b.getDepartureCity())) return false;
        if (a.getArrivalTime() == null || b.getDepartureTime() == null) return true;
        return !a.getArrivalTime().after(b.getDepartureTime());
    }

    private String formatChangeTime(Date aArrive, Date bDepart) {
        if (aArrive == null || bDepart == null) return "N/A";
        long diff = bDepart.getTime() - aArrive.getTime();
        if (diff < 0) diff += TimeUnit.DAYS.toMillis(1);
        long hours = TimeUnit.MILLISECONDS.toHours(diff);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diff) - TimeUnit.HOURS.toMinutes(hours);
        StringBuilder sb = new StringBuilder();
        if (hours < 10) sb.append('0');
        sb.append(hours).append(":");
        if (minutes < 10) sb.append('0');
        sb.append(minutes);
        return sb.toString();
    }

    private static double safeParseDouble(String s) {
        try { return Double.parseDouble(s); } catch (Exception e) { return Double.NaN; }
    }

    // very small CSV parser for our specific file (handles quotes around fields with commas)
    private static List<String> parseCsvLine(String line) {
        List<String> cols = new ArrayList<>();
        if (line == null) return cols;
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                cols.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        cols.add(current.toString().trim());
        return cols;
    }
}
