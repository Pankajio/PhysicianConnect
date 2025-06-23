package physicianconnect.persistence.stub;

import org.junit.jupiter.api.*;
import physicianconnect.objects.Notification;
import physicianconnect.persistence.interfaces.NotificationPersistence;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificationPersistenceStubTest {

    private NotificationPersistence stub;

    @BeforeEach
    void setUp() {
        stub = StubFactory.createNotificationPersistence();
    }

    @Test
    void testAddAndGetNotificationsForUser() {
        Notification n = new Notification("msg", "type", LocalDateTime.now(), "uid", "utype");
        stub.addNotification(n);
        List<Notification> list = stub.getNotificationsForUser("uid", "utype");
        assertEquals(1, list.size());
        assertEquals("msg", list.get(0).getMessage());
    }

    @Test
    void testClearNotificationsForUser() {
        Notification n = new Notification("msg", "type", LocalDateTime.now(), "uid", "utype");
        stub.addNotification(n);
        stub.clearNotificationsForUser("uid", "utype");
        List<Notification> list = stub.getNotificationsForUser("uid", "utype");
        assertTrue(list.isEmpty());
    }
}