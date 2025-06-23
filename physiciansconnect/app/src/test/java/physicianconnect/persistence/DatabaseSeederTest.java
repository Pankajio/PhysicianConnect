package physicianconnect.persistence;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DatabaseSeederTest {

    @Test
    void testThrowsIfSeedFileNotFound() {
        Connection mockConn = mock(Connection.class);
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                DatabaseSeeder.seed(mockConn, List.of("nonexistent_file.sql")));
        assertTrue(ex.getMessage().contains("Seed file not found"));
    }

    @Test
    void testThrowsIfSqlExecutionFails() throws Exception {
        Connection mockConn = mock(Connection.class);
        Statement mockStmt = mock(Statement.class);
        when(mockConn.createStatement()).thenReturn(mockStmt);
        // Make execute throw an exception
        doThrow(new RuntimeException("SQL fail")).when(mockStmt).execute(anyString());

        // Use the real file you placed in resources/database_seeds/seed_badfile.sql
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                DatabaseSeeder.seed(mockConn, List.of("database_seeds/seed_badfile.sql")));
        assertTrue(ex.getMessage().contains("Failed to execute seed file"));
    }
}