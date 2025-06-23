package physicianconnect.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import physicianconnect.objects.Message;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryMessageRepositoryTest {

    private InMemoryMessageRepository repo;

    @BeforeEach
    void setUp() {
        repo = new InMemoryMessageRepository();
    }

    @Test
    void testSaveAndFindByReceiverId() {
        Message m = new Message("sid", "stype", "rid", "rtype", "hello");
        repo.save(m);
        List<Message> received = repo.findByReceiverId("rid", "rtype");
        assertEquals(1, received.size());
        assertEquals("hello", received.get(0).getContent());
    }

    @Test
    void testFindByReceiverIdNegative() {
        Message m = new Message("sid", "stype", "rid", "rtype", "hello");
        repo.save(m);
        // Wrong receiverId
        assertTrue(repo.findByReceiverId("other", "rtype").isEmpty());
        // Wrong receiverType
        assertTrue(repo.findByReceiverId("rid", "othertype").isEmpty());
    }

    @Test
    void testFindBySenderId() {
        Message m = new Message("sid", "stype", "rid", "rtype", "hello");
        repo.save(m);
        List<Message> sent = repo.findBySenderId("sid", "stype");
        assertEquals(1, sent.size());
    }

    @Test
    void testFindBySenderIdNegative() {
        Message m = new Message("sid", "stype", "rid", "rtype", "hello");
        repo.save(m);
        // Wrong senderId
        assertTrue(repo.findBySenderId("other", "stype").isEmpty());
        // Wrong senderType
        assertTrue(repo.findBySenderId("sid", "othertype").isEmpty());
    }

    @Test
    void testFindUnreadByReceiverId() {
        Message m1 = new Message("sid", "stype", "rid", "rtype", "unread");
        Message m2 = new Message("sid", "stype", "rid", "rtype", "read");
        m2.setRead(true);
        repo.save(m1);
        repo.save(m2);
        List<Message> unread = repo.findUnreadByReceiverId("rid", "rtype");
        assertEquals(1, unread.size());
        assertEquals("unread", unread.get(0).getContent());
    }

    @Test
    void testFindUnreadByReceiverIdNegative() {
        Message m = new Message("sid", "stype", "rid", "rtype", "msg");
        m.setRead(true);
        repo.save(m);
        // All messages are read
        assertTrue(repo.findUnreadByReceiverId("rid", "rtype").isEmpty());
        // Wrong receiverId
        assertTrue(repo.findUnreadByReceiverId("other", "rtype").isEmpty());
        // Wrong receiverType
        assertTrue(repo.findUnreadByReceiverId("rid", "othertype").isEmpty());
    }

    @Test
    void testMarkAsReadIfMessageExists() {
        Message m = new Message("sid", "stype", "rid", "rtype", "hello");
        UUID id = UUID.randomUUID();
        m.setMessageId(id);
        repo.save(m);
        repo.markAsRead(id);
        assertTrue(repo.findByReceiverId("rid", "rtype").get(0).isRead());
    }

    @Test
    void testMarkAsReadIfMessageDoesNotExist() {
        // Should not throw
        assertDoesNotThrow(() -> repo.markAsRead(UUID.randomUUID()));
    }

    @Test
    void testCountUnreadMessages() {
        Message m = new Message("sid", "stype", "rid", "rtype", "hello");
        repo.save(m);
        assertEquals(1, repo.countUnreadMessages("rid", "rtype"));
        repo.markAsRead(m.getMessageId());
        assertEquals(0, repo.countUnreadMessages("rid", "rtype"));
    }

    @Test
    void testCountUnreadMessagesNegative() {
        Message m = new Message("sid", "stype", "rid", "rtype", "msg");
        m.setRead(true);
        repo.save(m);
        assertEquals(0, repo.countUnreadMessages("rid", "rtype"));
        assertEquals(0, repo.countUnreadMessages("other", "rtype"));
        assertEquals(0, repo.countUnreadMessages("rid", "othertype"));
    }
}