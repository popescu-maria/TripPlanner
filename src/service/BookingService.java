package service;

import exceptions.BookingException;
import interfaces.Bookable;
import models.Booking;
import models.Trip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingService {
    private Map<Integer, Booking> bookings = new HashMap<>();

    public Booking createBooking(Trip trip, Bookable bookable) throws BookingException {
        if (!bookable.isAvailable()) {
            throw new BookingException("The item you are trying to book is not available.");
        }
        try {
            bookable.book();
        } catch (Exception e) {
            throw new BookingException(e.getMessage());
        }
        Booking booking = new Booking(trip, bookable);
        booking.confirm();
        bookings.put(booking.getBookingId(), booking);

        if (trip.getBudget() != null) {
            trip.getBudget().addExpense(bookable.getPrice());
        }

        return booking;
    }

    public void cancelBooking(int bookingId) throws BookingException {
        Booking booking = bookings.get(bookingId);
        if (booking == null) {
            throw new BookingException("Booking with ID " + bookingId + " not found.");
        }
        if (booking.getStatus() == Booking.Status.CANCELLED) {
            throw new BookingException("Booking is already cancelled.");
        }
        try {
            booking.getBookable().cancel();
        } catch (Exception e) {
            throw new BookingException(e.getMessage());
        }
        booking.cancel();

        if (booking.getTrip().getBudget() != null) {
            booking.getTrip().getBudget().addExpense(-booking.getTotalPrice());
        }
    }

    public Booking findById(int bookingId) throws BookingException {
        Booking booking = bookings.get(bookingId);
        if (booking == null) {
            throw new BookingException("Booking with ID " + bookingId + " not found.");
        }
        return booking;
    }

    public List<Booking> getAllBookings() {
        return new ArrayList<>(bookings.values());
    }

    public List<Booking> getBookingsForTrip(Trip trip) {
        List<Booking> result = new ArrayList<>();
        for (Booking b : bookings.values()) {
            if (b.getTrip().getId() == trip.getId()) {
                result.add(b);
            }
        }
        return result;
    }
}