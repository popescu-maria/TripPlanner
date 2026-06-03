package services;

import audit.AuditService;
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
    private final AuditService audit = AuditService.getInstance();

    private Trip findById(int id) {
        Trip trip = tripDAO.findById(id);
        if (trip == null) {
            throw new TripNotFoundException(id);
        }
        return trip;
    }

    public Trip createTrip(Trip trip) {
        Trip saved = tripDAO.save(trip);
        audit.log("CREATE_TRIP");
        return saved;
    }

    public TreeSet<Trip> getAllTrips() {
        audit.log("LIST_ALL_TRIPS");
        return new TreeSet<>(tripDAO.findAll());
    }

    public List<Trip> getTripsForTraveler(Traveler traveler) {
        audit.log("LIST_TRIPS_FOR_TRAVELER");
        return tripDAO.findByTravelerId(traveler.getId());
    }

    public void updateTripDates(int id, LocalDate startDate, LocalDate endDate) {
        Trip trip = findById(id);
        trip.setStartDate(startDate);
        trip.setEndDate(endDate);
        tripDAO.update(trip);
        audit.log("UPDATE_TRIP_DATES");
    }

    public void deleteTrip(int id) {
        findById(id);
        tripDAO.delete(id);
        audit.log("DELETE_TRIP");
    }

    public List<Trip> searchByDestination(String city) {
        audit.log("SEARCH_TRIP_BY_DESTINATION");
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

    public String generateSummary(int tripId) {
        audit.log("GENERATE_TRIP_SUMMARY");
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