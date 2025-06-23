package physicianconnect.persistence.sqlite;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;

public class SchemaInitializerTest {

    private void assertTableExists(Connection conn, String tableName) throws Exception {
        ResultSet rs = conn.createStatement().executeQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='" + tableName + "'");
        assertTrue(rs.next(), "Table '" + tableName + "' should exist");
    }

    @Test
    public void testSchemaInitializerCreatesAllTables() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        SchemaInitializer.initializeSchema(conn);

        assertTableExists(conn, "physicians");
        assertTableExists(conn, "appointments");
        assertTableExists(conn, "medications");
        assertTableExists(conn, "prescriptions");
        assertTableExists(conn, "referrals");
        assertTableExists(conn, "receptionists");
        assertTableExists(conn, "invoices");
        assertTableExists(conn, "payments");
        assertTableExists(conn, "notifications");
        assertTableExists(conn, "messages");
    }

    @Test
    public void testSchemaInitializerCatchesSQLException() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        conn.close();
        // Should not throw, but if it does, it should be a RuntimeException
        assertThrows(RuntimeException.class, () -> SchemaInitializer.initializeSchema(conn));
    }
}