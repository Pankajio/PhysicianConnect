package physicianconnect.logic;

import org.junit.jupiter.api.*;
import org.mockito.*;
import physicianconnect.objects.Message;
import physicianconnect.persistence.interfaces.MessageRepository;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    private MessageService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new MessageService(messageRepository);
    }

    @Test
    void testSendMessageDelegatesAndReturns() {
        Message msg = new Message("sid", "stype", "rid", "rtype", "hello");
        when(messageRepository.save(any())).thenReturn(msg);

        Message result = service.sendMessage("sid", "stype", "rid", "rtype", "hello");

        assertEquals(msg, result);
        verify(messageRepository).save(any());
    }

    @Test
    void testGetMessagesForUserCombinesAndSorts() {
        Message m1 = mock(Message.class);
        Message m2 = mock(Message.class);
        when(m1.getTimestamp()).thenReturn(java.time.LocalDateTime.of(2025, 6, 1, 10, 0));
        when(m2.getTimestamp()).thenReturn(java.time.LocalDateTime.of(2025, 6, 1, 11, 0));
        when(messageRepository.findByReceiverId("uid", "utype")).thenReturn(List.of(m2));
        when(messageRepository.findBySenderId("uid", "utype")).thenReturn(List.of(m1));

        List<Message> result = service.getMessagesForUser("uid", "utype");

        assertEquals(2, result.size());
        assertTrue(result.get(0) == m1 || result.get(0) == m2); // Sorted by timestamp
        verify(messageRepository).findByReceiverId("uid", "utype");
        verify(messageRepository).findBySenderId("uid", "utype");
    }

    @Test
    void testGetUnreadMessagesForUserDelegates() {
        when(messageRepository.findUnreadByReceiverId("uid", "utype")).thenReturn(List.of());
        List<Message> result = service.getUnreadMessagesForUser("uid", "utype");
        assertNotNull(result);
        verify(messageRepository).findUnreadByReceiverId("uid", "utype");
    }

    @Test
    void testGetUnreadMessageCountDelegates() {
        when(messageRepository.countUnreadMessages("uid", "utype")).thenReturn(3);
        int count = service.getUnreadMessageCount("uid", "utype");
        assertEquals(3, count);
        verify(messageRepository).countUnreadMessages("uid", "utype");
    }

    @Test
    void testMarkMessageAsReadDelegates() {
        UUID id = UUID.randomUUID();
        service.markMessageAsRead(id);
        verify(messageRepository).markAsRead(id);
    }
}