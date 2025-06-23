package physicianconnect.logic.controller;

import org.junit.jupiter.api.*;
import org.mockito.*;
import physicianconnect.logic.MessageService;
import physicianconnect.logic.exceptions.InvalidMessageException;
import physicianconnect.objects.Message;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageControllerTest {

    @Mock
    private MessageService messageService;

    private MessageController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new MessageController(messageService);
    }

    @Test
    void testSendMessageDelegates() throws Exception {
        Message msg = mock(Message.class);
        when(messageService.sendMessage(any(), any(), any(), any(), any())).thenReturn(msg);

        Message result = controller.sendMessage("sid", "stype", "rid", "rtype", "hello");

        verify(messageService).sendMessage("sid", "stype", "rid", "rtype", "hello");
        assertEquals(msg, result);
    }

    @Test
    void testSendMessageThrowsOnBlankContent() {
        assertThrows(InvalidMessageException.class, () ->
                controller.sendMessage("sid", "stype", "rid", "rtype", "   "));
    }

    @Test
    void testSendMessageThrowsOnNullContent() {
        assertThrows(InvalidMessageException.class, () ->
                controller.sendMessage("sid", "stype", "rid", "rtype", null));
    }

    @Test
    void testSendMessageThrowsOnNullSenderId() {
        assertThrows(InvalidMessageException.class, () ->
                controller.sendMessage(null, "stype", "rid", "rtype", "msg"));
    }

    @Test
    void testSendMessageThrowsOnBlankSenderId() {
        assertThrows(InvalidMessageException.class, () ->
                controller.sendMessage("   ", "stype", "rid", "rtype", "msg"));
    }

    @Test
    void testSendMessageThrowsOnNullSenderType() {
        assertThrows(InvalidMessageException.class, () ->
                controller.sendMessage("sid", null, "rid", "rtype", "msg"));
    }

    @Test
    void testSendMessageThrowsOnBlankSenderType() {
        assertThrows(InvalidMessageException.class, () ->
                controller.sendMessage("sid", "   ", "rid", "rtype", "msg"));
    }

    @Test
    void testSendMessageThrowsOnNullReceiverId() {
        assertThrows(InvalidMessageException.class, () ->
                controller.sendMessage("sid", "stype", null, "rtype", "msg"));
    }

    @Test
    void testSendMessageThrowsOnBlankReceiverId() {
        assertThrows(InvalidMessageException.class, () ->
                controller.sendMessage("sid", "stype", "   ", "rtype", "msg"));
    }

    @Test
    void testSendMessageThrowsOnNullReceiverType() {
        assertThrows(InvalidMessageException.class, () ->
                controller.sendMessage("sid", "stype", "rid", null, "msg"));
    }

    @Test
    void testSendMessageThrowsOnBlankReceiverType() {
        assertThrows(InvalidMessageException.class, () ->
                controller.sendMessage("sid", "stype", "rid", "   ", "msg"));
    }

    @Test
    void testGetAllMessagesForUserDelegates() {
        when(messageService.getMessagesForUser("uid", "utype")).thenReturn(List.of());
        List<Message> result = controller.getAllMessagesForUser("uid", "utype");
        assertNotNull(result);
        verify(messageService).getMessagesForUser("uid", "utype");
    }

    @Test
    void testGetUnreadMessagesForUserDelegates() {
        when(messageService.getUnreadMessagesForUser("uid", "utype")).thenReturn(List.of());
        List<Message> result = controller.getUnreadMessagesForUser("uid", "utype");
        assertNotNull(result);
        verify(messageService).getUnreadMessagesForUser("uid", "utype");
    }

    @Test
    void testMarkMessageAsReadDelegates() {
        UUID id = UUID.randomUUID();
        controller.markMessageAsRead(id);
        verify(messageService).markMessageAsRead(id);
    }

    @Test
    void testGetUnreadMessageCountDelegates() {
        when(messageService.getUnreadMessageCount("uid", "utype")).thenReturn(3);
        int count = controller.getUnreadMessageCount("uid", "utype");
        assertEquals(3, count);
        verify(messageService).getUnreadMessageCount("uid", "utype");
    }
}