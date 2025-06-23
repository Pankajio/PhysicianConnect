package physicianconnect.presentation.physician;

import org.junit.jupiter.api.*;
import org.mockito.*;
import physicianconnect.logic.manager.AppointmentManager;
import physicianconnect.logic.manager.PhysicianManager;
import physicianconnect.objects.Physician;
import physicianconnect.presentation.config.UIConfig;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PhysicianProfilePanelTest {

    PhysicianManager physicianManager;
    AppointmentManager appointmentManager;
    Physician physician;
    Runnable onProfileUpdated;
    Runnable logoutCallback;

    @BeforeEach
    void setup() {
        physicianManager = mock(PhysicianManager.class);
        appointmentManager = mock(AppointmentManager.class);
        physician = mock(Physician.class);
        onProfileUpdated = mock(Runnable.class);
        logoutCallback = mock(Runnable.class);

        when(physician.getId()).thenReturn("doc1");
        when(physician.getName()).thenReturn("Dr. Test");
        when(physician.getEmail()).thenReturn("dr@test.com");
        when(physician.getSpecialty()).thenReturn("Cardiology");
        when(physician.getOfficeHours()).thenReturn("9-5");
        when(physician.getPhone()).thenReturn("555-1234");
        when(physician.getOfficeAddress()).thenReturn("123 Main St");
        when(physician.isNotifyAppointment()).thenReturn(true);
        when(physician.isNotifyBilling()).thenReturn(false);
        when(physician.isNotifyMessages()).thenReturn(true);
    }

    @Test
    void testPanelFieldsPopulatedOnConstruct() {
        PhysicianProfilePanel panel = new PhysicianProfilePanel(
                physician, physicianManager, appointmentManager, null, onProfileUpdated, logoutCallback);

        JTextField nameField = (JTextField) TestUtil.getField(panel, "nameField");
        JTextField emailField = (JTextField) TestUtil.getField(panel, "emailField");
        JTextField specialtyField = (JTextField) TestUtil.getField(panel, "specialtyField");
        JTextField officeHoursField = (JTextField) TestUtil.getField(panel, "officeHoursField");
        JTextField phoneField = (JTextField) TestUtil.getField(panel, "phoneField");
        JTextField addressField = (JTextField) TestUtil.getField(panel, "addressField");
        JCheckBox notifyAppointments = (JCheckBox) TestUtil.getField(panel, "notifyAppointments");
        JCheckBox notifyBilling = (JCheckBox) TestUtil.getField(panel, "notifyBilling");
        JCheckBox notifyMessages = (JCheckBox) TestUtil.getField(panel, "notifyMessages");

        assertEquals("Dr. Test", nameField.getText());
        assertEquals("dr@test.com", emailField.getText());
        assertEquals("Cardiology", specialtyField.getText());
        assertEquals("9-5", officeHoursField.getText());
        assertEquals("555-1234", phoneField.getText());
        assertEquals("123 Main St", addressField.getText());
        assertTrue(notifyAppointments.isSelected());
        assertFalse(notifyBilling.isSelected());
        assertTrue(notifyMessages.isSelected());
    }

    @Test
    void testEditAndSaveValid() {
        PhysicianProfilePanel panel = new PhysicianProfilePanel(
                physician, physicianManager, appointmentManager, null, onProfileUpdated, logoutCallback);

        JButton editButton = (JButton) TestUtil.getField(panel, "editButton");
        JButton saveButton = (JButton) TestUtil.getField(panel, "saveButton");
        JTextField nameField = (JTextField) TestUtil.getField(panel, "nameField");

        // Enter edit mode
        editButton.doClick();
        assertTrue(nameField.isEditable());
        // Change name and save
        nameField.setText("Dr. Changed");
        doNothing().when(physicianManager).validateAndUpdatePhysician(any(), any(), any(), any(), any(), any(), anyBoolean(), anyBoolean(), anyBoolean());
        saveButton.doClick();

        verify(physicianManager).validateAndUpdatePhysician(
                eq(physician), eq("Dr. Changed"), any(), any(), any(), any(), anyBoolean(), anyBoolean(), anyBoolean());
        verify(onProfileUpdated).run();
        assertFalse(nameField.isEditable());
    }

    @Test
    void testEditAndSaveInvalidShowsDialog() {
        PhysicianProfilePanel panel = new PhysicianProfilePanel(
                physician, physicianManager, appointmentManager, null, onProfileUpdated, logoutCallback);

        JButton editButton = (JButton) TestUtil.getField(panel, "editButton");
        JButton saveButton = (JButton) TestUtil.getField(panel, "saveButton");
        JTextField nameField = (JTextField) TestUtil.getField(panel, "nameField");

        editButton.doClick();
        doThrow(new IllegalArgumentException("Invalid")).when(physicianManager)
                .validateAndUpdatePhysician(any(), any(), any(), any(), any(), any(), anyBoolean(), anyBoolean(), anyBoolean());

        try (MockedStatic<JOptionPane> paneMock = mockStatic(JOptionPane.class)) {
            saveButton.doClick();
            paneMock.verify(() -> JOptionPane.showMessageDialog(any(), eq("Invalid"), eq(UIConfig.VALIDATION_ERROR_TITLE), eq(JOptionPane.ERROR_MESSAGE)));
        }
    }

    @Test
    void testCancelRestoresOriginalValues() {
        PhysicianProfilePanel panel = new PhysicianProfilePanel(
                physician, physicianManager, appointmentManager, null, onProfileUpdated, logoutCallback);

        JButton editButton = (JButton) TestUtil.getField(panel, "editButton");
        JButton cancelButton = (JButton) TestUtil.getField(panel, "cancelButton");
        JTextField nameField = (JTextField) TestUtil.getField(panel, "nameField");

        editButton.doClick();
        nameField.setText("Dr. Changed");
        cancelButton.doClick();
        assertEquals("Dr. Test", nameField.getText());
        assertFalse(nameField.isEditable());
    }

    @Test
    void testSignOutButtonClosesWindowAndRunsLogout() {
        PhysicianProfilePanel panel = new PhysicianProfilePanel(
                physician, physicianManager, appointmentManager, null, onProfileUpdated, logoutCallback);

        JButton signOutButton = (JButton) TestUtil.getField(panel, "signOutButton");
        JFrame frame = new JFrame();
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);

        signOutButton.doClick();
        assertFalse(frame.isVisible());
        verify(logoutCallback).run();
    }

    // this test is for a prior implementation, and does not reflect the current implementation.
