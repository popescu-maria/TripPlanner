package app;

import exceptions.BookingException;
import exceptions.TripNotFoundException;
import models.*;
import models.transportation.*;
import service.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class App {
    private final TripService tripService = new TripService();
    private final TravelerService travelerService = new TravelerService();
    private final BookingService bookingService = new BookingService();
    private final Scanner scanner = new Scanner(System.in);

    public void start() {
        System.out.println("Welcome to Trip Planner!");
        boolean running = true;
        while (running) {
            printMainMenu();
            String input = scanner.nextLine().trim();
            switch (input) {
                case "1" -> handleTravelerMenu();
                case "2" -> handleDestinationMenu();
                case "3" -> handleTransportationMenu();
                case "4" -> handleAccommodationMenu();
                case "5" -> handleActivityMenu();
                case "6" -> handleTripMenu();
                case "7" -> handleBookingMenu();
                case "0" -> {
                    System.out.println("Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    // ==================== MENUS ====================

    private void printMainMenu() {
        System.out.println("\n===== MAIN MENU =====");
        System.out.println("1 - Manage Travelers");
        System.out.println("2 - Manage Destinations");
        System.out.println("3 - Manage Transportation");
        System.out.println("4 - Manage Accommodation");
        System.out.println("5 - Manage Activities");
        System.out.println("6 - Manage Trips");
        System.out.println("7 - Manage Bookings");
        System.out.println("0 - Exit");
        System.out.print("Choose: ");
    }

    // ==================== TRAVELER ====================

    private void handleTravelerMenu() {
        System.out.println("\n--- Traveler Menu ---");
        System.out.println("1 - Add traveler");
        System.out.println("2 - List all travelers");
        System.out.println("3 - Find traveler by ID");
        System.out.println("4 - Delete traveler");
        System.out.print("Choose: ");
        switch (scanner.nextLine().trim()) {
            case "1" -> addTraveler();
            case "2" -> listTravelers();
            case "3" -> findTraveler();
            case "4" -> deleteTraveler();
            default -> System.out.println("Invalid option.");
        }
    }

    private void addTraveler() {
        System.out.println("\n-- Add Traveler --");
        System.out.print("First name: ");
        String firstName = scanner.nextLine().trim();
        System.out.print("Last name: ");
        String lastName = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Phone number: ");
        String phone = scanner.nextLine().trim();
        Traveler traveler = new Traveler(firstName, lastName, email, phone);
        travelerService.registerTraveler(traveler);
        System.out.println("Traveler added: " + traveler);
    }

    private void listTravelers() {
        List<Traveler> travelers = travelerService.getAllTravelers();
        if (travelers.isEmpty()) {
            System.out.println("No travelers registered.");
            return;
        }
        System.out.println("\n-- All Travelers --");
        for (Traveler t : travelers) System.out.println(t);
    }

    private void findTraveler() {
        System.out.print("Enter traveler ID: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            Traveler t = travelerService.findById(id);
            if (t == null) System.out.println("Traveler not found.");
            else System.out.println(t);
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
        }
    }

    private void deleteTraveler() {
        System.out.print("Enter traveler ID to delete: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            travelerService.deleteTraveler(id);
            System.out.println("Traveler deleted.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
        }
    }

    // ==================== DESTINATION ====================

    private void handleDestinationMenu() {
        System.out.println("\n-- Add Destination --");
        System.out.println("Note: destinations are added directly to trips.");
        System.out.println("Go to Manage Trips to add a destination to a trip.");
    }

    // ==================== TRANSPORTATION ====================

    private void handleTransportationMenu() {
        System.out.println("\n--- Transportation Menu ---");
        System.out.println("1 - Create flight");
        System.out.println("2 - Create train");
        System.out.println("3 - Create bus");
        System.out.print("Choose: ");
        switch (scanner.nextLine().trim()) {
            case "1" -> createFlight();
            case "2" -> createTrain();
            case "3" -> createBus();
            default -> System.out.println("Invalid option.");
        }
    }

    private void createFlight() {
        System.out.println("\n-- Create Flight --");
        System.out.print("Provider (airline name): ");
        String provider = scanner.nextLine().trim();
        System.out.print("Origin: ");
        String origin = scanner.nextLine().trim();
        System.out.print("Destination: ");
        String destination = scanner.nextLine().trim();
        System.out.print("Departure time (e.g. 08:00): ");
        String departure = scanner.nextLine().trim();
        System.out.print("Arrival time (e.g. 11:00): ");
        String arrival = scanner.nextLine().trim();
        System.out.print("Price (lei): ");
        double price = parseDouble();
        System.out.print("Flight number: ");
        String flightNumber = scanner.nextLine().trim();
        System.out.print("Class (economy/business/first): ");
        String airlineClass = scanner.nextLine().trim();

        FlightTransportation flight = new FlightTransportation.Builder(provider)
                .origin(origin)
                .destination(destination)
                .departureTime(departure)
                .arrivalTime(arrival)
                .price(price)
                .flightNumber(flightNumber)
                .airlineClass(airlineClass)
                .build();

        System.out.println("Flight created: " + flight);
        System.out.println("Flight ID: " + flight.getId() + " (use this to add it to a trip)");
    }

    private void createTrain() {
        System.out.println("\n-- Create Train --");
        System.out.print("Provider: ");
        String provider = scanner.nextLine().trim();
        System.out.print("Origin: ");
        String origin = scanner.nextLine().trim();
        System.out.print("Destination: ");
        String destination = scanner.nextLine().trim();
        System.out.print("Departure time: ");
        String departure = scanner.nextLine().trim();
        System.out.print("Arrival time: ");
        String arrival = scanner.nextLine().trim();
        System.out.print("Price (lei): ");
        double price = parseDouble();
        System.out.print("Train number: ");
        String trainNumber = scanner.nextLine().trim();
        System.out.print("Wagon number: ");
        int wagonNumber = parseInt();

        TrainTransportation train = new TrainTransportation.Builder(provider)
                .origin(origin)
                .destination(destination)
                .departureTime(departure)
                .arrivalTime(arrival)
                .price(price)
                .trainNumber(trainNumber)
                .wagonNumber(wagonNumber)
                .build();

        System.out.println("Train created: " + train);
        System.out.println("Train ID: " + train.getId() + " (use this to add it to a trip)");
    }

    private void createBus() {
        System.out.println("\n-- Create Bus --");
        System.out.print("Provider: ");
        String provider = scanner.nextLine().trim();
        System.out.print("Origin: ");
        String origin = scanner.nextLine().trim();
        System.out.print("Destination: ");
        String destination = scanner.nextLine().trim();
        System.out.print("Departure time: ");
        String departure = scanner.nextLine().trim();
        System.out.print("Arrival time: ");
        String arrival = scanner.nextLine().trim();
        System.out.print("Price (lei): ");
        double price = parseDouble();
        System.out.print("Bus number: ");
        String busNumber = scanner.nextLine().trim();

        BusTransportation bus = new BusTransportation.Builder(provider)
                .origin(origin)
                .destination(destination)
                .departureTime(departure)
                .arrivalTime(arrival)
                .price(price)
                .busNumber(busNumber)
                .build();

        System.out.println("Bus created: " + bus);
        System.out.println("Bus ID: " + bus.getId() + " (use this to add it to a trip)");
    }

    // ==================== ACCOMMODATION ====================

    private void handleAccommodationMenu() {
        System.out.println("\n-- Create Accommodation --");
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Type (hotel/hostel/airbnb): ");
        String type = scanner.nextLine().trim();
        System.out.print("City: ");
        String city = scanner.nextLine().trim();
        System.out.print("Country: ");
        String country = scanner.nextLine().trim();
        System.out.print("Price per night (lei): ");
        double pricePerNight = parseDouble();
        System.out.print("Number of nights: ");
        int nights = parseInt();

        Accommodation accommodation = new Accommodation.Builder(name)
                .type(type)
                .destination(new Destination(city, country))
                .pricePerNight(pricePerNight)
                .numberOfNights(nights)
                .build();

        System.out.println("Accommodation created: " + accommodation);
        System.out.println("Accommodation ID: " + accommodation.getId() + " (use this to add it to a trip)");
    }

    // ==================== ACTIVITY ====================

    private void handleActivityMenu() {
        System.out.println("\n-- Create Activity --");
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("City: ");
        String city = scanner.nextLine().trim();
        System.out.print("Country: ");
        String country = scanner.nextLine().trim();
        System.out.print("Price (lei): ");
        double price = parseDouble();
        System.out.print("Max participants: ");
        int maxParticipants = parseInt();

        Activity activity = new Activity.Builder(name)
                .destination(new Destination(city, country))
                .price(price)
                .maxParticipants(maxParticipants)
                .build();

        System.out.println("Activity created: " + activity);
        System.out.println("Activity ID: " + activity.getId() + " (use this to add it to a trip)");
    }

    // ==================== TRIP ====================

    private void handleTripMenu() {
        System.out.println("\n--- Trip Menu ---");
        System.out.println("1 - Create trip");
        System.out.println("2 - List all trips");
        System.out.println("3 - View trip summary");
        System.out.println("4 - Add traveler to trip");
        System.out.println("5 - Search trips by destination");
        System.out.println("6 - Delete trip");
        System.out.print("Choose: ");
        switch (scanner.nextLine().trim()) {
            case "1" -> createTrip();
            case "2" -> listTrips();
            case "3" -> viewTripSummary();
            case "4" -> addTravelerToTrip();
            case "5" -> searchTripsByDestination();
            case "6" -> deleteTrip();
            default -> System.out.println("Invalid option.");
        }
    }

    private void createTrip() {
        System.out.println("\n-- Create Trip --");
        System.out.print("Trip name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Start date (yyyy-mm-dd): ");
        LocalDate startDate = parseDate();
        System.out.print("End date (yyyy-mm-dd): ");
        LocalDate endDate = parseDate();
        System.out.print("Total budget (lei): ");
        double budget = parseDouble();

        Trip trip = new Trip.Builder(name)
                .startDate(startDate)
                .endDate(endDate)
                .budget(new Budget(budget))
                .build();

        tripService.createTrip(trip);
        System.out.println("Trip created! ID: " + trip.getId());
    }

    private void listTrips() {
        if (tripService.getAllTrips().isEmpty()) {
            System.out.println("No trips found.");
            return;
        }
        System.out.println("\n-- All Trips (sorted by start date) --");
        for (Trip t : tripService.getAllTrips()) System.out.println(t);
    }

    private void viewTripSummary() {
        System.out.print("Enter trip ID: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            System.out.println(tripService.generateSummary(id));
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
        } catch (TripNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private void addTravelerToTrip() {
        System.out.print("Trip ID: ");
        try {
            int tripId = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Traveler ID: ");
            int travelerId = Integer.parseInt(scanner.nextLine().trim());
            Traveler traveler = travelerService.findById(travelerId);
            if (traveler == null) {
                System.out.println("Traveler not found.");
                return;
            }
            tripService.addTraveler(tripId, traveler);
            System.out.println("Traveler added to trip.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
        } catch (TripNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private void searchTripsByDestination() {
        System.out.print("Enter city name: ");
        String city = scanner.nextLine().trim();
        List<Trip> results = tripService.searchByDestination(city);
        if (results.isEmpty()) System.out.println("No trips found for: " + city);
        else results.forEach(System.out::println);
    }

    private void deleteTrip() {
        System.out.print("Enter trip ID to delete: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            tripService.deleteTrip(id);
            System.out.println("Trip deleted.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
        } catch (TripNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    // ==================== BOOKING ====================

    private void handleBookingMenu() {
        System.out.println("\n--- Booking Menu ---");
        System.out.println("1 - List all bookings");
        System.out.println("2 - Cancel booking");
        System.out.print("Choose: ");
        switch (scanner.nextLine().trim()) {
            case "1" -> listBookings();
            case "2" -> cancelBooking();
            default -> System.out.println("Invalid option.");
        }
    }

    private void listBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        if (bookings.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }
        System.out.println("\n-- All Bookings --");
        for (Booking b : bookings) System.out.println(b);
    }

    private void cancelBooking() {
        System.out.print("Enter booking ID to cancel: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            bookingService.cancelBooking(id);
            System.out.println("Booking cancelled.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
        } catch (BookingException e) {
            System.out.println(e.getMessage());
        }
    }

    // ==================== HELPERS ====================

    private double parseDouble() {
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Invalid number, try again: ");
            }
        }
    }

    private int parseInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Invalid number, try again: ");
            }
        }
    }

    private LocalDate parseDate() {
        while (true) {
            try {
                return LocalDate.parse(scanner.nextLine().trim());
            } catch (DateTimeParseException e) {
                System.out.print("Invalid date format, use yyyy-mm-dd: ");
            }
        }
    }
}