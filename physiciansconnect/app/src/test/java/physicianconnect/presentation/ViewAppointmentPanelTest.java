package physicianconnect.presentation;

import org.junit.jupiter.api.*;
import org.mockito.*;
import physicianconnect.logic.controller.AppointmentController;
import physicianconnect.objects.Appointment;
import physicianconnect.presentation.config.UIConfig;
import physicianconnect.presentation.config.UITheme;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ViewAppointmentPanelTest {

    @Mock
    AppointmentController appointmentController;

    JFrame parent;
    Appointment appointment;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        parent = new JFrame();
        // Use a constructor that matches your Appointment class
        appointment = new Appointment(
                "doc1",
                "Dr. Alice",
                LocalDateTime.of(2025, 6, 10, 14, 0),
                "Initial notes"
        );
    }

    @AfterEach
    void tearDown() {
        parent.dispose();
    }

    @Test
    void testPanelDisplaysAppointmentDetails() {
        ViewAppointmentPanel panel = new ViewAppointmentPanel(parent, appointmentController, appointment);

        // Check patient label
        JLabel patientLabel = findLabel(panel, UIConfig.PATIENT_LABEL + appointment.getPatientName());
        assertNotNull(patientLabel);

        // Check date label
        String expectedDate = appointment.getDateTime().format(java.time.format.DateTimeFormatter.ofPattern(UIConfig.HISTORY_DATE_PATTERN));
        JLabel dateLabel = findLabel(panel, UIConfig.DATE_LABEL + expectedDate);

        // Check notes area
        JTextArea notesArea = (JTextArea) getField(panel, "notesArea");
        assertEquals("Initial notes", notesArea.getText());
    }

    @Test
    void testUpdateNotesSuccess() {
        Runnable onSuccess = mock(Runnable.class);
        ViewAppointmentPanel panel = new ViewAppointmentPanel(parent, appointmentController, appointment, onSuccess);

        JTextArea notesArea = (JTextArea) getField(panel, "notesArea");
        notesArea.setText("Updated notes");

        JButton updateBtn = findButton(panel, UIConfig.BUTTON_UPDATE_NOTES);
        assertNotNull(updateBtn);

        try (MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {
            updateBtn.doClick();
            verify(appointmentController).updateAppointmentNotes(appointment, "Updated notes");
            mockedPane.verify(() -> JOptionPane.showMessageDialog(
                    any(), eq(UIConfig.MESSAGE_NOTES_UPDATED), eq(UIConfig.SUCCESS_DIALOG_TITLE), eq(JOptionPane.INFORMATION_MESSAGE)));
            verify(onSuccess).run();
        }
    }

    @Test
    void testUpdateNotesFailureShowsDialog() throws Exception {
        ViewAppointmentPanel panel = new ViewAppointmentPanel(parent, appointmentController, appointment);

        JTextArea notesArea = (JTextArea) getField(panel, "notesArea");
        notesArea.setText("fail");

        JButton updateBtn = findButton(panel, UIConfig.BUTTON_UPDATE_NOTES);
        assertNotNull(updateBtn);

        doThrow(new RuntimeException("DB error")).when(appointmentController).updateAppointmentNotes(any(), anyString());

        try (MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {
            updateBtn.doClick();
            mockedPane.verify(() -> JOptionPane.showMessageDialog(
                    any(), contains(UIConfig.ERROR_UPDATING_NOTES), eq(UIConfig.ERROR_DIALOG_TITLE), eq(JOptionPane.ERROR_MESSAGE)));
        }
    }

    @Test
    void testDeleteAppointmentConfirmedAndSuccess() throws Exception {
        Runnable onSuccess = mock(Runnable.class);
        ViewAppointmentPanel panel = new ViewAppointmentPanel(parent, appointmentController, appointment, onSuccess);

        JButton deleteBtn = findButton(panel, UIConfig.BUTTON_DELETE_APPOINTMENT);
        assertNotNull(deleteBtn);

        try (MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {
            mockedPane.when(() -> JOptionPane.showConfirmDialog(
                    any(), eq(UIConfig.CONFIRM_DELETE_MESSAGE), eq(UIConfig.CONFIRM_DIALOG_TITLE),
                    eq(JOptionPane.YES_NO_OPTION), eq(JOptionPane.WARNING_MESSAGE)))
                    .thenReturn(JOptionPane.YES_OPTION);

            deleteBtn.doClick();
            verify(appointmentController).deleteAppointment(appointment);
            verify(onSuccess).run();
            // Panel should be disposed (not visible)
            assertFalse(panel.isVisible());
        }
    }

    @Test
    void testDeleteAppointmentConfirmedAndFailure() throws Exception {
        ViewAppointmentPanel panel = new ViewAppointmentPanel(parent, appointmentController, appointment);

        JButton deleteBtn = findButton(panel, UIConfig.BUTTON_DELETE_APPOINTMENT);
        assertNotNull(deleteBtn);

        doThrow(new RuntimeException("DB error")).when(appointmentController).deleteAppointment(any());

        try (MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {
            mockedPane.when(() -> JOptionPane.showConfirmDialog(
                    any(), eq(UIConfig.CONFIRM_DELETE_MESSAGE), eq(UIConfig.CONFIRM_DIALOG_TITLE),
                    eq(JOptionPane.YES_NO_OPTION), eq(JOptionPane.WARNING_MESSAGE)))
                    .thenReturn(JOptionPane.YES_OPTION);

            deleteBtn.doClick();
            verify(appointmentController).deleteAppointment(appointment);
            mockedPane.verify(() -> JOptionPane.showMessageDialog(
                    any(), contains(UIConfig.ERROR_DELETING_APPOINTMENT), eq(UIConfig.ERROR_DIALOG_TITLE), eq(JOptionPane.ERROR_MESSAGE)));
        }
    }

    @Test
    void testDeleteAppointmentNotConfirmedDoesNothing() throws Exception {
        ViewAppointmentPanel panel = new ViewAppointmentPanel(parent, appointmentController, appointment);

        JButton deleteBtn = findButton(panel, UIConfig.BUTTON_DELETE_APPOINTMENT);
        assertNotNull(deleteBtn);

        try (MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {
            mockedPane.when(() -> JOptionPane.showConfirmDialog(
                    any(), eq(UIConfig.CONFIRM_DELETE_MESSAGE), eq(UIConfig.CONFIRM_DIALOG_TITLE),
                    eq(JOptionPane.YES_NO_OPTION), eq(JOptionPane.WARNING_MESSAGE)))
                    .thenReturn(JOptionPane.NO_OPTION);

            deleteBtn.doClick();
            verify(appointmentController, never()).deleteAppointment(any());
        }
    }

    @Test
    void testCloseButtonDisposesPanel() {
        ViewAppointmentPanel panel = new ViewAppointmentPanel(parent, appointmentController, appointment);

        JButton closeBtn = findButton(panel, UIConfig.BUTTON_CLOSE);
        assertNotNull(closeBtn);

        closeBtn.doClick();
        assertFalse(panel.isVisible());
    }

    @Test
    void testStyleButtonProperties() {
        ViewAppointmentPanel panel = new ViewAppointmentPanel(parent, appointmentController, appointment);

        JButton styled = invokeStyle(panel, "Test", Color.RED);
        assertEquals("Test", styled.getText());
        assertEquals(Color.RED, styled.getBackground());
        assertEquals(UITheme.BUTTON_FONT, styled.getFont());
        assertEquals(UITheme.BACKGROUND_COLOR, styled.getForeground());
        assertTrue(styled.isOpaque());
        assertFalse(styled.isFocusPainted());
        assertFalse(styled.isBorderPainted());
        assertEquals(Cursor.HAND_CURSOR, styled.getCursor().getType());
    }

    // --- Helpers ---
    private JLabel findLabel(Container container, String text) {
        for (Component c : container.getComponents()) {
            if (c instanceof JLabel && ((JLabel) c).getText().equals(text)) {
                return (JLabel) c;
            }
            if (c instanceof Container) {
                JLabel l = findLabel((Container) c, text);
                if (l != null) return l;
            }
        }
        return null;
    }

    private JButton findButton(Container container, String text) {
        for (Component c : container.getComponents()) {
            if (c instanceof JButton && ((JButton) c).getText().equals(text)) {
                return (JButton) c;
            }
            if (c instanceof Container) {
                JButton b = findButton((Container) c, text);
                if (b != null) return b;
            }
        }
        return null;
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

    private JButton invokeStyle(ViewAppointmentPanel panel, String text, Color color) {
        try {
            var m = ViewAppointmentPanel.class.getDeclaredMethod("style", String.class, Color.class);
            m.setAccessible(true);
            return (JButton) m.invoke(panel, text, color);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}