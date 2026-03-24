package exceptions;

public class TripNotFoundException extends Exception {
    public TripNotFoundException(int tripId) {
        super("Trip with ID '" + tripId + "' was not found.");
    }
}