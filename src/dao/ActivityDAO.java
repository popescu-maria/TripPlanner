package dao;

import db.DatabaseConnection;
import exceptions.DataAccessException;
import models.Activity;
import models.Destination;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ActivityDAO implements GenericDAO<Activity> {

    private final Connection connection = DatabaseConnection.getInstance().getConnection();
    private final DestinationDAO destinationDAO = new DestinationDAO();

    private Activity mapRow(ResultSet rs) throws SQLException {
        int destinationId = rs.getInt("destination_id");
        Destination destination =
                (destinationId != 0) ? destinationDAO.findById(destinationId) : null;

        Activity a = new Activity.Builder(rs.getString("name"))
                .destination(destination)
                .price(rs.getDouble("price"))
                .maxParticipants(rs.getInt("max_participants"))
                .build();

        a.setId(rs.getInt("id"));
        a.setCurrentParticipants(rs.getInt("current_participants"));
        return a;
    }

    private Integer ensureDestination(Activity a) {
        Destination d = a.getDestination();
        if (d == null) return null;
        if (d.getId() == 0 || destinationDAO.findById(d.getId()) == null) {
            destinationDAO.save(d);
        }
        return d.getId();
    }

    @Override
    public Activity save(Activity activity) {
        Integer destinationId = ensureDestination(activity);

        String sql = "INSERT INTO activity " +
                "(name, destination_id, price, max_participants, current_participants) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, activity.getName());
            if (destinationId != null) ps.setInt(2, destinationId);
            else ps.setNull(2, java.sql.Types.INTEGER);
            ps.setDouble(3, activity.getPrice());
            ps.setInt(4, activity.getMaxParticipants());
            ps.setInt(5, activity.getCurrentParticipants());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    activity.setId(keys.getInt(1));
                }
            }
            return activity;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to save activity: " + e.getMessage());
        }
    }

    @Override
    public Activity findById(int id) {
        String sql = "SELECT * FROM activity WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find activity: " + e.getMessage());
        }
    }

    @Override
    public List<Activity> findAll() {
        String sql = "SELECT * FROM activity ORDER BY id";
        List<Activity> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list activities: " + e.getMessage());
        }
    }

    @Override
    public void update(Activity activity) {
        Integer destinationId = ensureDestination(activity);

        String sql = "UPDATE activity SET name = ?, destination_id = ?, price = ?, " +
                "max_participants = ?, current_participants = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, activity.getName());
            if (destinationId != null) ps.setInt(2, destinationId);
            else ps.setNull(2, java.sql.Types.INTEGER);
            ps.setDouble(3, activity.getPrice());
            ps.setInt(4, activity.getMaxParticipants());
            ps.setInt(5, activity.getCurrentParticipants());
            ps.setInt(6, activity.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update activity: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM activity WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete activity: " + e.getMessage());
        }
    }
}