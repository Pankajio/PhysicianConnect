package physicianconnect.persistence.sqlite;

import org.junit.jupiter.api.*;
import physicianconnect.objects.Message;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MessageDBTest {
    private Connection conn;
    private MessageDB db;

    @BeforeEach
    void setUp() throws Exception {
        conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        SchemaInitializer.initializeSchema(conn);
        db = new MessageDB(conn);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    private Message makeMessage(String senderId, String senderType, String receiverId, String receiverType, String content) {
        Message m = new Message(senderId, senderType, receiverId, receiverType, content);
        m.setMessageId(UUID.randomUUID());
        m.setTimestamp(LocalDateTime.now());
        return m;
    }

    @Test
    void testSaveAndFetchMessage() {
        Message m = makeMessage("sid", "stype", "rid", "rtype", "hello");
        db.save(m);
        List<Message> received = db.findByReceiverId("rid", "rtype");
        assertEquals(1, received.size());
        assertEquals("hello", received.get(0).getContent());
    }

    @Test
    void testSaveNullMessageThrows() {
        assertThrows(IllegalArgumentException.class, () -> db.save(null));
    }

    @Test
    void testSaveNullIdThrows() {
        Message m = makeMessage("sid", "stype", "rid", "rtype", "hello");
        m.setMessageId(null);
        assertThrows(IllegalArgumentException.class, () -> db.save(m));
    }

    @Test
    void testSaveNullSenderIdThrows() {
        Message m = makeMessage(null, "stype", "rid", "rtype", "hello");
        assertThrows(IllegalArgumentException.class, () -> db.save(m));
    }

    @Test
    void testSaveEmptySenderIdThrows() {
        Message m = makeMessage("   ", "stype", "rid", "rtype", "hello");
        assertThrows(IllegalArgumentException.class, () -> db.save(m));
    }

    @Test
    void testSaveNullSenderTypeThrows() {
        Message m = makeMessage("sid", null, "rid", "rtype", "hello");
        assertThrows(IllegalArgumentException.class, () -> db.save(m));
    }

    @Test
    void testSaveEmptySenderTypeThrows() {
        Message m = makeMessage("sid", "   ", "rid", "rtype", "hello");
        assertThrows(IllegalArgumentException.class, () -> db.save(m));
    }

    @Test
    void testSaveNullReceiverIdThrows() {
        Message m = makeMessage("sid", "stype", null, "rtype", "hello");
        assertThrows(IllegalArgumentException.class, () -> db.save(m));
    }

    @Test
    void testSaveEmptyReceiverIdThrows() {
        Message m = makeMessage("sid", "stype", "   ", "rtype", "hello");
        assertThrows(IllegalArgumentException.class, () -> db.save(m));
    }

    @Test
    void testSaveNullReceiverTypeThrows() {
        Message m = makeMessage("sid", "stype", "rid", null, "hello");
        assertThrows(IllegalArgumentException.class, () -> db.save(m));
    }

    @Test
    void testSaveEmptyReceiverTypeThrows() {
        Message m = makeMessage("sid", "stype", "rid", "   ", "hello");
        assertThrows(IllegalArgumentException.class, () -> db.save(m));
    }

    @Test
    void testSaveNullContentThrows() {
        Message m = makeMessage("sid", "stype", "rid", "rtype", null);
        assertThrows(IllegalArgumentException.class, () -> db.save(m));
    }

    @Test
    void testSaveNullTimestampThrows() {
        Message m = makeMessage("sid", "stype", "rid", "rtype", "hello");
        m.setTimestamp(null);
        assertThrows(IllegalArgumentException.class, () -> db.save(m));
    }

    @Test
    void testMarkAsReadNullIdThrows() {
        assertThrows(IllegalArgumentException.class, () -> db.markAsRead(null));
    }

    @Test
    void testMarkAsReadNonexistentThrows() {
        UUID fakeId = UUID.randomUUID();
        Exception ex = assertThrows(RuntimeException.class, () -> db.markAsRead(fakeId));
        assertTrue(ex.getMessage().contains("No message found"));
    }

    @Test
    void testFindByReceiverIdNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> db.findByReceiverId(null, "rtype"));
    }

    @Test
    void testFindByReceiverIdEmptyThrows() {
        assertThrows(IllegalArgumentException.class, () -> db.findByReceiverId("   ", "rtype"));
    }

    @Test
    void testFindByReceiverTypeNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> db.findByReceiverId("rid", null));
    }

    @Test
    void testFindByReceiverTypeEmptyThrows() {
        assertThrows(IllegalArgumentException.class, () -> db.findByReceiverId("rid", "   "));
    }

    @Test
    void testFindBySenderIdNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> db.findBySenderId(null, "stype"));
    }

    @Test
    void testFindBySenderIdEmptyThrows() {
        assertThrows(IllegalArgumentException.class, () -> db.findBySenderId("   ", "stype"));
    }

    @Test
    void testFindBySenderTypeNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> db.findBySenderId("sid", null));
    }

    @Test
    void testFindBySenderTypeEmptyThrows() {
        assertThrows(IllegalArgumentException.class, () -> db.findBySenderId("sid", "   "));
    }

    @Test
    void testFindUnreadByReceiverIdNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> db.findUnreadByReceiverId(null, "rtype"));
    }

    @Test
    void testFindUnreadByReceiverIdEmptyThrows() {
        assertThrows(IllegalArgumentException.class, () -> db.findUnreadByReceiverId("   ", "rtype"));
    }

    @Test
    void testFindUnreadByReceiverTypeNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> db.findUnreadByReceiverId("rid", null));
    }

    @Test
    void testFindUnreadByReceiverTypeEmptyThrows() {
        assertThrows(IllegalArgumentException.class, () -> db.findUnreadByReceiverId("rid", "   "));
    }

    @Test
    void testCountUnreadMessagesNullIdThrows() {
        assertThrows(IllegalArgumentException.class, () -> db.countUnreadMessages(null, "rtype"));
    }

    @Test
    void testCountUnreadMessagesEmptyIdThrows() {
        assertThrows(IllegalArgumentException.class, () -> db.countUnreadMessages("   ", "rtype"));
    }

    @Test
    void testCountUnreadMessagesNullTypeThrows() {
        assertThrows(IllegalArgumentException.class, () -> db.countUnreadMessages("rid", null));
    }

    @Test
    void testCountUnreadMessagesEmptyTypeThrows() {
        assertThrows(IllegalArgumentException.class, () -> db.countUnreadMessages("rid", "   "));
    }

    @Test
    void testMarkAsReadUpdatesIsRead() {
        Message m = makeMessage("sid", "stype", "rid", "rtype", "hello");
        db.save(m);
        db.markAsRead(m.getMessageId());
        List<Message> received = db.findByReceiverId("rid", "rtype");
        assertTrue(received.get(0).isRead());
    }

    @Test
    void testCountUnreadMessages() {
        Message m = makeMessage("sid", "stype", "rid", "rtype", "hello");
        db.save(m);
        int count = db.countUnreadMessages("rid", "rtype");
        assertEquals(1, count);
        db.markAsRead(m.getMessageId());
        assertEquals(0, db.countUnreadMessages("rid", "rtype"));
    }

    @Test
    void testFindBySenderId() {
        Message m = makeMessage("sid", "stype", "rid", "rtype", "hello");
        db.save(m);
        List<Message> sent = db.findBySenderId("sid", "stype");
        assertEquals(1, sent.size());
        assertEquals("hello", sent.get(0).getContent());
    }

    @Test
    void testFindUnreadByReceiverId() {
        Message m = makeMessage("sid", "stype", "rid", "rtype", "hello");
        db.save(m);
        List<Message> unread = db.findUnreadByReceiverId("rid", "rtype");
        assertEquals(1, unread.size());
        assertFalse(unread.get(0).isRead());
        db.markAsRead(m.getMessageId());
        assertTrue(db.findUnreadByReceiverId("rid", "rtype").isEmpty());
    }

    @Test
    void testSaveCatchesSQLException() throws Exception {
        conn.close();
        Message m = makeMessage("sid", "stype", "rid", "rtype", "hello");
        Exception ex = assertThrows(RuntimeException.class, () -> db.save(m));
        assertTrue(ex.getMessage().contains("Failed to save message"));
    }

    @Test
    void testMarkAsReadCatchesSQLException() throws Exception {
        Message m = makeMessage("sid", "stype", "rid", "rtype", "hello");
        db.save(m);
        conn.close();
        Exception ex = assertThrows(RuntimeException.class, () -> db.markAsRead(m.getMessageId()));
        assertTrue(ex.getMessage().contains("Failed to mark message as read"));
    }

    @Test
    void testFindByReceiverIdCatchesSQLException() throws Exception {
        Message m = makeMessage("sid", "stype", "rid", "rtype", "hello");
        db.save(m);
        conn.close();
        Exception ex = assertThrows(RuntimeException.class, () -> db.findByReceiverId("rid", "rtype"));
        assertTrue(ex.getMessage().contains("Failed to find messages by receiver"));
    }

    @Test
    void testFindBySenderIdCatchesSQLException() throws Exception {
        Message m = makeMessage("sid", "stype", "rid", "rtype", "hello");
        db.save(m);
        conn.close();
        Exception ex = assertThrows(RuntimeException.class, () -> db.findBySenderId("sid", "stype"));
        assertTrue(ex.getMessage().contains("Failed to find messages by sender"));
    }

    @Test
    void testFindUnreadByReceiverIdCatchesSQLException() throws Exception {
        Message m = makeMessage("sid", "stype", "rid", "rtype", "hello");
        db.save(m);
        conn.close();
        Exception ex = assertThrows(RuntimeException.class, () -> db.findUnreadByReceiverId("rid", "rtype"));
        assertTrue(ex.getMessage().contains("Failed to find unread messages by receiver"));
    }

    @Test
    void testCountUnreadMessagesCatchesSQLException() throws Exception {
        Message m = makeMessage("sid", "stype", "rid", "rtype", "hello");
        db.save(m);
        conn.close();
        Exception ex = assertThrows(RuntimeException.class, () -> db.countUnreadMessages("rid", "rtype"));
        assertTrue(ex.getMessage().contains("Failed to count unread messages"));
    }
}