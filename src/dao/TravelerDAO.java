package dao;

import db.DatabaseConnection;
import exceptions.DataAccessException;
import models.Traveler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TravelerDAO implements GenericDAO<Traveler> {

    private final Connection connection = DatabaseConnection.getInstance().getConnection();

    private Traveler mapRow(ResultSet rs) throws SQLException {
        Traveler t = new Traveler(
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email"),
                rs.getString("phone_number")
        );
        t.setId(rs.getInt("id"));
        return t;
    }

    @Override
    public Traveler save(Traveler traveler) {
        String sql = "INSERT INTO traveler (first_name, last_name, email, phone_number) " +
                "VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, traveler.getFirstName());
            ps.setString(2, traveler.getLastName());
            ps.setString(3, traveler.getEmail());
            ps.setString(4, traveler.getPhoneNumber());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    traveler.setId(keys.getInt(1));
                }
            }
            return traveler;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to save traveler: " + e.getMessage());
        }
    }

    @Override
    public Traveler findById(int id) {
        String sql = "SELECT * FROM traveler WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find traveler: " + e.getMessage());
        }
    }

    @Override
    public List<Traveler> findAll() {
        String sql = "SELECT * FROM traveler ORDER BY id";
        List<Traveler> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list travelers: " + e.getMessage());
        }
    }

    @Override
    public void update(Traveler traveler) {
        String sql = "UPDATE traveler SET first_name = ?, last_name = ?, " +
                "email = ?, phone_number = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, traveler.getFirstName());
            ps.setString(2, traveler.getLastName());
            ps.setString(3, traveler.getEmail());
            ps.setString(4, traveler.getPhoneNumber());
            ps.setInt(5, traveler.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update traveler: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM traveler WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete traveler: " + e.getMessage());
        }
    }

    public Traveler findByEmail(String email) {
        String sql = "SELECT * FROM traveler WHERE email = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find traveler by email: " + e.getMessage());
        }
    }
}