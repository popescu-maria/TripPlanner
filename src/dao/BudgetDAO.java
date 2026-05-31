package dao;

import db.DatabaseConnection;
import exceptions.DataAccessException;
import models.Budget;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BudgetDAO implements GenericDAO<Budget> {

    private final Connection connection = DatabaseConnection.getInstance().getConnection();

    private Budget mapRow(ResultSet rs) throws SQLException {
        Budget b = new Budget(rs.getDouble("total_budget"));
        b.setId(rs.getInt("id"));
        b.setSpentAmount(rs.getDouble("spent_amount"));
        return b;
    }

    @Override
    public Budget save(Budget budget) {
        String sql = "INSERT INTO budget (total_budget, spent_amount) VALUES (?, ?)";
        try (PreparedStatement ps =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDouble(1, budget.getTotalBudget());
            ps.setDouble(2, budget.getSpentAmount());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    budget.setId(keys.getInt(1));
                }
            }
            return budget;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to save budget: " + e.getMessage());
        }
    }

    @Override
    public Budget findById(int id) {
        String sql = "SELECT * FROM budget WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find budget: " + e.getMessage());
        }
    }

    @Override
    public List<Budget> findAll() {
        String sql = "SELECT * FROM budget ORDER BY id";
        List<Budget> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list budgets: " + e.getMessage());
        }
    }

    @Override
    public void update(Budget budget) {
        String sql = "UPDATE budget SET total_budget = ?, spent_amount = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, budget.getTotalBudget());
            ps.setDouble(2, budget.getSpentAmount());
            ps.setInt(3, budget.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update budget: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM budget WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete budget: " + e.getMessage());
        }
    }
}