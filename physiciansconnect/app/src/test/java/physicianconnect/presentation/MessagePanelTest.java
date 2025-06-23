package physicianconnect.presentation;

import org.junit.jupiter.api.*;
import org.mockito.*;
import physicianconnect.logic.controller.MessageController;
import physicianconnect.logic.exceptions.InvalidMessageException;
import physicianconnect.objects.Message;
import physicianconnect.objects.Physician;
import physicianconnect.objects.Receptionist;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class MessagePanelTest {

    @Mock
    MessageController messageController;

    Physician p1;
    Physician p2;
    Receptionist r1;
    Receptionist r2;
    List<Object> users;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        p1 = new Physician("doc1", "Dr. Alice", "alice@email.com", "pw");
        p2 = new Physician("doc2", "Dr. Bob", "bob@email.com", "pw");
        r1 = new Receptionist("rec1", "Receptionist One", "rec1@email.com", "pw");
        r2 = new Receptionist("rec2", "Receptionist Two", "rec2@email.com", "pw");
        users = List.of(p1, p2, r1, r2);
    }

    @Test
    void testPanelConstructsAndShowsUsers() {
        when(messageController.getAllMessagesForUser(anyString(), anyString())).thenReturn(List.of());
        when(messageController.getUnreadMessagesForUser(anyString(), anyString())).thenReturn(List.of());
        when(messageController.getUnreadMessageCount(anyString(), anyString())).thenReturn(0);

        MessagePanel panel = new MessagePanel(messageController, "doc1", "physician", users);

        JTextField searchField = (JTextField) getField(panel, "searchField");
        assertNotNull(searchField);
        assertEquals("", searchField.getText());

        // Should not show self in user list
        JList<?> searchResultsList = (JList<?>) getField(panel, "searchResultsList");
        DefaultListModel<?> model = (DefaultListModel<?>) getField(panel, "searchResultsModel");
        assertFalse(containsUser(model, p1));
        assertTrue(containsUser(model, p2));
        assertTrue(containsUser(model, r1));
        assertTrue(containsUser(model, r2));
    }

    @Test
    void testSearchFiltersUsers() {
        when(messageController.getAllMessagesForUser(anyString(), anyString())).thenReturn(List.of());
        when(messageController.getUnreadMessagesForUser(anyString(), anyString())).thenReturn(List.of());
        when(messageController.getUnreadMessageCount(anyString(), anyString())).thenReturn(0);

        MessagePanel panel = new MessagePanel(messageController, "doc1", "physician", users);
        JTextField searchField = (JTextField) getField(panel, "searchField");
        DefaultListModel<?> model = (DefaultListModel<?>) getField(panel, "searchResultsModel");

        searchField.setText("Bob");
        // Simulate document event
        panel.requestFocus(); // just to trigger listeners if needed
        assertEquals(1, model.size());
        assertTrue(containsUser(model, p2));
    }

    @Test
    void testSelectRecipientAndLoadMessages() throws Exception {
        Message m1 = createMessage("doc1", "physician", "rec1", "receptionist", "Hello", false);
        Message m2 = createMessage("rec1", "receptionist", "doc1", "physician", "Hi!", false);
        when(messageController.getAllMessagesForUser("doc1", "physician")).thenReturn(List.of(m1, m2));
        when(messageController.getUnreadMessagesForUser(anyString(), anyString())).thenReturn(List.of());
        when(messageController.getUnreadMessageCount(anyString(), anyString())).thenReturn(0);

        MessagePanel panel = new MessagePanel(messageController, "doc1", "physician", users);

        JList<Object> searchResultsList = (JList<Object>) getField(panel, "searchResultsList");
        DefaultListModel<Object> model = (DefaultListModel<Object>) getField(panel, "searchResultsModel");
        int rec1Index = -1;
        for (int i = 0; i < model.size(); i++) {
            if (model.get(i) instanceof Receptionist && ((Receptionist) model.get(i)).getId().equals("rec1")) {
                rec1Index = i;
                break;
            }
        }
        assertTrue(rec1Index >= 0);
        searchResultsList.setSelectedIndex(rec1Index);

        // Should load messages with rec1
        DefaultListModel<Message> messageListModel = (DefaultListModel<Message>) getField(panel, "messageListModel");
        assertEquals(2, messageListModel.size());
    }

