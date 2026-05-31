package db;

import exceptions.DataAccessException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:postgresql://localhost:5433/trip_planner";
    private static final String USER = "postgres";
    private static final String PASSWORD = "trip123";

    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        try {
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to connect to the database: " + e.getMessage());
        }
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Lost database connection: " + e.getMessage());
        }
        return connection;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to close connection: " + e.getMessage());
        }
    }
}