// This is just to test some of the actions. (create a trip, see it's details,
// book a booking for that trip, calculate the budget based on that etc.)

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

    private Traveler loggedInTraveler = null;

    public void start() {
        System.out.println("Welcome to Trip Planner!");
        boolean running = true;
        while (running) {
            if (loggedInTraveler == null) {
                printAuthMenu();
                String input = scanner.nextLine().trim();
                switch (input) {
                    case "1" -> register();
                    case "2" -> login();
                    case "0" -> {
                        System.out.println("Goodbye!");
                        running = false;
                    }
                    default -> System.out.println("Invalid option. Please try again.");
                }
            } else {
                printMainMenu();
                String input = scanner.nextLine().trim();
                switch (input) {
                    case "1" -> handleTripMenu();
                    case "2" -> handleBookingMenu();
                    case "3" -> logout();
                    case "0" -> {
                        System.out.println("Goodbye!");
                        running = false;
                    }
                    default -> System.out.println("Invalid option. Please try again.");
                }
            }
        }
    }

    // ==================== AUTH ====================

    private void printAuthMenu() {
        System.out.println("\n===== TRIP PLANNER =====");
        System.out.println("1 - Register");
        System.out.println("2 - Login");
        System.out.println("0 - Exit");
        System.out.print("Choose: ");
    }

    private void printMainMenu() {
        System.out.println("\n===== MAIN MENU =====");
        System.out.println("Logged in as: " + loggedInTraveler.getFullName());
        System.out.println("1 - Manage My Trips");
        System.out.println("2 - Manage Bookings");
        System.out.println("3 - Logout");
        System.out.println("0 - Exit");
        System.out.print("Choose: ");
    }

    private void register() {
        System.out.println("\n-- Register --");
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
        System.out.println("Registered successfully! Please login.");
    }

    private void login() {
        System.out.print("\nEnter your email: ");
        String email = scanner.nextLine().trim();
        Traveler traveler = travelerService.findByEmail(email);
        if (traveler == null) {
            System.out.println("No account found with that email.");
        } else {
            loggedInTraveler = traveler;
            System.out.println("Welcome back, " + loggedInTraveler.getFullName() + "!");
        }
    }

    private void logout() {
        System.out.println("Logged out. Goodbye, " + loggedInTraveler.getFullName() + "!");
        loggedInTraveler = null;
    }

    // ==================== TRIP ====================

    private void handleTripMenu() {
        System.out.println("\n--- My Trips ---");
        System.out.println("1 - Create trip");
        System.out.println("2 - List my trips");
        System.out.println("3 - View trip summary");
        System.out.println("4 - Search trips by destination");
        System.out.println("5 - Delete trip");
        System.out.print("Choose: ");
        switch (scanner.nextLine().trim()) {
            case "1" -> createTrip();
            case "2" -> listMyTrips();
            case "3" -> viewTripSummary();
            case "4" -> searchTripsByDestination();
            case "5" -> deleteTrip();
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
                .traveler(loggedInTraveler)
                .budget(new Budget(budget))
                .build();

        tripService.createTrip(trip);
        System.out.println("Trip created! ID: " + trip.getId());
    }

    private void listMyTrips() {
        List<Trip> myTrips = tripService.getTripsForTraveler(loggedInTraveler);
        if (myTrips.isEmpty()) {
            System.out.println("You have no trips yet.");
            return;
        }
        System.out.println("\n-- My Trips (sorted by start date) --");
        for (Trip t : myTrips) System.out.println(t);
    }

    private void viewTripSummary() {
        Trip trip = selectMyTrip();
        if (trip == null) return;
        try {
            System.out.println(tripService.generateSummary(trip.getId()));
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
        Trip trip = selectMyTrip();
        if (trip == null) return;
        try {
            tripService.deleteTrip(trip.getId());
            System.out.println("Trip deleted.");
        } catch (TripNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    // ==================== BOOKING ====================

    private void handleBookingMenu() {
        Trip trip = selectMyTrip();
        if (trip == null) return;

        System.out.println("\n--- Bookings for: " + trip.getName() + " ---");
        System.out.println("1 - Book a flight");
        System.out.println("2 - Book a train");
        System.out.println("3 - Book a bus");
        System.out.println("4 - Book an accommodation");
        System.out.println("5 - Book an activity");
        System.out.println("6 - View bookings for this trip");
        System.out.println("7 - Cancel a booking");
        System.out.print("Choose: ");

        switch (scanner.nextLine().trim()) {
            case "1" -> bookFlight(trip);
            case "2" -> bookTrain(trip);
            case "3" -> bookBus(trip);
            case "4" -> bookAccommodation(trip);
            case "5" -> bookActivity(trip);
            case "6" -> listBookingsForTrip(trip);
            case "7" -> cancelBooking();
            default -> System.out.println("Invalid option.");
        }
    }

    private void bookFlight(Trip trip) {
        System.out.println("\n-- Book a Flight --");
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

        try {
            trip.addTransportation(flight);
            Booking booking = bookingService.createBooking(trip, flight);
            System.out.println("Flight booked successfully: " + booking);
        } catch (BookingException e) {
            System.out.println("Booking error: " + e.getMessage());
        }
    }

    private void bookTrain(Trip trip) {
        System.out.println("\n-- Book a Train --");
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

        try {
            trip.addTransportation(train);
            Booking booking = bookingService.createBooking(trip, train);
            System.out.println("Train booked successfully: " + booking);
        } catch (BookingException e) {
            System.out.println("Booking error: " + e.getMessage());
        }
    }

    private void bookBus(Trip trip) {
        System.out.println("\n-- Book a Bus --");
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

        try {
            trip.addTransportation(bus);
            Booking booking = bookingService.createBooking(trip, bus);
            System.out.println("Bus booked successfully: " + booking);
        } catch (BookingException e) {
            System.out.println("Booking error: " + e.getMessage());
        }
    }

    private void bookAccommodation(Trip trip) {
        System.out.println("\n-- Book an Accommodation --");
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

        try {
            trip.setAccommodation(accommodation);
            Booking booking = bookingService.createBooking(trip, accommodation);
            System.out.println("Accommodation booked successfully: " + booking);
        } catch (BookingException e) {
            System.out.println("Booking error: " + e.getMessage());
        }
    }

    private void bookActivity(Trip trip) {
        System.out.println("\n-- Book an Activity --");
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

        try {
            trip.addActivity(activity);
            Booking booking = bookingService.createBooking(trip, activity);
            System.out.println("Activity booked successfully: " + booking);
        } catch (BookingException e) {
            System.out.println("Booking error: " + e.getMessage());
        }
    }

    private void listBookingsForTrip(Trip trip) {
        List<Booking> bookings = bookingService.getBookingsForTrip(trip);
        if (bookings.isEmpty()) {
            System.out.println("No bookings for this trip.");
            return;
        }
        System.out.println("\n-- Bookings for " + trip.getName() + " --");
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

    private Trip selectMyTrip() {
        List<Trip> myTrips = tripService.getTripsForTraveler(loggedInTraveler);
        if (myTrips.isEmpty()) {
            System.out.println("You have no trips yet. Create one first.");
            return null;
        }
        System.out.println("\nYour trips:");
        for (Trip t : myTrips) System.out.println(t);
        System.out.print("Enter trip ID: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            Trip trip = myTrips.stream()
                    .filter(t -> t.getId() == id)
                    .findFirst()
                    .orElse(null);
            if (trip == null) System.out.println("Trip not found.");
            return trip;
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
            return null;
        }
    }

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