package dao;

import db.DatabaseConnection;
import exceptions.DataAccessException;
import factory.TransportationFactory;
import models.transportation.BusTransportation;
import models.transportation.FlightTransportation;
import models.transportation.TrainTransportation;
import models.transportation.Transportation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TransportationDAO implements GenericDAO<Transportation> {

    private final Connection connection = DatabaseConnection.getInstance().getConnection();

    private Transportation mapRow(ResultSet rs) throws SQLException {
        return TransportationFactory.fromResultSet(rs);
    }

    @Override
    public Transportation save(Transportation t) {
        String sql = "INSERT INTO transportation " +
                "(transport_type, provider, origin, destination, departure_time, arrival_time, " +
                " price, available, flight_number, airline_class, train_number, wagon_number, bus_number) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, t.getTransportationType());
            ps.setString(2, t.getProvider());
            ps.setString(3, t.getOrigin());
            ps.setString(4, t.getDestination());
            ps.setString(5, t.getDepartureTime());
            ps.setString(6, t.getArrivalTime());
            ps.setDouble(7, t.getPrice());
            ps.setBoolean(8, t.isAvailable());

            if (t instanceof FlightTransportation f) {
                ps.setString(9, f.getFlightNumber());
                ps.setString(10, f.getAirlineClass());
                ps.setNull(11, java.sql.Types.VARCHAR);
                ps.setNull(12, java.sql.Types.INTEGER);
                ps.setNull(13, java.sql.Types.VARCHAR);
            } else if (t instanceof TrainTransportation tr) {
                ps.setNull(9, java.sql.Types.VARCHAR);
                ps.setNull(10, java.sql.Types.VARCHAR);
                ps.setString(11, tr.getTrainNumber());
                ps.setInt(12, tr.getWagonNumber());
                ps.setNull(13, java.sql.Types.VARCHAR);
            } else if (t instanceof BusTransportation b) {
                ps.setNull(9, java.sql.Types.VARCHAR);
                ps.setNull(10, java.sql.Types.VARCHAR);
                ps.setNull(11, java.sql.Types.VARCHAR);
                ps.setNull(12, java.sql.Types.INTEGER);
                ps.setString(13, b.getBusNumber());
            } else {
                ps.setNull(9, java.sql.Types.VARCHAR);
                ps.setNull(10, java.sql.Types.VARCHAR);
                ps.setNull(11, java.sql.Types.VARCHAR);
                ps.setNull(12, java.sql.Types.INTEGER);
                ps.setNull(13, java.sql.Types.VARCHAR);
            }

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    t.setId(keys.getInt(1));
                }
            }
            return t;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to save transportation: " + e.getMessage());
        }
    }

    @Override
    public Transportation findById(int id) {
        String sql = "SELECT * FROM transportation WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find transportation: " + e.getMessage());
        }
    }

    @Override
    public List<Transportation> findAll() {
        String sql = "SELECT * FROM transportation ORDER BY id";
        List<Transportation> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list transportations: " + e.getMessage());
        }
    }

    @Override
    public void update(Transportation t) {
        String sql = "UPDATE transportation SET provider = ?, origin = ?, destination = ?, " +
                "departure_time = ?, arrival_time = ?, price = ?, available = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, t.getProvider());
            ps.setString(2, t.getOrigin());
            ps.setString(3, t.getDestination());
            ps.setString(4, t.getDepartureTime());
            ps.setString(5, t.getArrivalTime());
            ps.setDouble(6, t.getPrice());
            ps.setBoolean(7, t.isAvailable());
            ps.setInt(8, t.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update transportation: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM transportation WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete transportation: " + e.getMessage());
        }
    }
}