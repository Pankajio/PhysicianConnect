package physicianconnect.presentation.receptionist;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import physicianconnect.logic.manager.ReceptionistManager;
import physicianconnect.objects.Receptionist;
import physicianconnect.presentation.config.UIConfig;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import org.mockito.MockedStatic;

class ReceptionistProfilePanelTest {

    Receptionist receptionist;
    ReceptionistManager receptionistManager;

    @BeforeEach
    void setup() {
        receptionist = mock(Receptionist.class);
        receptionistManager = mock(ReceptionistManager.class);
        when(receptionist.getId()).thenReturn("123");
        when(receptionist.getName()).thenReturn("Jane Doe");
        when(receptionist.getEmail()).thenReturn("jane@example.com");
    }

    @Test
    void testInitialState() {
        ReceptionistProfilePanel panel = new ReceptionistProfilePanel(receptionist, receptionistManager, null, null);

        JTextField nameField = (JTextField) getField(panel, "nameField");
        JTextField emailField = (JTextField) getField(panel, "emailField");
        JButton editButton = (JButton) getField(panel, "editButton");
        JButton saveButton = (JButton) getField(panel, "saveButton");
        JButton cancelButton = (JButton) getField(panel, "cancelButton");

        assertEquals("Jane Doe", nameField.getText());
        assertEquals("jane@example.com", emailField.getText());
        assertFalse(nameField.isEditable());
        assertFalse(saveButton.isVisible());
        assertFalse(cancelButton.isVisible());
        assertTrue(editButton.isVisible());
    }

    @Test
    void testEditAndCancel() {
        ReceptionistProfilePanel panel = new ReceptionistProfilePanel(receptionist, receptionistManager, null, null);

        JButton editButton = (JButton) getField(panel, "editButton");
        JButton cancelButton = (JButton) getField(panel, "cancelButton");
        JButton saveButton = (JButton) getField(panel, "saveButton");
        JTextField nameField = (JTextField) getField(panel, "nameField");

        // Click edit
        editButton.doClick();
        assertTrue(nameField.isEditable());
        assertTrue(saveButton.isVisible());
        assertTrue(cancelButton.isVisible());
        assertFalse(editButton.isVisible());

        // Change name and cancel
        nameField.setText("Changed Name");
        cancelButton.doClick();
        assertEquals("Jane Doe", nameField.getText());
        assertFalse(nameField.isEditable());
        assertFalse(saveButton.isVisible());
        assertFalse(cancelButton.isVisible());
        assertTrue(editButton.isVisible());
    }

    @Test
    void testSignOutButtonCallsLogoutCallback() {
        Runnable logoutCallback = mock(Runnable.class);
        ReceptionistProfilePanel panel = new ReceptionistProfilePanel(receptionist, receptionistManager, logoutCallback, null);

        JButton signOutButton = (JButton) getField(panel, "signOutButton");
        signOutButton.doClick();
        verify(logoutCallback).run();
    }

    @Test
    void testSignOutButtonDisposesWindowAndCallsLogout() {
        Runnable logoutCallback = mock(Runnable.class);
        ReceptionistProfilePanel panel = new ReceptionistProfilePanel(receptionist, receptionistManager, logoutCallback, null);

        // Create a fake window and add the panel to it
        JFrame frame = new JFrame();
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);

        JButton signOutButton = (JButton) getField(panel, "signOutButton");
        signOutButton.doClick();

        // The frame should be disposed and callback called
        assertFalse(frame.isDisplayable());
        verify(logoutCallback).run();
    }

    @Test
    void testLoadProfilePhotoFileNotExists() throws Exception {
        ReceptionistProfilePanel panel = new ReceptionistProfilePanel(receptionist, receptionistManager, null, null);

        // Ensure file does not exist
        File file = new File("src/main/resources/profile_photos/r_123.png");
        if (file.exists()) file.delete();

        Method m = ReceptionistProfilePanel.class.getDeclaredMethod("loadProfilePhoto", String.class);
        m.setAccessible(true);
        m.invoke(panel, "123");

        JLabel photoLabel = (JLabel) getField(panel, "photoLabel");
        assertNotNull(photoLabel.getIcon());
    }

    // --- Helpers ---
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