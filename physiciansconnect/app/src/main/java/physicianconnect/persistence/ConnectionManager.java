package physicianconnect.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
    private static Connection connection;

    public static void initialize(String dbFilePath) {
        if (connection != null)
            return;

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFilePath);

            try (var stmt = connection.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize DB connection", e);
        }
    }

    public static Connection get() {
        if (connection == null) {
            throw new IllegalStateException("Connection not initialized");
        }
        return connection;
    }

    public static void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException("Failed to close DB connection", e);
            } finally {
                connection = null;
            }
        }
    }

    public static void reset() {
        close();
    }

    public static boolean isInitialized() {
        return connection != null;
    }
}
