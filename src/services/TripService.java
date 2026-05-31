package services;

import dao.TripDAO;
import exceptions.TripNotFoundException;
import models.Traveler;
import models.Trip;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class TripService {
    private final TripDAO tripDAO = new TripDAO();

    public Trip createTrip(Trip trip) {
        return tripDAO.save(trip);
    }

    public Trip findById(int id) {
        Trip trip = tripDAO.findById(id);
        if (trip == null) {
            throw new TripNotFoundException(id);
        }
        return trip;
    }

    public TreeSet<Trip> getAllTrips() {
        return new TreeSet<>(tripDAO.findAll());
    }

    public List<Trip> getTripsForTraveler(Traveler traveler) {
        return tripDAO.findByTravelerId(traveler.getId());
    }

    public void updateTripDates(int id, LocalDate startDate, LocalDate endDate) {
        Trip trip = findById(id);
        trip.setStartDate(startDate);
        trip.setEndDate(endDate);
        tripDAO.update(trip);
    }

    public void deleteTrip(int id) {
        findById(id);   // throws TripNotFoundException if missing
        tripDAO.delete(id);
    }

    public List<Trip> searchByDestination(String city) {
        List<Trip> result = new ArrayList<>();
        for (Trip t : tripDAO.findAll()) {
            if (t.getDestinations() != null) {
                for (var d : t.getDestinations()) {
                    if (d.getCity().equalsIgnoreCase(city)) {
                        result.add(t);
                        break;
                    }
                }
            }
        }
        return result;
    }

    public List<Trip> searchByDateRange(LocalDate from, LocalDate to) {
        List<Trip> result = new ArrayList<>();
        for (Trip t : tripDAO.findAll()) {
            if (t.getStartDate() != null && t.getEndDate() != null
                    && !t.getStartDate().isBefore(from) && !t.getEndDate().isAfter(to)) {
                result.add(t);
            }
        }
        return result;
    }

    public double calculateTotalCost(int tripId) {
        return findById(tripId).calculateTotalCost();
    }

    public String generateSummary(int tripId) {
        Trip trip = findById(tripId);
        StringBuilder sb = new StringBuilder();
        sb.append("=== Trip Summary ===\n");
        sb.append("Name: ").append(trip.getName()).append("\n");
        sb.append("Dates: ").append(trip.getStartDate()).append(" → ").append(trip.getEndDate()).append("\n");
        sb.append("Destinations: ").append(trip.getDestinations()).append("\n");
        sb.append("Transportations: ").append(trip.getTransportations()).append("\n");
        sb.append("Accommodation: ").append(trip.getAccommodation()).append("\n");
        sb.append("Activities: ").append(trip.getActivities()).append("\n");
        sb.append("Total Cost: ").append(trip.calculateTotalCost()).append(" lei\n");
        if (trip.getBudget() != null) {
            sb.append("Budget: ").append(trip.getBudget()).append("\n");
        }
        return sb.toString();
    }
}