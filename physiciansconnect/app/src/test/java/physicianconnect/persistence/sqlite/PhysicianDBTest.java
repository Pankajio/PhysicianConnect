package physicianconnect.persistence.sqlite;

import org.junit.jupiter.api.*;
import physicianconnect.objects.Physician;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PhysicianDBTest {

    private Connection conn;
    private PhysicianDB db;

    @BeforeEach
    public void setup() throws Exception {
        conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        SchemaInitializer.initializeSchema(conn);
        db = new PhysicianDB(conn);
    }

    @AfterEach
    public void cleanup() throws Exception {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    @Test
    public void testAddAndFetchPhysician() {
        Physician p = new Physician("abc123", "Dr. Watson", "watson@email.com", "secret");
        db.addPhysician(p);

        Physician fetched = db.getPhysicianById("abc123");
        assertNotNull(fetched);
        assertEquals("Dr. Watson", fetched.getName());
        assertEquals("secret", fetched.getPassword());
    }

    @Test
    public void testDuplicatePhysicianIsIgnored() {
        Physician p1 = new Physician("x", "A", "a@email.com", "pw1");
        Physician p2 = new Physician("x", "B", "b@email.com", "pw2");

        db.addPhysician(p1);
        db.addPhysician(p2); // Duplicate ID, should be ignored

        Physician result = db.getPhysicianById("x");
        assertNotNull(result);
        assertEquals("A", result.getName()); // Should still be p1
        assertEquals("pw1", result.getPassword());
    }

    @Test
    public void testDeletePhysician() {
        Physician p = new Physician("delme", "Dr. Doom", "doom@latveria.com", "mask");
        db.addPhysician(p);

        db.deletePhysicianById("delme");
        assertNull(db.getPhysicianById("delme"));
    }

    @Test
    public void testDeleteAllPhysicians() {
        db.addPhysician(new Physician("1", "A", "a@a.com", "pw"));
        db.addPhysician(new Physician("2", "B", "b@b.com", "pw"));
        db.deleteAllPhysicians();
        assertTrue(db.getAllPhysicians().isEmpty());
    }

    @Test
    public void testGetAllPhysicians() {
        db.addPhysician(new Physician("1", "A", "a@a.com", "pw"));
        db.addPhysician(new Physician("2", "B", "b@b.com", "pw"));
        assertEquals(2, db.getAllPhysicians().size());
    }

    @Test
    public void testAddPhysicianNullIdThrows() {
        Physician p = new Physician(null, "A", "a@a.com", "pw");
        assertThrows(IllegalArgumentException.class, () -> db.addPhysician(p));
    }

    @Test
    public void testAddPhysicianBlankIdThrows() {
        Physician p = new Physician("   ", "A", "a@a.com", "pw");
        assertThrows(IllegalArgumentException.class, () -> db.addPhysician(p));
    }

    @Test
    public void testGetPhysicianByIdNotFound() {
        assertNull(db.getPhysicianById("notfound"));
    }

    // --- Catch/exception coverage ---

    @Test
    public void testAddPhysicianCatchesSQLException() throws Exception {
        Physician p = new Physician("x", "A", "a@a.com", "pw");
        conn.close();
        Exception ex = assertThrows(RuntimeException.class, () -> db.addPhysician(p));
        assertTrue(ex.getMessage().contains("Failed to add physician"));
    }

    @Test
    public void testGetAllPhysiciansCatchesSQLException() throws Exception {
        db.addPhysician(new Physician("1", "A", "a@a.com", "pw"));
        conn.close();
        Exception ex = assertThrows(RuntimeException.class, () -> db.getAllPhysicians());
        assertTrue(ex.getMessage().contains("Failed to fetch physicians"));
    }

    @Test
    public void testGetPhysicianByIdCatchesSQLException() throws Exception {
        db.addPhysician(new Physician("1", "A", "a@a.com", "pw"));
        conn.close();
        Exception ex = assertThrows(RuntimeException.class, () -> db.getPhysicianById("1"));
        assertTrue(ex.getMessage().contains("Failed to find physician"));
    }

    @Test
    public void testDeletePhysicianByIdCatchesSQLException() throws Exception {
        db.addPhysician(new Physician("1", "A", "a@a.com", "pw"));
        conn.close();
        Exception ex = assertThrows(RuntimeException.class, () -> db.deletePhysicianById("1"));
        assertTrue(ex.getMessage().contains("Failed to delete physician"));
    }

    @Test
    public void testDeleteAllPhysiciansCatchesSQLException() throws Exception {
        db.addPhysician(new Physician("1", "A", "a@a.com", "pw"));
        conn.close();
        Exception ex = assertThrows(RuntimeException.class, () -> db.deleteAllPhysicians());
        assertTrue(ex.getMessage().contains("Failed to delete all physicians"));
    }

    @Test
    public void testUpdatePhysicianCatchesSQLException() throws Exception {
        Physician p = new Physician("1", "A", "a@a.com", "pw");
        db.addPhysician(p);
        conn.close();
        // updatePhysician prints stack trace, does not throw
        assertDoesNotThrow(() -> db.updatePhysician(p));
    }
}