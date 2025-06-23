package physicianconnect.presentation.physician;

import org.junit.jupiter.api.*;
import org.mockito.*;
import physicianconnect.logic.manager.ReferralManager;
import physicianconnect.objects.Referral;
import physicianconnect.presentation.NotificationBanner;
import physicianconnect.presentation.NotificationPanel;
import physicianconnect.presentation.config.UIConfig;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReferralPanelTest {

    ReferralManager referralManager;
    NotificationPanel notificationPanel;
    NotificationBanner notificationBanner;
    JFrame parentFrame;

    @BeforeEach
    void setup() {
        referralManager = mock(ReferralManager.class);
        notificationPanel = mock(NotificationPanel.class);
        notificationBanner = mock(NotificationBanner.class);
        parentFrame = new JFrame();
    }

    @Test
    void testPanelPopulatesPatientsAndReferralList() {
        when(referralManager.getReferralsForPatient("Alice")).thenReturn(List.of(
                new Referral(1, "doc1", "Alice", "Cardiology", "See specialist", "2024-01-01")
        ));

        ReferralPanel panel = new ReferralPanel(referralManager, "doc1", List.of("Alice"),
                notificationPanel, notificationBanner, parentFrame);

        JComboBox<?> combo = (JComboBox<?>) getField(panel, "patientCombo");
        JTextArea area = (JTextArea) getField(panel, "referralListArea");

        assertEquals(1, combo.getItemCount());
        assertEquals("Alice", combo.getItemAt(0));
        assertTrue(area.getText().contains("Cardiology"));
    }

    @Test
    void testCreateReferralWithValidFields() {
        ReferralPanel panel = new ReferralPanel(referralManager, "doc1", List.of("Alice"),
                notificationPanel, notificationBanner, parentFrame);

        JComboBox<String> combo = (JComboBox<String>) getField(panel, "patientCombo");
        JTextField typeField = (JTextField) getField(panel, "typeField");
        JTextArea detailsArea = (JTextArea) getField(panel, "detailsArea");
        JButton createButton = (JButton) getField(panel, "createButton");

        combo.setSelectedItem("Alice");
        typeField.setText("Cardiology");
        detailsArea.setText("See specialist");

        try (MockedStatic<JOptionPane> paneMock = mockStatic(JOptionPane.class)) {
            createButton.doClick();
            verify(referralManager).addReferral(any(Referral.class));
            paneMock.verify(() -> JOptionPane.showMessageDialog(
                    any(), contains("New referral created for Alice"), eq(UIConfig.SUCCESS_DIALOG_TITLE), eq(JOptionPane.INFORMATION_MESSAGE)));
            verify(notificationPanel).addNotification(contains("New referral created for Alice"), eq("New Referral!"));
            verify(notificationBanner).show(contains("New referral created for Alice"), any());
        }
    }

    @Test
    void testCreateReferralWithMissingFieldsShowsError() {
        ReferralPanel panel = new ReferralPanel(referralManager, "doc1", List.of("Alice"),
                notificationPanel, notificationBanner, parentFrame);

        JComboBox<String> combo = (JComboBox<String>) getField(panel, "patientCombo");
        JTextField typeField = (JTextField) getField(panel, "typeField");
        JButton createButton = (JButton) getField(panel, "createButton");

        combo.setSelectedItem(null);
        typeField.setText("");

        try (MockedStatic<JOptionPane> paneMock = mockStatic(JOptionPane.class)) {
            createButton.doClick();
            paneMock.verify(() -> JOptionPane.showMessageDialog(
                    any(), eq(UIConfig.ERROR_REQUIRED_FIELD_REFERRAL), eq(UIConfig.ERROR_DIALOG_TITLE), eq(JOptionPane.ERROR_MESSAGE)));
            verify(referralManager, never()).addReferral(any());
        }
    }

    @Test
    void testUpdateReferralListOnPatientChange() {
        when(referralManager.getReferralsForPatient("Alice")).thenReturn(List.of(
                new Referral(1, "doc1", "Alice", "Cardiology", "See specialist", "2024-01-01")
        ));
        when(referralManager.getReferralsForPatient("Bob")).thenReturn(List.of(
                new Referral(2, "doc1", "Bob", "Neurology", "MRI scan", "2024-02-01")
        ));

        ReferralPanel panel = new ReferralPanel(referralManager, "doc1", List.of("Alice", "Bob"),
                notificationPanel, notificationBanner, parentFrame);

        JComboBox<String> combo = (JComboBox<String>) getField(panel, "patientCombo");
        JTextArea area = (JTextArea) getField(panel, "referralListArea");

        combo.setSelectedItem("Bob");
        for (var l : combo.getActionListeners()) l.actionPerformed(null);
        assertTrue(area.getText().contains("Neurology"));
        assertFalse(area.getText().contains("Cardiology"));
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