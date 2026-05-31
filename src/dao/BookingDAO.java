package dao;

import db.DatabaseConnection;
import exceptions.DataAccessException;
import interfaces.Bookable;
import models.Accommodation;
import models.Activity;
import models.Booking;
import models.Trip;
import models.transportation.Transportation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO implements GenericDAO<Booking> {

    private final Connection connection = DatabaseConnection.getInstance().getConnection();

    private final TripDAO tripDAO = new TripDAO();
    private final AccommodationDAO accommodationDAO = new AccommodationDAO();
    private final ActivityDAO activityDAO = new ActivityDAO();
    private final TransportationDAO transportationDAO = new TransportationDAO();

    private String typeOf(Bookable bookable) {
        if (bookable instanceof Accommodation) return "Accommodation";
        if (bookable instanceof Activity) return "Activity";
        if (bookable instanceof Transportation) return "Transportation";
        throw new DataAccessException("Unknown bookable type: " + bookable.getClass().getName());
    }

    private int idOf(Bookable bookable) {
        if (bookable instanceof Accommodation a) return a.getId();
        if (bookable instanceof Activity a) return a.getId();
        if (bookable instanceof Transportation t) return t.getId();
        throw new DataAccessException("Unknown bookable type: " + bookable.getClass().getName());
    }

    private Bookable loadBookable(String type, int id) {
        return switch (type) {
            case "Accommodation" -> accommodationDAO.findById(id);
            case "Activity" -> activityDAO.findById(id);
            case "Transportation" -> transportationDAO.findById(id);
            default -> throw new DataAccessException("Unknown bookable_type: " + type);
        };
    }

    private Booking mapRow(ResultSet rs) throws SQLException {
        int tripId = rs.getInt("trip_id");
        Trip trip = (tripId != 0) ? tripDAO.findById(tripId) : null;

        String bookableType = rs.getString("bookable_type");
        int bookableId = rs.getInt("bookable_id");
        Bookable bookable = loadBookable(bookableType, bookableId);

        Booking booking = new Booking(trip, bookable);
        booking.setBookingId(rs.getInt("id"));
        booking.setStatus(Booking.Status.valueOf(rs.getString("status")));
        booking.setTotalPrice(rs.getDouble("total_price"));
        Timestamp ts = rs.getTimestamp("booking_date");
        if (ts != null) booking.setBookingDate(ts.toLocalDateTime());
        return booking;
    }

    @Override
    public Booking save(Booking booking) {
        Bookable bookable = booking.getBookable();

        String sql = "INSERT INTO booking " +
                "(trip_id, bookable_type, bookable_id, booking_date, status, total_price) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (booking.getTrip() != null) ps.setInt(1, booking.getTrip().getId());
            else ps.setNull(1, java.sql.Types.INTEGER);
            ps.setString(2, typeOf(bookable));
            ps.setInt(3, idOf(bookable));
            ps.setTimestamp(4, Timestamp.valueOf(booking.getBookingDate()));
            ps.setString(5, booking.getStatus().name());
            ps.setDouble(6, booking.getTotalPrice());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    booking.setBookingId(keys.getInt(1));
                }
            }
            return booking;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to save booking: " + e.getMessage());
        }
    }

    @Override
    public Booking findById(int id) {
        String sql = "SELECT * FROM booking WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find booking: " + e.getMessage());
        }
    }

    @Override
    public List<Booking> findAll() {
        String sql = "SELECT * FROM booking ORDER BY id";
        List<Booking> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list bookings: " + e.getMessage());
        }
    }

    @Override
    public void update(Booking booking) {
        String sql = "UPDATE booking SET status = ?, total_price = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, booking.getStatus().name());
            ps.setDouble(2, booking.getTotalPrice());
            ps.setInt(3, booking.getBookingId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update booking: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM booking WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete booking: " + e.getMessage());
        }
    }

    public List<Booking> findByTripId(int tripId) {
        String sql = "SELECT * FROM booking WHERE trip_id = ? ORDER BY id";
        List<Booking> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, tripId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
            return result;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find bookings by trip: " + e.getMessage());
        }
    }
}