//    @Test
//    void testChangePhotoButtonShowsFileChooserAndHandlesIOException() {
//        File fakeFile = new File("notfound.png");
//
//        try (
//                MockedConstruction<JFileChooser> chooserMock = mockConstruction(JFileChooser.class, (mock, context) -> {
//                    when(mock.showOpenDialog(any())).thenReturn(JFileChooser.APPROVE_OPTION);
//                    when(mock.getSelectedFile()).thenReturn(fakeFile);
//                });
//                MockedStatic<JOptionPane> paneMock = mockStatic(JOptionPane.class)
//        ) {
//            PhysicianProfilePanel panel = new PhysicianProfilePanel(
//                    physician, physicianManager, appointmentManager, null, onProfileUpdated, logoutCallback);
//
//            JButton changePhotoButton = (JButton) TestUtil.getField(panel, "changePhotoButton");
//
//            doThrow(new IOException("fail")).when(physicianManager).uploadProfilePhoto(anyString(), any());
//
//            changePhotoButton.doClick();
//
//            paneMock.verify(() -> JOptionPane.showMessageDialog(
//                    any(), contains("fail"), eq("Error"), eq(JOptionPane.ERROR_MESSAGE)
//            ));
//        }
//    }



    @Test
    void testLoadProfilePhotoWithAndWithoutFile() {
        PhysicianProfilePanel panel = new PhysicianProfilePanel(
                physician, physicianManager, appointmentManager, null, onProfileUpdated, logoutCallback);

        // Should load placeholder if file does not exist
        JLabel photoLabel = (JLabel) TestUtil.getField(panel, "photoLabel");
        assertNotNull(photoLabel.getIcon());
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