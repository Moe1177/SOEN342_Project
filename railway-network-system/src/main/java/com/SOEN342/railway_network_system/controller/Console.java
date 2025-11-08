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
import java.util.Scanner;
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
    private final ClientsDB clientsDB;
    private final ReservationsDB reservationsDB;
    private final TripsDB tripsDB;

    @Autowired
    public Console(RoutesDB routesDB, TrainsDB trainsDB, TicketsDB ticketsDB, ClientsDB clientsDB, ReservationsDB reservationsDB, TripsDB tripsDB) {
        this.routesDB = routesDB;
        this.trainsDB = trainsDB;
        this.ticektsDB = ticketsDB;
        this.clientsDB = clientsDB;
        this.reservationsDB = reservationsDB;
        this.tripsDB = tripsDB;
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
                    ticektsDB.addTicket(routeId, "first", new FirstClass(routeId, firstRate));
                    ticektsDB.addTicket(routeId, "second", new SecondClass(routeId, secondRate));
                }
            }
        } catch (Exception e) {
            // swallow; in a real app we would log this
        }
    }

    //displays results of the user's search
    public void displayRoutes(List<Route> routes){
		List<List<Route>> itineraries = groupItineraries(routes);
		if (itineraries.isEmpty()) {
			System.out.println("No routes found.");
			return;
		}
		int idx = 0;
		for (List<Route> itin : itineraries) {
			Route first = itin.get(0);
			Route last = itin.get(itin.size() - 1);
			System.out.println(idx + ") " + first.getDepartureCity() + " -> " + last.getArrivalCity() + " (" + (itin.size() - 1) + " stop" + (itin.size() - 1 == 1 ? "" : "s") + ")");
			Route prev = null;
			for (Route r : itin) {
				System.out.println("  " + r.displayRouteInfo());
				Train tr = trainsDB.getTrainByRoute(r.getRouteID());
				if (tr != null) {
					System.out.println("    " + tr.displayTrainInfo());
				}
				Ticket second = ticektsDB.getTicketByRoute(r.getRouteID(), "second");
				Ticket firstT = ticektsDB.getTicketByRoute(r.getRouteID(), "first");
				if (second != null) {
					System.out.println("    " + second.displayTicketInfo());
				}
				if (firstT != null) {
					System.out.println("    " + firstT.displayTicketInfo());
				}
				if (prev != null && canConnect(prev, r)) {
					String change = formatChangeTime(prev.getArrivalTime(), r.getDepartureTime());
					System.out.println("    Change time: " + change);
				}
				prev = r;
			}
			idx++;
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
        // base filter using Route.matchesCriteria for cities and duration
        Map<String,String> base = new HashMap<>();
        if (criteria != null){
            for (Map.Entry<String,String> e: criteria.entrySet()){
                String k = e.getKey();
                if ("routeID".equalsIgnoreCase(k)) continue; // ignore id
                if ("departureCity".equalsIgnoreCase(k) || "arrivalCity".equalsIgnoreCase(k) || "totalDuration".equalsIgnoreCase(k)){
                    base.put(k, e.getValue());
                }
            }
        }
        List<Route> direct = routesDB.searchRoutes(base);
        List<Route> results = new ArrayList<>(direct);
        if (includeIndirect) {
            String dep = criteria.get("departureCity");
            String arr = criteria.get("arrivalCity");
            if (dep != null && arr != null) {
				List<Route> rawOneStop = routesDB.findIndirectRoutes(dep, arr, 1);
				List<Route> rawTwoStop = routesDB.findIndirectRoutes(dep, arr, 2);
				List<Route> combined = new ArrayList<>();
				combined.addAll(rawOneStop);
				combined.addAll(rawTwoStop);
				// group into itineraries and filter by days of operation coherence
				List<List<Route>> itins = groupItineraries(combined);
				String opDay = criteria.get("operatingDay");
				itins = filterItinerariesByOperatingDay(itins, opDay);
				// flatten back to list for downstream filters/display
				for (List<Route> itin : itins) {
					results.addAll(itin);
				}
            }
        }
        // additional filters: times, train type, operating day, price caps
        results = filterAdditional(results, criteria);
        return results;
    }

    private List<Route> filterAdditional(List<Route> routes, Map<String,String> criteria){
        if (routes == null) return new ArrayList<>();
        if (criteria == null || criteria.isEmpty()) return routes;
        String depTime = criteria.get("departureTime");
        String arrTime = criteria.get("arrivalTime");
        String trainType = criteria.get("trainType");
        String opDay = criteria.get("operatingDay");
        String maxFirst = criteria.get("maxFirstPrice");
        String maxSecond = criteria.get("maxSecondPrice");

        SimpleDateFormat hhmm = new SimpleDateFormat("HH:mm", Locale.US);
        Date depFilter = null, arrFilter = null;
        try { if (depTime != null && !depTime.isEmpty()) depFilter = hhmm.parse(depTime); } catch(Exception ignored){}
        try { if (arrTime != null && !arrTime.isEmpty()) arrFilter = hhmm.parse(arrTime); } catch(Exception ignored){}

        Double maxFirstD = parseDoubleOrNull(maxFirst);
        Double maxSecondD = parseDoubleOrNull(maxSecond);

        List<Route> out = new ArrayList<>();
        for (Route r: routes){
            if (depFilter != null && r.getDepartureTime() != null && r.getDepartureTime().getTime() != depFilter.getTime()) continue;
            if (arrFilter != null && r.getArrivalTime() != null && r.getArrivalTime().getTime() != arrFilter.getTime()) continue;
            if (trainType != null && !trainType.isEmpty()){
                Train t = trainsDB.getTrainByRoute(r.getRouteID());
                if (t == null || !t.matchesType(trainType)) continue;
            }
            if (opDay != null && !opDay.isEmpty()){
                Train t = trainsDB.getTrainByRoute(r.getRouteID());
                if (t == null || t.getDaysOfOperation() == null || t.getDaysOfOperation().isEmpty()) continue;
                if (!t.operatesOn(opDay)) continue;
            }
            if (maxFirstD != null){
                Ticket f = ticektsDB.getTicketByRoute(r.getRouteID(), "first");
                if (f == null || f.getTicketRate() > maxFirstD) continue;
            }
            if (maxSecondD != null){
                Ticket s = ticektsDB.getTicketByRoute(r.getRouteID(), "second");
                if (s == null || s.getTicketRate() > maxSecondD) continue;
            }
            out.add(r);
        }
        return out;
    }

    private Double parseDoubleOrNull(String s){
        try{ return s==null||s.trim().isEmpty()? null: Double.parseDouble(s.trim()); }catch(Exception e){ return null; }
    }

    public void bookTrip(List<Route> lastResults, Scanner scanner){
        if (lastResults == null || lastResults.isEmpty()){
            System.out.println("No search results to book from. Run a search first.");
            return;
        }
		displayRoutes(lastResults);
		List<List<Route>> itineraries = groupItineraries(lastResults);
		if (itineraries.isEmpty()) {
			System.out.println("No itineraries available to book.");
			return;
		}
		System.out.print("Select itinerary index to book: ");
        String idxStr = scanner.nextLine().trim();
        int idx;
        try{
            idx = Integer.parseInt(idxStr);
        }catch(Exception e){
            System.out.println("Invalid index.");
            return;
        }
		if (idx < 0 || idx >= itineraries.size()){
            System.out.println("Index out of range.");
            return;
        }
		List<Route> selectedItinerary = itineraries.get(idx);
        // Prompt for travel date and validate against days of operation
		List<Train> trainsForItinerary = new ArrayList<>();
		for (Route r : selectedItinerary) {
			trainsForItinerary.add(trainsDB.getTrainByRoute(r.getRouteID()));
		}
        Date travelDate = null;
        while (true) {
            System.out.print("Enter travel date (yyyy-MM-dd): ");
            String dateStr = scanner.nextLine().trim();
			String dayOfWeek = null;
            Date parsed = null;
            try {
                SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                parsed = ymd.parse(dateStr);
                Date todayStart = ymd.parse(ymd.format(new Date()));
                if (parsed.before(todayStart)) {
                    System.out.println("Date is in the past. Please enter today or a future date.");
                    continue;
                }
				dayOfWeek = weekdayFromYmd(dateStr); // e.g., Mon
            } catch (Exception e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd.");
                continue;
            }
			boolean allOperate = true;
			for (Train t : trainsForItinerary) {
				if (t == null || t.getDaysOfOperation() == null || t.getDaysOfOperation().isEmpty()) {
					continue; // unknown schedule -> allow
				}
				if (!t.operatesOn(dayOfWeek)) {
					allOperate = false;
					System.out.println("One leg does not operate on " + dayOfWeek + ". Operates: " + t.getDaysOfOperation());
					break;
				}
			}
			if (allOperate) {
                travelDate = parsed;
                break;
            }
        }
        System.out.print("Number of travelers: ");
        int n;
        try{
            n = Integer.parseInt(scanner.nextLine().trim());
        }catch(Exception e){
            System.out.println("Invalid number.");
            return;
        }
		// Create trips per leg so reservations can reference each segment
		List<Trip> createdTrips = new ArrayList<>();
		for (Route r : selectedItinerary) {
			Trip tLeg = tripsDB.createTrip(r, new ArrayList<>(), travelDate);
			createdTrips.add(tLeg);
		}
        List<Reservation> resList = new ArrayList<>();
        for(int i=0;i<n;i++){
            System.out.println("-- Traveler " + (i+1) + " --");
            System.out.print("First name: ");
            String fn = scanner.nextLine().trim();
            System.out.print("Last name: ");
            String ln = scanner.nextLine().trim();
            System.out.print("Age: ");
            int age = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Gov ID (passport/state): ");
            String gid = scanner.nextLine().trim();
            // Optional: class prompt (defaults to second)
            System.out.print("Ticket class (first/second) [default: second]: ");
            String cls = scanner.nextLine().trim();
            if(cls.isEmpty()) cls = "second";
			Client savedClient = clientsDB.upsertClient(fn, ln, gid, age);
			for (int legIdx = 0; legIdx < selectedItinerary.size(); legIdx++) {
				Route r = selectedItinerary.get(legIdx);
				Trip tLeg = createdTrips.get(legIdx);
				try{
					Reservation rsv = reservationsDB.createReservation(fn, ln, age, gid, r.getRouteID(), cls.toLowerCase(), tLeg.getTripId(), savedClient.getClientId());
					resList.add(rsv);
				}catch(IllegalStateException dup){
					System.out.println("Cannot add reservation for leg " + (legIdx+1) + ": " + dup.getMessage());
					return;
				}
			}
        }
		System.out.print("Itinerary booked! Trip IDs: ");
		for (int i = 0; i < createdTrips.size(); i++) {
			System.out.print(createdTrips.get(i).getTripId());
			if (i < createdTrips.size() - 1) System.out.print(", ");
		}
		System.out.println();
		System.out.println("Tickets:");
		for (int legIdx = 0; legIdx < selectedItinerary.size(); legIdx++) {
			Route r = selectedItinerary.get(legIdx);
			Trip t = createdTrips.get(legIdx);
			for(Reservation rsv: resList){
				if (rsv.getRouteID().equals(r.getRouteID())) {
					System.out.println(" - Leg " + (legIdx+1) + " (Trip " + t.getTripId() + ", Route " + r.getRouteID() + "): "
						+ rsv.getPassengerFirstName() + " " + rsv.getPassengerLastName()
						+ " | ticket #" + rsv.getTicketNumber() + " | class: " + rsv.getTicketClass());
				}
			}
		}
    }

    public void viewTrips(Scanner scanner){
        System.out.print("Enter your last name: ");
        String ln = scanner.nextLine().trim();
        System.out.print("Enter your gov ID: ");
        String gid = scanner.nextLine().trim();
        List<Trip> mine = tripsDB.getTripsForClient(ln, gid);
        if(mine.isEmpty()){
            System.out.println("No trips found.");
            return;
        }
        Date now = new Date();
        List<Route> all = routesDB.getAllRoutes();
        Map<String, Route> byId = new HashMap<>();
        for(Route r: all){ byId.put(r.getRouteID(), r); }
    
        System.out.println("== Current/Future Trips ==");
        for(Trip t: mine){
            Route r = byId.get(t.getRouteID());
            Date depDate = t.getDepartureDate();
            if(depDate == null || !stripToDate(depDate).before(stripToDate(now))){
                printTrip(t, r);
            }
        }
        System.out.println("== Past Trips (History) ==");
        for(Trip t: mine){
            Route r = byId.get(t.getRouteID());
            Date depDate = t.getDepartureDate();
            if(depDate != null && stripToDate(depDate).before(stripToDate(now))){
                printTrip(t, r);
            }
        }
    }
    
    // book a trip by directly entering cities (mirrors Option 5 wording/flow after search)
    public void bookTripByCities(Scanner scanner){
        if (scanner == null) return;
        // Prompt for cities
        System.out.print("Departure city (blank to skip): ");
        String dep = scanner.nextLine().trim();

        String arr = null;
        while (true) {
            System.out.print("Arrival city (must provide): ");
            arr = scanner.nextLine().trim();
            if (arr.isEmpty()) {
                System.out.println("Arrival city is required.");
                continue;
            }
            boolean exists = false;
            List<Route> all = routesDB.getAllRoutes();
            for (Route r : all) {
                if (r.getArrivalCity() != null && r.getArrivalCity().equalsIgnoreCase(arr)) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                System.out.println("We do not have any arrival cities there. Please enter a different arrival city.");
                continue;
            }
            break;
        }

        // Prompt for indirect connections (same wording as search option)
        System.out.print("Include indirect connections (1- and 2-stop)? [y/N]: ");
        String yn = scanner.nextLine().trim().toLowerCase(Locale.ROOT);
        boolean includeIndirect = yn.equals("y") || yn.equals("yes");

        // Build criteria and search
        Map<String, String> criteria = new HashMap<>();
        if (!dep.isEmpty()) criteria.put("departureCity", dep);
        criteria.put("arrivalCity", arr);

        List<Route> results = searchForConnection(criteria, includeIndirect);
        if (results == null || results.isEmpty()){
            System.out.println("No routes found.");
            return;
        }

        bookTrip(results, scanner);
    }

    private void printTrip(Trip t, Route r){
        SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm", Locale.US);
        String datePart = t.getDepartureDate() != null ? dateFmt.format(t.getDepartureDate()) : "N/A";
        String depTime = (r!=null && r.getDepartureTime()!=null) ? timeFmt.format(r.getDepartureTime()) : "--:--";
        String arrTime = (r!=null && r.getArrivalTime()!=null) ? timeFmt.format(r.getArrivalTime()) : "--:--";
        System.out.println("Trip " + t.getTripId() + " | route " + t.getRouteID() + 
            (r!=null ? (" | " + r.getDepartureCity() + " -> " + r.getArrivalCity() 
            + " on " + datePart + " | " + depTime + "-" + arrTime) : ""));
        for(Reservation x: t.getReservations()){
            System.out.println("   * " + x.getPassengerFirstName() + " " + x.getPassengerLastName() +
                               " (age " + x.getPassengerAge() + ", id " + x.getPassengerGovId() + 
                               "), ticket #" + x.getTicketNumber());
        }
    }
    
    private Date stripToDate(Date d){
        try{
            SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            return ymd.parse(ymd.format(d));
        }catch(Exception e){
            return d;
        }
    }
    
    private String weekdayFromYmd(String yyyymmdd){
        if (yyyymmdd == null || yyyymmdd.trim().isEmpty()) return "";
        try{
            SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date d = ymd.parse(yyyymmdd.trim());
            return new SimpleDateFormat("EEE", Locale.US).format(d);
        }catch(Exception e){
            return "";
        }
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
        if (a.getArrivalTime() == null || b.getDepartureTime() == null) return false;
        long diff = b.getDepartureTime().getTime() - a.getArrivalTime().getTime();
        if (diff < 0) diff += TimeUnit.DAYS.toMillis(1);
        long maxLayover = TimeUnit.HOURS.toMillis(6);
        return diff >= 0 && diff <= maxLayover;
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

	// group consecutive connectable routes into itineraries (supports direct, 1-stop, 2-stop)
	private List<List<Route>> groupItineraries(List<Route> routes) {
		List<List<Route>> grouped = new ArrayList<>();
		if (routes == null || routes.isEmpty()) return grouped;
		List<Route> current = new ArrayList<>();
		Route prev = null;
		for (Route r : routes) {
			if (current.isEmpty()) {
				current.add(r);
			} else if (prev != null && canConnect(prev, r)) {
				current.add(r);
			} else {
				grouped.add(current);
				current = new ArrayList<>();
				current.add(r);
			}
			prev = r;
		}
		if (!current.isEmpty()) grouped.add(current);
		return grouped;
	}

	// Filter itineraries by days-of-operation constraints:
	// - if opDay is provided, all legs must operate on that day (unknown schedules allowed)
	// - if opDay is not provided, require that there exists at least one day Mon..Sun where all legs operate
	private List<List<Route>> filterItinerariesByOperatingDay(List<List<Route>> itineraries, String opDay) {
		List<List<Route>> out = new ArrayList<>();
		if (itineraries == null || itineraries.isEmpty()) return out;
		List<String> days = java.util.Arrays.asList("Mon","Tue","Wed","Thu","Fri","Sat","Sun");
		for (List<Route> itin : itineraries) {
			if (itin == null || itin.isEmpty()) continue;
			if (opDay != null && !opDay.trim().isEmpty()) {
				boolean ok = true;
				for (Route r : itin) {
					Train t = trainsDB.getTrainByRoute(r.getRouteID());
					if (t == null || t.getDaysOfOperation() == null || t.getDaysOfOperation().isEmpty()) {
						continue; // unknown schedule -> allow
					}
					if (!t.operatesOn(opDay.trim())) {
						ok = false;
						break;
					}
				}
				if (ok) out.add(itin);
			} else {
				boolean anyDay = false;
				for (String d : days) {
					boolean allLegs = true;
					for (Route r : itin) {
						Train t = trainsDB.getTrainByRoute(r.getRouteID());
						if (t == null || t.getDaysOfOperation() == null || t.getDaysOfOperation().isEmpty()) {
							continue; // unknown schedule -> allow
						}
						if (!t.operatesOn(d)) {
							allLegs = false;
							break;
						}
					}
					if (allLegs) { anyDay = true; break; }
				}
				if (anyDay) out.add(itin);
			}
		}
		return out;
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
