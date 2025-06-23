package physicianconnect.objects;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {

    @Test
    void testConstructorAndGetters() {
        LocalDateTime now = LocalDateTime.now();
        Notification n = new Notification("msg", "type", now, "uid", "utype");
        assertEquals("msg", n.getMessage());
        assertEquals("type", n.getType());
        assertEquals(now, n.getTimestamp());
        assertEquals("uid", n.getUserId());
        assertEquals("utype", n.getUserType());
        assertFalse(n.isRead());
    }

    @Test
    void testMarkAsRead() {
        Notification n = new Notification("msg", "type", LocalDateTime.now(), "uid", "utype");
        n.markAsRead();
        assertTrue(n.isRead());
    }
}