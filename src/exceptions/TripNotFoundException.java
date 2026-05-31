package exceptions;

public class TripNotFoundException extends RuntimeException {
    public TripNotFoundException(int tripId) {
        super("Trip with ID '" + tripId + "' was not found.");
    }
}