@Test
void testSendMessageSuccess() throws Exception {
    Message sent = createMessage("doc1", "physician", "rec1", "receptionist", "How are you?", false);

    // Before sending, no messages
    when(messageController.getAllMessagesForUser(anyString(), anyString()))
        .thenReturn(List.of()) // first call: recipient selection
        .thenReturn(List.of(sent)); // second call: after sending
    when(messageController.getUnreadMessagesForUser(anyString(), anyString())).thenReturn(List.of());
    when(messageController.getUnreadMessageCount(anyString(), anyString())).thenReturn(0);

    MessagePanel panel = new MessagePanel(messageController, "doc1", "physician", users);

    when(messageController.sendMessage(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(sent);

    // Select recipient
    JList<Object> searchResultsList = (JList<Object>) getField(panel, "searchResultsList");
    DefaultListModel<Object> model = (DefaultListModel<Object>) getField(panel, "searchResultsModel");
    int rec1Index = -1;
    for (int i = 0; i < model.size(); i++) {
        if (model.get(i) instanceof Receptionist && ((Receptionist) model.get(i)).getId().equals("rec1")) {
            rec1Index = i;
            break;
        }
    }
    searchResultsList.setSelectedIndex(rec1Index);

    JTextField messageInput = (JTextField) getField(panel, "messageInput");
    messageInput.setText("How are you?");

    JButton sendButton = findButton(panel, "Send");
    assertNotNull(sendButton);
    sendButton.doClick();

    DefaultListModel<Message> messageListModel = (DefaultListModel<Message>) getField(panel, "messageListModel");
    assertEquals(1, messageListModel.size());
    assertEquals("How are you?", messageListModel.get(0).getContent());
}

    @Test
    void testSendMessageNoRecipientShowsDialog() throws Exception {
        MessagePanel panel = new MessagePanel(messageController, "doc1", "physician", users);
        JTextField messageInput = (JTextField) getField(panel, "messageInput");
        messageInput.setText("Hello!");

        try (MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {
            JButton sendButton = findButton(panel, "Send");
            sendButton.doClick();
            mockedPane.verify(() -> JOptionPane.showMessageDialog(any(), contains("recipient"), any(), eq(JOptionPane.WARNING_MESSAGE)));
        }
    }

    @Test
    void testSendMessageInvalidShowsDialog() throws Exception {
        when(messageController.sendMessage(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenThrow(new InvalidMessageException("Invalid!"));
        MessagePanel panel = new MessagePanel(messageController, "doc1", "physician", users);

        // Select recipient
        JList<Object> searchResultsList = (JList<Object>) getField(panel, "searchResultsList");
        DefaultListModel<Object> model = (DefaultListModel<Object>) getField(panel, "searchResultsModel");
        int rec1Index = -1;
        for (int i = 0; i < model.size(); i++) {
            if (model.get(i) instanceof Receptionist && ((Receptionist) model.get(i)).getId().equals("rec1")) {
                rec1Index = i;
                break;
            }
        }
        searchResultsList.setSelectedIndex(rec1Index);

        JTextField messageInput = (JTextField) getField(panel, "messageInput");
        messageInput.setText("bad");

        try (MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {
            JButton sendButton = findButton(panel, "Send");
            sendButton.doClick();
            mockedPane.verify(() -> JOptionPane.showMessageDialog(any(), contains("Invalid!"), any(), eq(JOptionPane.ERROR_MESSAGE)));
        }
    }

    // --- Helpers ---
    private boolean containsUser(DefaultListModel<?> model, Object user) {
        for (int i = 0; i < model.size(); i++) {
            if (model.get(i).equals(user)) return true;
        }
        return false;
    }

    private Object getField(Object obj, String fieldName) {
        try {
            var field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private JButton findButton(Container container, String text) {
        for (Component c : container.getComponents()) {
            if (c instanceof JButton && ((JButton) c).getText().equalsIgnoreCase(text)) {
                return (JButton) c;
            }
            if (c instanceof Container) {
                JButton btn = findButton((Container) c, text);
                if (btn != null) return btn;
            }
        }
        return null;
    }

    // Helper to create a Message object (matches your Message class)
    private Message createMessage(String senderId, String senderType, String receiverId, String receiverType, String content, boolean isRead) {
        Message m = new Message(senderId, senderType, receiverId, receiverType, content);
        m.setMessageId(UUID.randomUUID());
        m.setTimestamp(LocalDateTime.now());
        m.setRead(isRead);
        return m;
    }
}