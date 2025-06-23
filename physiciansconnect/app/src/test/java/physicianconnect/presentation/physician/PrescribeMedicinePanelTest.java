package physicianconnect.presentation.physician;

import org.junit.jupiter.api.*;
import org.mockito.*;
import physicianconnect.logic.controller.PrescriptionController;
import physicianconnect.logic.exceptions.InvalidPrescriptionException;
import physicianconnect.logic.manager.AppointmentManager;
import physicianconnect.objects.Appointment;
import physicianconnect.objects.Medication;
import physicianconnect.persistence.interfaces.MedicationPersistence;
import physicianconnect.presentation.NotificationBanner;
import physicianconnect.presentation.NotificationPanel;
import physicianconnect.presentation.config.UIConfig;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PrescribeMedicinePanelTest {

    AppointmentManager appointmentManager;
    MedicationPersistence medicationPersistence;
    PrescriptionController prescriptionController;
    NotificationPanel notificationPanel;
    NotificationBanner notificationBanner;
    Runnable onPrescriptionAdded;
    JFrame parentFrame;

    @BeforeEach
    void setup() {
        appointmentManager = mock(AppointmentManager.class);
        medicationPersistence = mock(MedicationPersistence.class);
        prescriptionController = mock(PrescriptionController.class);
        notificationPanel = mock(NotificationPanel.class);
        notificationBanner = mock(NotificationBanner.class);
        onPrescriptionAdded = mock(Runnable.class);
        parentFrame = new JFrame();

        Appointment apt = mock(Appointment.class);
        when(apt.getPatientName()).thenReturn("Alice");
        when(appointmentManager.getAppointmentsForPhysician(anyString())).thenReturn(List.of(apt));

        Medication med = mock(Medication.class);
        when(med.getName()).thenReturn("Ibuprofen");
        when(med.getDosage()).thenReturn("200mg");
        when(med.getDefaultFrequency()).thenReturn("2x/day");
        when(med.getDefaultNotes()).thenReturn("Take with food");
        when(medicationPersistence.getAllMedications()).thenReturn(List.of(med));
    }

    @Test
    void testPanelFieldsPopulatedOnConstruct() {
        PrescribeMedicinePanel panel = new PrescribeMedicinePanel(
                appointmentManager, medicationPersistence, prescriptionController,
                "doc1", onPrescriptionAdded, notificationPanel, notificationBanner, parentFrame);

        JComboBox<String> patientCombo = (JComboBox<String>) TestUtil.getField(panel, "patientCombo");
        JComboBox<?> medicineCombo = (JComboBox<?>) TestUtil.getField(panel, "medicineCombo");
        JTextField dosageField = (JTextField) TestUtil.getField(panel, "dosageField");
        JTextField frequencyField = (JTextField) TestUtil.getField(panel, "frequencyField");
        JTextArea notesArea = (JTextArea) TestUtil.getField(panel, "notesArea");

        assertEquals("Alice", patientCombo.getSelectedItem());
        assertEquals("Ibuprofen", ((Medication)medicineCombo.getSelectedItem()).getName());
        assertEquals("200mg", dosageField.getText());
        assertEquals("2x/day", frequencyField.getText());
        assertEquals("Take with food", notesArea.getText());
    }

    @Test
    void testRequiredFieldValidationShowsDialog() {
        PrescribeMedicinePanel panel = new PrescribeMedicinePanel(
                appointmentManager, medicationPersistence, prescriptionController,
                "doc1", onPrescriptionAdded, notificationPanel, notificationBanner, parentFrame);

        JComboBox<String> patientCombo = (JComboBox<String>) TestUtil.getField(panel, "patientCombo");
        JComboBox<?> medicineCombo = (JComboBox<?>) TestUtil.getField(panel, "medicineCombo");
        JTextField dosageField = (JTextField) TestUtil.getField(panel, "dosageField");
        JTextField frequencyField = (JTextField) TestUtil.getField(panel, "frequencyField");
        JButton prescribeButton = (JButton) TestUtil.getField(panel, "prescribeButton");

        // Clear required fields
        patientCombo.setSelectedItem(null);
        medicineCombo.setSelectedItem(null);
        dosageField.setText("");
        frequencyField.setText("");

        try (MockedStatic<JOptionPane> paneMock = mockStatic(JOptionPane.class)) {
            prescribeButton.doClick();
            paneMock.verify(() -> JOptionPane.showMessageDialog(
                    any(), eq(UIConfig.ERROR_REQUIRED_FIELD), eq(UIConfig.ERROR_DIALOG_TITLE), eq(JOptionPane.ERROR_MESSAGE)));
        }
    }

    @Test
    void testSuccessfulPrescriptionShowsNotificationsAndClosesDialog() throws Exception {
        PrescribeMedicinePanel panel = new PrescribeMedicinePanel(
                appointmentManager, medicationPersistence, prescriptionController,
                "doc1", onPrescriptionAdded, notificationPanel, notificationBanner, parentFrame);

        JComboBox<String> patientCombo = (JComboBox<String>) TestUtil.getField(panel, "patientCombo");
        JComboBox<?> medicineCombo = (JComboBox<?>) TestUtil.getField(panel, "medicineCombo");
        JTextField dosageField = (JTextField) TestUtil.getField(panel, "dosageField");
        JTextField frequencyField = (JTextField) TestUtil.getField(panel, "frequencyField");
        JTextArea notesArea = (JTextArea) TestUtil.getField(panel, "notesArea");
        JButton prescribeButton = (JButton) TestUtil.getField(panel, "prescribeButton");

        // All fields valid
        patientCombo.setSelectedItem("Alice");
        Medication med = (Medication) medicineCombo.getSelectedItem();
        dosageField.setText("200mg");
        frequencyField.setText("2x/day");
        notesArea.setText("Take with food");

        try (MockedStatic<JOptionPane> paneMock = mockStatic(JOptionPane.class)) {
            // Simulate parent dialog
            JDialog dialog = new JDialog();
            dialog.add(panel);
            dialog.pack();
            dialog.setVisible(true);

            prescribeButton.doClick();

            verify(prescriptionController).createPrescription(
                    eq("doc1"), eq("Alice"), eq("Ibuprofen"), eq("200mg"), eq("200mg"), eq("2x/day"), eq("Take with food"));
            paneMock.verify(() -> JOptionPane.showMessageDialog(
                    any(), contains("New prescription added for Alice"), eq(UIConfig.SUCCESS_DIALOG_TITLE), eq(JOptionPane.INFORMATION_MESSAGE)));
            verify(notificationBanner).show(contains("New prescription added for Alice"), any());
            verify(notificationPanel).addNotification(contains("New prescription added for Alice"), eq("New Prescription!"));
//            verify(onPrescriptionAdded).run();
            assertFalse(dialog.isVisible());
        }
    }

    @Test
    void testInvalidPrescriptionShowsErrorDialog() throws Exception {
        doThrow(new InvalidPrescriptionException("Invalid Rx")).when(prescriptionController)
                .createPrescription(any(), any(), any(), any(), any(), any(), any());

        PrescribeMedicinePanel panel = new PrescribeMedicinePanel(
                appointmentManager, medicationPersistence, prescriptionController,
                "doc1", onPrescriptionAdded, notificationPanel, notificationBanner, parentFrame);

        JButton prescribeButton = (JButton) TestUtil.getField(panel, "prescribeButton");

        try (MockedStatic<JOptionPane> paneMock = mockStatic(JOptionPane.class)) {
            prescribeButton.doClick();
            paneMock.verify(() -> JOptionPane.showMessageDialog(
                    any(), eq("Invalid Rx"), eq(UIConfig.ERROR_DIALOG_TITLE), eq(JOptionPane.ERROR_MESSAGE)));
        }
    }

    @Test
    void testUnexpectedExceptionShowsErrorDialog() throws Exception {
        doThrow(new RuntimeException("fail")).when(prescriptionController)
                .createPrescription(any(), any(), any(), any(), any(), any(), any());

        PrescribeMedicinePanel panel = new PrescribeMedicinePanel(
                appointmentManager, medicationPersistence, prescriptionController,
                "doc1", onPrescriptionAdded, notificationPanel, notificationBanner, parentFrame);

        JButton prescribeButton = (JButton) TestUtil.getField(panel, "prescribeButton");

        try (MockedStatic<JOptionPane> paneMock = mockStatic(JOptionPane.class)) {
            prescribeButton.doClick();
            paneMock.verify(() -> JOptionPane.showMessageDialog(
                    any(), contains("fail"), eq(UIConfig.ERROR_DIALOG_TITLE), eq(JOptionPane.ERROR_MESSAGE)));
        }
    }

    // --- Helper for reflection ---
    static class TestUtil {
        static Object getField(Object obj, String name) {
            try {
                java.lang.reflect.Field f = obj.getClass().getDeclaredField(name);
                f.setAccessible(true);
                return f.get(obj);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}