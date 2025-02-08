package nekonic.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class for managing database connections.
 * Supports both SQLite and MySQL based on plugin configuration.
 */
public class DBUtils {

    private static Connection connection;
    private static String databaseUrl;

    private DBUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Initializes the database connection based on the provided URL.
     *
     * @param dbUrl The database URL (e.g., SQLite or MySQL connection string).
     * @throws SQLException If a database access error occurs.
     */
    public static void initialize(String dbUrl) throws SQLException {
        if (connection != null) {
            throw new IllegalStateException("Database connection is already initialized.");
        }
        databaseUrl = dbUrl;
        connection = DriverManager.getConnection(databaseUrl);
    }

    /**
     * Gets the existing database connection.
     *
     * @return A {@link Connection} object.
     * @throws SQLException If the connection is not initialized or closed.
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(databaseUrl);
        }
        return connection;
    }

    /**
     * Closes the database connection if it is open.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace(); // Replace with proper logging in production
            }
        }
    }
}
