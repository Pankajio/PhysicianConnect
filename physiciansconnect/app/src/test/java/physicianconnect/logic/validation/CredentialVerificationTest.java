package physicianconnect.logic.validation;

import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import physicianconnect.logic.manager.PhysicianManager;
import physicianconnect.logic.manager.ReceptionistManager;
import physicianconnect.presentation.config.UIConfig;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CredentialVerificationTest {

    PhysicianManager physicianManager;
    ReceptionistManager receptionistManager;
    JDialog dialog;
    CredentialVerification verifier;

    @BeforeEach
    void setup() {
        physicianManager = mock(PhysicianManager.class);
        receptionistManager = mock(ReceptionistManager.class);
        dialog = mock(JDialog.class);
        verifier = new CredentialVerification(physicianManager, receptionistManager, dialog);
    }

    @Test
    void testEmptyFields() {
        try (MockedStatic<JOptionPane> paneMock = mockStatic(JOptionPane.class)) {
            boolean result = verifier.verifySignUpData("", "a@b.com", "pass123", "pass123");
            assertFalse(result);
            paneMock.verify(() -> JOptionPane.showMessageDialog(dialog, UIConfig.ERROR_REQUIRED_FIELD,
                    UIConfig.ERROR_DIALOG_TITLE, JOptionPane.ERROR_MESSAGE));
        }
    }

    @Test
    void testInvalidEmail() {
        try (MockedStatic<JOptionPane> paneMock = mockStatic(JOptionPane.class)) {
            boolean result = verifier.verifySignUpData("Name", "not-an-email", "pass123", "pass123");
            assertFalse(result);
            paneMock.verify(() -> JOptionPane.showMessageDialog(dialog, UIConfig.ERROR_INVALID_EMAIL,
                    "Error", JOptionPane.ERROR_MESSAGE));
        }
    }

    @Test
    void testShortPassword() {
        try (MockedStatic<JOptionPane> paneMock = mockStatic(JOptionPane.class)) {
            boolean result = verifier.verifySignUpData("Name", "a@b.com", "123", "123");
            assertFalse(result);
            paneMock.verify(() -> JOptionPane.showMessageDialog(dialog, UIConfig.ERROR_PASSWORD_LENGTH,
                    UIConfig.ERROR_DIALOG_TITLE, JOptionPane.ERROR_MESSAGE));
        }
    }

    @Test
    void testPasswordMismatch() {
        try (MockedStatic<JOptionPane> paneMock = mockStatic(JOptionPane.class)) {
            boolean result = verifier.verifySignUpData("Name", "a@b.com", "pass123", "different");
            assertFalse(result);
            paneMock.verify(() -> JOptionPane.showMessageDialog(dialog, UIConfig.ERROR_PASSWORD_MISMATCH,
                    UIConfig.ERROR_DIALOG_TITLE, JOptionPane.ERROR_MESSAGE));
        }
    }

    @Test
    void testEmailExistsPhysician() {
        when(physicianManager.getPhysicianByEmail("a@b.com"))
                .thenReturn(mock(physicianconnect.objects.Physician.class));
        when(receptionistManager.getReceptionistByEmail("a@b.com")).thenReturn(null);
        try (MockedStatic<JOptionPane> paneMock = mockStatic(JOptionPane.class)) {
            boolean result = verifier.verifySignUpData("Name", "a@b.com", "pass123", "pass123");
            assertFalse(result);
            paneMock.verify(() -> JOptionPane.showMessageDialog(dialog, UIConfig.ERROR_EMAIL_EXISTS,
                    UIConfig.ERROR_DIALOG_TITLE, JOptionPane.ERROR_MESSAGE));
        }
    }

    @Test
    void testEmailExistsReceptionist() {
        when(physicianManager.getPhysicianByEmail("a@b.com")).thenReturn(null);
        when(receptionistManager.getReceptionistByEmail("a@b.com"))
                .thenReturn(mock(physicianconnect.objects.Receptionist.class));
        try (MockedStatic<JOptionPane> paneMock = mockStatic(JOptionPane.class)) {
            boolean result = verifier.verifySignUpData("Name", "a@b.com", "pass123", "pass123");
            assertFalse(result);
            paneMock.verify(() -> JOptionPane.showMessageDialog(dialog, UIConfig.ERROR_EMAIL_EXISTS,
                    UIConfig.ERROR_DIALOG_TITLE, JOptionPane.ERROR_MESSAGE));
        }
    }

    @Test
    void testValidDataReturnsTrue() {
        when(physicianManager.getPhysicianByEmail("a@b.com")).thenReturn(null);
        when(receptionistManager.getReceptionistByEmail("a@b.com")).thenReturn(null);
        boolean result = verifier.verifySignUpData("Name", "a@b.com", "pass123", "pass123");
        assertTrue(result);
    }
}