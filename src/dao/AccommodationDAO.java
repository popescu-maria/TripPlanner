package dao;

import db.DatabaseConnection;
import exceptions.DataAccessException;
import models.Accommodation;
import models.Destination;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AccommodationDAO implements GenericDAO<Accommodation> {

    private final Connection connection = DatabaseConnection.getInstance().getConnection();
    private final DestinationDAO destinationDAO = new DestinationDAO();

    private Accommodation mapRow(ResultSet rs) throws SQLException {
        int destinationId = rs.getInt("destination_id");
        Destination destination =
                (destinationId != 0) ? destinationDAO.findById(destinationId) : null;

        Accommodation a = new Accommodation.Builder(rs.getString("name"))
                .type(rs.getString("type"))
                .destination(destination)
                .pricePerNight(rs.getDouble("price_per_night"))
                .numberOfNights(rs.getInt("number_of_nights"))
                .build();

        a.setId(rs.getInt("id"));
        a.setAvailable(rs.getBoolean("available"));
        return a;
    }

    private Integer ensureDestination(Accommodation a) {
        Destination d = a.getDestination();
        if (d == null) return null;
        if (d.getId() == 0 || destinationDAO.findById(d.getId()) == null) {
            destinationDAO.save(d);
        }
        return d.getId();
    }

    @Override
    public Accommodation save(Accommodation accommodation) {
        Integer destinationId = ensureDestination(accommodation);

        String sql = "INSERT INTO accommodation " +
                "(name, type, destination_id, price_per_night, number_of_nights, available) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, accommodation.getName());
            ps.setString(2, accommodation.getType());
            if (destinationId != null) ps.setInt(3, destinationId);
            else ps.setNull(3, java.sql.Types.INTEGER);
            ps.setDouble(4, accommodation.getPricePerNight());
            ps.setInt(5, accommodation.getNumberOfNights());
            ps.setBoolean(6, accommodation.isAvailable());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    accommodation.setId(keys.getInt(1));
                }
            }
            return accommodation;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to save accommodation: " + e.getMessage());
        }
    }

    @Override
    public Accommodation findById(int id) {
        String sql = "SELECT * FROM accommodation WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find accommodation: " + e.getMessage());
        }
    }

    @Override
    public List<Accommodation> findAll() {
        String sql = "SELECT * FROM accommodation ORDER BY id";
        List<Accommodation> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list accommodations: " + e.getMessage());
        }
    }

    @Override
    public void update(Accommodation accommodation) {
        Integer destinationId = ensureDestination(accommodation);

        String sql = "UPDATE accommodation SET name = ?, type = ?, destination_id = ?, " +
                "price_per_night = ?, number_of_nights = ?, available = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, accommodation.getName());
            ps.setString(2, accommodation.getType());
            if (destinationId != null) ps.setInt(3, destinationId);
            else ps.setNull(3, java.sql.Types.INTEGER);
            ps.setDouble(4, accommodation.getPricePerNight());
            ps.setInt(5, accommodation.getNumberOfNights());
            ps.setBoolean(6, accommodation.isAvailable());
            ps.setInt(7, accommodation.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update accommodation: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM accommodation WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete accommodation: " + e.getMessage());
        }
    }

    public void setTripId(int itemId, int tripId) {
        String sql = "UPDATE accommodation SET trip_id = ? WHERE id = ?";
        try (java.sql.PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, tripId);
            ps.setInt(2, itemId);
            ps.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new exceptions.DataAccessException("Failed to set trip_id: " + e.getMessage());
        }
    }

    public void clearTripId(int itemId) {
        String sql = "UPDATE accommodation SET trip_id = NULL WHERE id = ?";
        try (java.sql.PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            ps.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new exceptions.DataAccessException("Failed to clear trip_id: " + e.getMessage());
        }
    }

}