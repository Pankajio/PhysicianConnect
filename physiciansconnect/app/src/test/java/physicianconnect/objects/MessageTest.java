package physicianconnect.objects;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    @Test
    void testConstructorAndGettersSetters() {
        Message m = new Message("sid", "stype", "rid", "rtype", "hello");
        assertEquals("sid", m.getSenderId());
        assertEquals("stype", m.getSenderType());
        assertEquals("rid", m.getReceiverId());
        assertEquals("rtype", m.getReceiverType());
        assertEquals("hello", m.getContent());
        assertNotNull(m.getTimestamp());
        assertFalse(m.isRead());

        m.setRead(true);
        assertTrue(m.isRead());

        LocalDateTime now = LocalDateTime.now();
        m.setTimestamp(now);
        assertEquals(now, m.getTimestamp());

        UUID uuid = UUID.randomUUID();
        m.setMessageId(uuid);
        assertEquals(uuid, m.getMessageId());
    }
}