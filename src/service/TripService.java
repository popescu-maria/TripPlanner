package service;

import exceptions.TripNotFoundException;
import models.*;
import models.transportation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class TripService {
    private TreeSet<Trip> trips = new TreeSet<>();

    public Trip createTrip(Trip trip) {
        trips.add(trip);
        return trip;
    }

    public Trip findById(int id) throws TripNotFoundException {
        for (Trip t : trips) {
            if (t.getId() == id) return t;
        }
        throw new TripNotFoundException(id);
    }

    public TreeSet<Trip> getAllTrips() {
        return trips;
    }

    public void updateTripDates(int id, LocalDate startDate, LocalDate endDate) throws TripNotFoundException {
        Trip trip = findById(id);
        trips.remove(trip);
        trip.setStartDate(startDate);
        trip.setEndDate(endDate);
        trips.add(trip);
    }

    public void updateTripName(int id, String name) throws TripNotFoundException {
        Trip trip = findById(id);
        trip.setName(name);
    }

    public void deleteTrip(int id) throws TripNotFoundException {
        Trip trip = findById(id);
        trips.remove(trip);
    }

    public void addDestination(int tripId, Destination destination) throws TripNotFoundException {
        Trip trip = findById(tripId);
        trip.getDestinations().add(destination);
    }

    public void addTransportation(int tripId, Transportation transportation) throws TripNotFoundException {
        Trip trip = findById(tripId);
        trip.addTransportation(transportation);
    }

    public void removeTransportation(int tripId, Transportation transportation) throws TripNotFoundException {
        Trip trip = findById(tripId);
        trip.removeTransportation(transportation);
    }

    public void setAccommodation(int tripId, Accommodation accommodation) throws TripNotFoundException {
        Trip trip = findById(tripId);
        trip.setAccommodation(accommodation);
    }

    public void addActivity(int tripId, Activity activity) throws TripNotFoundException {
        Trip trip = findById(tripId);
        trip.addActivity(activity);
    }

    public void removeActivity(int tripId, Activity activity) throws TripNotFoundException {
        Trip trip = findById(tripId);
        trip.removeActivity(activity);
    }

    public void addTraveler(int tripId, Traveler traveler) throws TripNotFoundException {
        Trip trip = findById(tripId);
        trip.addTraveler(traveler);
    }

    public void removeTraveler(int tripId, Traveler traveler) throws TripNotFoundException {
        Trip trip = findById(tripId);
        trip.removeTraveler(traveler);
    }

    public List<Trip> searchByDestination(String city) {
        List<Trip> result = new ArrayList<>();
        for (Trip t : trips) {
            for (Destination d : t.getDestinations()) {
                if (d.getCity().equalsIgnoreCase(city)) {
                    result.add(t);
                    break;
                }
            }
        }
        return result;
    }

    public List<Trip> searchByDateRange(LocalDate from, LocalDate to) {
        List<Trip> result = new ArrayList<>();
        for (Trip t : trips) {
            if (!t.getStartDate().isBefore(from) && !t.getEndDate().isAfter(to)) {
                result.add(t);
            }
        }
        return result;
    }

    public double calculateTotalCost(int tripId) throws TripNotFoundException {
        return findById(tripId).calculateTotalCost();
    }

    public String generateSummary(int tripId) throws TripNotFoundException {
        Trip trip = findById(tripId);
        StringBuilder sb = new StringBuilder();
        sb.append("=== Trip Summary ===\n");
        sb.append("Name: ").append(trip.getName()).append("\n");
        sb.append("Dates: ").append(trip.getStartDate()).append(" → ").append(trip.getEndDate()).append("\n");
        sb.append("Destinations: ").append(trip.getDestinations()).append("\n");
        sb.append("Transportations: ").append(trip.getTransportations()).append("\n");
        sb.append("Accommodation: ").append(trip.getAccommodation()).append("\n");
        sb.append("Activities: ").append(trip.getActivities()).append("\n");
        sb.append("Travelers: ").append(trip.getTravelers()).append("\n");
        sb.append("Total Cost: ").append(trip.calculateTotalCost()).append(" lei\n");
        if (trip.getBudget() != null) {
            sb.append("Budget: ").append(trip.getBudget()).append("\n");
        }
        return sb.toString();
    }
}
