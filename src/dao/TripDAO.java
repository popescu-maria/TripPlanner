package dao;

import db.DatabaseConnection;
import exceptions.DataAccessException;
import models.Budget;
import models.Traveler;
import models.Trip;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TripDAO implements GenericDAO<Trip> {

    private final Connection connection = DatabaseConnection.getInstance().getConnection();

    private final TravelerDAO travelerDAO = new TravelerDAO();
    private final BudgetDAO budgetDAO = new BudgetDAO();
    private final TransportationDAO transportationDAO = new TransportationDAO();
    private final AccommodationDAO accommodationDAO = new AccommodationDAO();
    private final ActivityDAO activityDAO = new ActivityDAO();
    private final DestinationDAO destinationDAO = new DestinationDAO();

    private Trip mapRow(ResultSet rs) throws SQLException {
        int travelerId = rs.getInt("traveler_id");
        int budgetId = rs.getInt("budget_id");

        Traveler traveler = (travelerId != 0) ? travelerDAO.findById(travelerId) : null;
        Budget budget = (budgetId != 0) ? budgetDAO.findById(budgetId) : null;

        Date start = rs.getDate("start_date");
        Date end = rs.getDate("end_date");

        Trip trip = new Trip.Builder(rs.getString("name"))
                .startDate(start != null ? start.toLocalDate() : null)
                .endDate(end != null ? end.toLocalDate() : null)
                .traveler(traveler)
                .budget(budget)
                .build();

        trip.setId(rs.getInt("id"));

        loadRelated(trip, trip.getId());

        return trip;
    }

    private void loadRelated(Trip trip, int tripId) {
        String tSql = "SELECT id FROM transportation WHERE trip_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(tSql)) {
            ps.setInt(1, tripId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    var item = transportationDAO.findById(rs.getInt("id"));
                    if (item != null) trip.addTransportation(item);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load transportations: " + e.getMessage());
        }

        String aSql = "SELECT id FROM activity WHERE trip_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(aSql)) {
            ps.setInt(1, tripId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    var item = activityDAO.findById(rs.getInt("id"));
                    if (item != null) trip.addActivity(item);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load activities: " + e.getMessage());
        }

        String acSql = "SELECT id FROM accommodation WHERE trip_id = ? ORDER BY id DESC LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(acSql)) {
            ps.setInt(1, tripId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    var item = accommodationDAO.findById(rs.getInt("id"));
                    if (item != null) trip.setAccommodation(item);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load accommodation: " + e.getMessage());
        }

        String dSql = "SELECT destination_id FROM trip_destination WHERE trip_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(dSql)) {
            ps.setInt(1, tripId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    var d = destinationDAO.findById(rs.getInt("destination_id"));
                    if (d != null) trip.getDestinations().add(d);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load destinations: " + e.getMessage());
        }
    }

    @Override
    public Trip save(Trip trip) {
        if (trip.getBudget() != null && trip.getBudget().getId() == 0) {
            budgetDAO.save(trip.getBudget());
        } else if (trip.getBudget() != null && budgetDAO.findById(trip.getBudget().getId()) == null) {
            budgetDAO.save(trip.getBudget());
        }

        String sql = "INSERT INTO trip (name, start_date, end_date, traveler_id, budget_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, trip.getName());
            ps.setDate(2, trip.getStartDate() != null ? Date.valueOf(trip.getStartDate()) : null);
            ps.setDate(3, trip.getEndDate() != null ? Date.valueOf(trip.getEndDate()) : null);

            if (trip.getTraveler() != null) ps.setInt(4, trip.getTraveler().getId());
            else ps.setNull(4, java.sql.Types.INTEGER);

            if (trip.getBudget() != null) ps.setInt(5, trip.getBudget().getId());
            else ps.setNull(5, java.sql.Types.INTEGER);

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    trip.setId(keys.getInt(1));
                }
            }

            if (trip.getDestinations() != null) {
                for (var d : trip.getDestinations()) {
                    if (d.getId() == 0 || destinationDAO.findById(d.getId()) == null) {
                        destinationDAO.save(d);
                    }
                    linkDestination(trip.getId(), d.getId());
                }
            }
            return trip;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to save trip: " + e.getMessage());
        }
    }

    @Override
    public Trip findById(int id) {
        String sql = "SELECT * FROM trip WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find trip: " + e.getMessage());
        }
    }

    @Override
    public List<Trip> findAll() {
        String sql = "SELECT * FROM trip ORDER BY start_date";
        List<Trip> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list trips: " + e.getMessage());
        }
    }

    @Override
    public void update(Trip trip) {
        String sql = "UPDATE trip SET name = ?, start_date = ?, end_date = ?, " +
                "traveler_id = ?, budget_id = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, trip.getName());
            ps.setDate(2, trip.getStartDate() != null ? Date.valueOf(trip.getStartDate()) : null);
            ps.setDate(3, trip.getEndDate() != null ? Date.valueOf(trip.getEndDate()) : null);

            if (trip.getTraveler() != null) ps.setInt(4, trip.getTraveler().getId());
            else ps.setNull(4, java.sql.Types.INTEGER);

            if (trip.getBudget() != null) ps.setInt(5, trip.getBudget().getId());
            else ps.setNull(5, java.sql.Types.INTEGER);

            ps.setInt(6, trip.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update trip: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM trip WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete trip: " + e.getMessage());
        }
    }

    public List<Trip> findByTravelerId(int travelerId) {
        String sql = "SELECT * FROM trip WHERE traveler_id = ? ORDER BY start_date";
        List<Trip> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, travelerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
            return result;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find trips by traveler: " + e.getMessage());
        }
    }

    public void linkDestination(int tripId, int destinationId) {
        String sql = "INSERT INTO trip_destination (trip_id, destination_id) VALUES (?, ?) " +
                "ON CONFLICT DO NOTHING";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, tripId);
            ps.setInt(2, destinationId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to link destination: " + e.getMessage());
        }
    }

}