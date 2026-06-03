package services;

import audit.AuditService;
import dao.AccommodationDAO;
import dao.ActivityDAO;
import dao.BookingDAO;
import dao.BudgetDAO;
import dao.TransportationDAO;
import exceptions.BookingException;
import interfaces.Bookable;
import models.Accommodation;
import models.Activity;
import models.Booking;
import models.Trip;
import models.transportation.Transportation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingService {
    private final BookingDAO bookingDAO = new BookingDAO();
    private final BudgetDAO budgetDAO = new BudgetDAO();
    private final AccommodationDAO accommodationDAO = new AccommodationDAO();
    private final ActivityDAO activityDAO = new ActivityDAO();
    private final TransportationDAO transportationDAO = new TransportationDAO();
    private final AuditService audit = AuditService.getInstance();

    private final Map<Integer, Booking> bookings = new HashMap<>();

    private void persistBookableState(Bookable bookable) {
        if (bookable instanceof Accommodation a) {
            accommodationDAO.update(a);
        } else if (bookable instanceof Activity a) {
            activityDAO.update(a);
        } else if (bookable instanceof Transportation t) {
            transportationDAO.update(t);
        }
    }

    private void linkBookableToTrip(Trip trip, Bookable bookable) {
        if (trip == null) return;
        if (bookable instanceof Accommodation a) {
            accommodationDAO.setTripId(a.getId(), trip.getId());
        } else if (bookable instanceof Activity a) {
            activityDAO.setTripId(a.getId(), trip.getId());
        } else if (bookable instanceof Transportation t) {
            transportationDAO.setTripId(t.getId(), trip.getId());
        }
    }

    private void unlinkBookableFromTrip(Bookable bookable) {
        if (bookable instanceof Accommodation a) {
            accommodationDAO.clearTripId(a.getId());
        } else if (bookable instanceof Activity a) {
            activityDAO.clearTripId(a.getId());
        } else if (bookable instanceof Transportation t) {
            transportationDAO.clearTripId(t.getId());
        }
    }

    public Booking createBooking(Trip trip, Bookable bookable) {
        if (!bookable.isAvailable()) {
            throw new BookingException("The item you are trying to book is not available.");
        }

        if (trip.getBudget() != null) {
            double projected = trip.getBudget().getSpentAmount() + bookable.getPrice();
            if (projected > trip.getBudget().getTotalBudget()) {
                throw new BookingException("This booking exceeds the remaining budget. Remaining: "
                        + trip.getBudget().getRemainingBudget() + " lei, item costs: " + bookable.getPrice() + " lei.");
            }
        }

        try {
            bookable.book();
        } catch (Exception e) {
            throw new BookingException(e.getMessage());
        }

        Booking booking = new Booking(trip, bookable);
        booking.confirm();
        bookingDAO.save(booking);
        persistBookableState(bookable);
        linkBookableToTrip(trip, bookable);

        if (trip.getBudget() != null) {
            trip.getBudget().addExpense(bookable.getPrice());
            budgetDAO.update(trip.getBudget());
        }

        bookings.put(booking.getBookingId(), booking);
        audit.log("CREATE_BOOKING");
        return booking;
    }

    public void cancelBooking(int bookingId) {
        Booking booking = bookingDAO.findById(bookingId);
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
        bookingDAO.update(booking);
        persistBookableState(booking.getBookable());
        unlinkBookableFromTrip(booking.getBookable());

        if (booking.getTrip() != null && booking.getTrip().getBudget() != null) {
            booking.getTrip().getBudget().addExpense(-booking.getTotalPrice());
            budgetDAO.update(booking.getTrip().getBudget());
        }

        bookings.put(booking.getBookingId(), booking);
        audit.log("CANCEL_BOOKING");
    }

    public Booking findById(int bookingId) {
        Booking booking = bookingDAO.findById(bookingId);
        if (booking == null) {
            throw new BookingException("Booking with ID " + bookingId + " not found.");
        }
        return booking;
    }

    public List<Booking> getAllBookings() {
        List<Booking> all = bookingDAO.findAll();
        bookings.clear();
        for (Booking b : all) bookings.put(b.getBookingId(), b);
        return all;
    }

    public List<Booking> getBookingsForTrip(Trip trip) {
        return bookingDAO.findByTripId(trip.getId());
    }
}