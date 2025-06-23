package physicianconnect.persistence.sqlite;

import org.junit.jupiter.api.*;
import physicianconnect.objects.Receptionist;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReceptionistDBTest {
    private Connection conn;
    private ReceptionistDB db;

    @BeforeEach
    void setUp() throws Exception {
        conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        SchemaInitializer.initializeSchema(conn);
        db = new ReceptionistDB(conn);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    @Test
    void testAddAndFetchReceptionist() {
        Receptionist r = new Receptionist("id1", "Alice", "alice@email.com", "pw");
        db.addReceptionist(r);
        Receptionist fetched = db.getReceptionistById("id1");
        assertNotNull(fetched);
        assertEquals("Alice", fetched.getName());
    }

    @Test
    void testGetReceptionistByEmail() {
        Receptionist r = new Receptionist("id2", "Bob", "bob@email.com", "pw2");
        db.addReceptionist(r);
        Receptionist fetched = db.getReceptionistByEmail("bob@email.com");
        assertNotNull(fetched);
        assertEquals("Bob", fetched.getName());
        assertEquals("id2", fetched.getId());
    }

    @Test
    void testGetReceptionistByEmailNotFound() {
        Receptionist fetched = db.getReceptionistByEmail("notfound@email.com");
        assertNull(fetched);
    }

    @Test
    void testGetAllReceptionists() {
        db.addReceptionist(new Receptionist("id1", "Alice", "alice@email.com", "pw"));
        db.addReceptionist(new Receptionist("id2", "Bob", "bob@email.com", "pw"));
        List<Receptionist> all = db.getAllReceptionists();
        assertEquals(2, all.size());
    }

    // --- Catch/exception coverage ---

    @Test
    void testAddReceptionistCatchesSQLException() throws Exception {
        Receptionist r = new Receptionist("id1", "Alice", "alice@email.com", "pw");
        conn.close();
        Exception ex = assertThrows(RuntimeException.class, () -> db.addReceptionist(r));
        assertTrue(ex.getMessage().contains("Failed to add receptionist"));
    }

@Test
void testGetReceptionistByIdCatchesSQLException() throws Exception {
    db.addReceptionist(new Receptionist("id1", "Alice", "alice@email.com", "pw"));
    conn.close();
    Receptionist result = db.getReceptionistById("id1");
    assertNull(result);
}

    @Test
    void testGetReceptionistByEmailCatchesSQLException() throws Exception {
        db.addReceptionist(new Receptionist("id2", "Bob", "bob@email.com", "pw2"));
        conn.close();
        Exception ex = assertThrows(RuntimeException.class, () -> db.getReceptionistByEmail("bob@email.com"));
        assertTrue(ex.getMessage().contains("Failed to find receptionist by email"));
    }

    @Test
    void testGetAllReceptionistsCatchesSQLException() throws Exception {
        db.addReceptionist(new Receptionist("id1", "Alice", "alice@email.com", "pw"));
        conn.close();
        Exception ex = assertThrows(RuntimeException.class, () -> db.getAllReceptionists());
        assertTrue(ex.getMessage().contains("Failed to fetch receptionists"));
    }

    @Test
    void testUpdateReceptionistCatchesSQLException() throws Exception {
        Receptionist r = new Receptionist("id1", "Alice", "alice@email.com", "pw");
        db.addReceptionist(r);
        conn.close();
        Exception ex = assertThrows(RuntimeException.class, () -> db.updateReceptionist(r));
        assertTrue(ex.getMessage().contains("Failed to update receptionist"));
    }
}