package physicianconnect.persistence.sqlite;

import org.junit.jupiter.api.*;
import physicianconnect.objects.Notification;
import physicianconnect.persistence.interfaces.ReceptionistPersistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationDBTest {
    private Connection conn;
    private NotificationDB db;
    private ReceptionistPersistence receptionistPersistence;

    @BeforeEach
    void setUp() throws Exception {
        conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        SchemaInitializer.initializeSchema(conn);
        receptionistPersistence = mock(ReceptionistPersistence.class);
        db = new NotificationDB(conn, receptionistPersistence);

            // Insert a physician with id "uid" to satisfy the foreign key constraint
    try (var stmt = conn.prepareStatement(
            "INSERT INTO physicians (id, name, email, password) VALUES (?, ?, ?, ?)")) {
        stmt.setString(1, "uid");
        stmt.setString(2, "Test Physician");
        stmt.setString(3, "test@doc.com");
        stmt.setString(4, "pw");
        stmt.executeUpdate();
    }
    }

    @AfterEach
    void tearDown() throws Exception {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

@Test
void testAddAndFetchNotification() {
    LocalDateTime now = LocalDateTime.now();
    Notification n = new Notification("msg", "type", now, "uid", "utype");
    db.addNotification(n);
    List<Notification> list = db.getNotificationsForUser("uid", "utype");
    assertEquals(1, list.size());
    assertEquals("msg", list.get(0).getMessage());
}

    @Test
    void testClearNotificationsForUser() {
        Notification n = new Notification("msg", "type", LocalDateTime.now(), "uid", "utype");
        db.addNotification(n);
        db.clearNotificationsForUser("uid", "utype");
        List<Notification> list = db.getNotificationsForUser("uid", "utype");
        assertTrue(list.isEmpty());
    }

    @Test
    void testGetNotificationsForUserEmpty() {
        List<Notification> list = db.getNotificationsForUser("nouser", "notype");
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    // --- Catch/exception coverage ---

    @Test
    void testAddNotificationCatchesSQLException() throws Exception {
        Notification n = new Notification("msg", "type", LocalDateTime.now(), "uid", "utype");
        conn.close();
        assertDoesNotThrow(() -> db.addNotification(n)); // e.printStackTrace() is called, not thrown
    }

    @Test
    void testGetNotificationsForUserCatchesSQLException() throws Exception {
        Notification n = new Notification("msg", "type", LocalDateTime.now(), "uid", "utype");
        db.addNotification(n);
        conn.close();
        List<Notification> list = db.getNotificationsForUser("uid", "utype");
        assertNotNull(list);
        assertTrue(list.isEmpty()); // returns empty list on exception
    }

    @Test
    void testClearNotificationsForUserCatchesSQLException() throws Exception {
        Notification n = new Notification("msg", "type", LocalDateTime.now(), "uid", "utype");
        db.addNotification(n);
        conn.close();
        assertDoesNotThrow(() -> db.clearNotificationsForUser("uid", "utype")); // e.printStackTrace() is called, not thrown
    }
}