package com.SOEN342.railway_network_system;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.SOEN342.railway_network_system.controller.Console;
import com.SOEN342.railway_network_system.model.Route;

@SpringBootApplication
public class RailwayNetworkSystemApplication implements CommandLineRunner {

    @Autowired
    private Console console;

    private List<Route> lastResults;

    public static void main(String[] args) {
        SpringApplication.run(RailwayNetworkSystemApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                printMenu();
                String choice = scanner.nextLine().trim();
                switch (choice) {
                    case "1":
                        Map<String, String> criteria = promptCriteria(scanner);
                        boolean includeIndirect = promptYesNo(scanner, "Include indirect connections (1- and 2-stop)? [y/N]: ");
                        lastResults = console.searchForConnection(criteria, includeIndirect);
                        console.displayRoutes(lastResults);
                        break;
                    case "2":
                        if (ensureResults()) {
                            lastResults = console.sortRoutes(lastResults, "duration");
                            console.displayRoutes(lastResults);
                        }
                        break;
                    case "3":
                        if (ensureResults()) {
                            lastResults = console.sortRoutes(lastResults, "price");
                            console.displayRoutes(lastResults);
                        }
                        break;
                    case "4":
                        if (ensureResults()) {
                            console.displayRoutes(lastResults);
                        }
                        break;
                    case "5":
                        if (ensureResults()) {
                            console.bookTrip(lastResults, scanner);
                        }
                        break;
                    case "6":
                        console.viewTrips(scanner);
                        break;
                    case "q":
                    case "Q":
                    case "7":
                        running = false;
						System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("==== Railway Network CLI ====");
        System.out.println("1) Search for connection");
        System.out.println("2) Sort last results by trip duration");
        System.out.println("3) Sort last results by price");
        System.out.println("4) Show last results");
        System.out.println("5) Book a trip from last search results");
        System.out.println("6) View my trips");
        System.out.println("7) Quit (or press 'q')");
        System.out.print("Select: ");
    }

    private Map<String, String> promptCriteria(Scanner scanner) {
        Map<String, String> map = new HashMap<>();
        System.out.print("Departure city (blank to skip): ");
        String dep = scanner.nextLine().trim();
        if (!dep.isEmpty()) map.put("departureCity", dep);
        System.out.print("Arrival city (blank to skip): ");
        String arr = scanner.nextLine().trim();
        if (!arr.isEmpty()) map.put("arrivalCity", arr);
        System.out.print("Departure time HH:mm (blank to skip): ");
        String depTime = scanner.nextLine().trim();
        if (!depTime.isEmpty()) map.put("departureTime", depTime);
        System.out.print("Arrival time HH:mm (blank to skip): ");
        String arrTime = scanner.nextLine().trim();
        if (!arrTime.isEmpty()) map.put("arrivalTime", arrTime);
        System.out.print("Duration HH:mm (blank to skip): ");
        String dur = scanner.nextLine().trim();
        if (!dur.isEmpty()) map.put("totalDuration", dur);
        System.out.print("Train type (blank to skip): ");
        String type = scanner.nextLine().trim();
        if (!type.isEmpty()) map.put("trainType", type);
        System.out.print("Operating day (Mon/Tue/... or Daily) (blank to skip): ");
        String day = scanner.nextLine().trim();
        if (!day.isEmpty()) map.put("operatingDay", day);
        System.out.print("Max first class price (blank to skip): ");
        String maxFirst = scanner.nextLine().trim();
        if (!maxFirst.isEmpty()) map.put("maxFirstPrice", maxFirst);
        System.out.print("Max second class price (blank to skip): ");
        String maxSecond = scanner.nextLine().trim();
        if (!maxSecond.isEmpty()) map.put("maxSecondPrice", maxSecond);
        return map;
    }

    private boolean promptYesNo(Scanner scanner, String prompt) {
        System.out.print(prompt);
        String line = scanner.nextLine().trim().toLowerCase();
        return line.equals("y") || line.equals("yes");
    }

    private boolean ensureResults() {
        if (lastResults == null || lastResults.isEmpty()) {
            System.out.println("No results yet. Choose option 1 to search first.");
            return false;
        }
        return true;
    }
}
