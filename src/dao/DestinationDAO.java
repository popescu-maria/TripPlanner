package dao;

import db.DatabaseConnection;
import exceptions.DataAccessException;
import models.Destination;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DestinationDAO implements GenericDAO<Destination> {

    private final Connection connection = DatabaseConnection.getInstance().getConnection();

    private Destination mapRow(ResultSet rs) throws SQLException {
        Destination d = new Destination(
                rs.getString("city"),
                rs.getString("country")
        );
        d.setId(rs.getInt("id"));
        return d;
    }

    @Override
    public Destination save(Destination destination) {
        String sql = "INSERT INTO destination (city, country) VALUES (?, ?)";
        try (PreparedStatement ps =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, destination.getCity());
            ps.setString(2, destination.getCountry());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    destination.setId(keys.getInt(1));
                }
            }
            return destination;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to save destination: " + e.getMessage());
        }
    }

    @Override
    public Destination findById(int id) {
        String sql = "SELECT * FROM destination WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find destination: " + e.getMessage());
        }
    }

    @Override
    public List<Destination> findAll() {
        String sql = "SELECT * FROM destination ORDER BY id";
        List<Destination> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list destinations: " + e.getMessage());
        }
    }

    @Override
    public void update(Destination destination) {
        String sql = "UPDATE destination SET city = ?, country = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, destination.getCity());
            ps.setString(2, destination.getCountry());
            ps.setInt(3, destination.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update destination: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM destination WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete destination: " + e.getMessage());
        }
    }

    public List<Destination> findByCity(String city) {
        String sql = "SELECT * FROM destination WHERE LOWER(city) = LOWER(?)";
        List<Destination> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, city);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
            return result;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find destination by city: " + e.getMessage());
        }
    }
}