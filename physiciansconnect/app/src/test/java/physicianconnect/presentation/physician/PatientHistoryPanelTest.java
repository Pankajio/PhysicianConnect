package physicianconnect.presentation.physician;

import org.junit.jupiter.api.*;
import org.mockito.*;
import physicianconnect.logic.controller.PatientHistoryController;
import physicianconnect.logic.manager.AppointmentManager;
import physicianconnect.objects.Appointment;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PatientHistoryPanelTest {

    AppointmentManager appointmentManager;
    PatientHistoryController historyController;

    @BeforeEach
    void setup() {
        appointmentManager = mock(AppointmentManager.class);
        historyController = mock(PatientHistoryController.class);
    }

    @Test
    void testPanelPopulatesPatientsAndLoadsHistory() {
        Appointment apt1 = mock(Appointment.class);
        Appointment apt2 = mock(Appointment.class);
        when(apt1.getPatientName()).thenReturn("Alice");
        when(apt2.getPatientName()).thenReturn("Bob");
        when(appointmentManager.getAppointmentsForPhysician("doc1")).thenReturn(List.of(apt1, apt2));
        when(historyController.getPatientHistoryString(eq("doc1"), any())).thenReturn("History for patient");

        PatientHistoryPanel panel = new PatientHistoryPanel(appointmentManager, historyController, "doc1");

        JComboBox<?> combo = (JComboBox<?>) getField(panel, "patientCombo");
        JTextArea area = (JTextArea) getField(panel, "historyArea");

        assertEquals(2, combo.getItemCount());
        assertEquals("Alice", combo.getItemAt(0));
        assertEquals("Bob", combo.getItemAt(1));
        assertEquals("History for patient", area.getText());
    }

    @Test
    void testPanelWithNoPatients() {
        when(appointmentManager.getAppointmentsForPhysician("doc1")).thenReturn(List.of());
        PatientHistoryPanel panel = new PatientHistoryPanel(appointmentManager, historyController, "doc1");

        JComboBox<?> combo = (JComboBox<?>) getField(panel, "patientCombo");
        JTextArea area = (JTextArea) getField(panel, "historyArea");

        assertEquals(0, combo.getItemCount());
        assertEquals("", area.getText());
    }

    @Test
    void testChangingPatientSelectionUpdatesHistory() {
        Appointment apt1 = mock(Appointment.class);
        Appointment apt2 = mock(Appointment.class);
        when(apt1.getPatientName()).thenReturn("Alice");
        when(apt2.getPatientName()).thenReturn("Bob");
        when(appointmentManager.getAppointmentsForPhysician("doc1")).thenReturn(List.of(apt1, apt2));
        when(historyController.getPatientHistoryString("doc1", "Alice")).thenReturn("Alice's history");
        when(historyController.getPatientHistoryString("doc1", "Bob")).thenReturn("Bob's history");

        PatientHistoryPanel panel = new PatientHistoryPanel(appointmentManager, historyController, "doc1");
        JComboBox<String> combo = (JComboBox<String>) getField(panel, "patientCombo");
        JTextArea area = (JTextArea) getField(panel, "historyArea");

        combo.setSelectedItem("Bob");
        // Simulate action event
        for (var l : combo.getActionListeners()) l.actionPerformed(null);
        assertEquals("Bob's history", area.getText());

        combo.setSelectedItem("Alice");
        for (var l : combo.getActionListeners()) l.actionPerformed(null);
        assertEquals("Alice's history", area.getText());
    }

    @Test
    void testNullSelectionClearsHistoryArea() {
        Appointment apt1 = mock(Appointment.class);
        when(apt1.getPatientName()).thenReturn("Alice");
        when(appointmentManager.getAppointmentsForPhysician("doc1")).thenReturn(List.of(apt1));
        PatientHistoryPanel panel = new PatientHistoryPanel(appointmentManager, historyController, "doc1");
        JComboBox<String> combo = (JComboBox<String>) getField(panel, "patientCombo");
        JTextArea area = (JTextArea) getField(panel, "historyArea");

        combo.setSelectedItem(null);
        for (var l : combo.getActionListeners()) l.actionPerformed(null);
        assertEquals("", area.getText());
    }

    // --- Helper for reflection ---
    private Object getField(Object obj, String name) {
        try {
            java.lang.reflect.Field f = obj.getClass().getDeclaredField(name);
            f.setAccessible(true);
            return f.get(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}