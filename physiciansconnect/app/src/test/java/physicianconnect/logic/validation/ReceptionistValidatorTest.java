package physicianconnect.logic.validation;

import org.junit.jupiter.api.Test;
import physicianconnect.logic.exceptions.InvalidCredentialException;

import static org.junit.jupiter.api.Assertions.*;

public class ReceptionistValidatorTest {

    @Test
    void testValidateEmailValid() {
        assertDoesNotThrow(() -> ReceptionistValidator.validateEmail("rec@example.com"));
    }

    @Test
    void testValidateEmailNullThrows() {
        assertThrows(InvalidCredentialException.class, () -> ReceptionistValidator.validateEmail(null));
    }

    @Test
    void testValidateEmailBlankThrows() {
        assertThrows(InvalidCredentialException.class, () -> ReceptionistValidator.validateEmail("   "));
    }

    @Test
    void testValidateEmailNoAtThrows() {
        assertThrows(InvalidCredentialException.class, () -> ReceptionistValidator.validateEmail("rec.example.com"));
    }

    @Test
    void testValidatePasswordValid() {
        assertDoesNotThrow(() -> ReceptionistValidator.validatePassword("abcdef"));
    }

    @Test
    void testValidatePasswordNullThrows() {
        assertThrows(InvalidCredentialException.class, () -> ReceptionistValidator.validatePassword(null));
    }

    @Test
    void testValidatePasswordTooShortThrows() {
        assertThrows(InvalidCredentialException.class, () -> ReceptionistValidator.validatePassword("abc"));
    }

    @Test
    void testValidateRegistrationValid() {
        assertDoesNotThrow(() -> ReceptionistValidator.validateRegistration("Alice", "alice@email.com", "abcdef", "abcdef"));
    }

    @Test
    void testValidateRegistrationBlankNameThrows() {
        assertThrows(InvalidCredentialException.class, () -> ReceptionistValidator.validateRegistration("   ", "a@b.com", "abcdef", "abcdef"));
    }

    @Test
    void testValidateRegistrationPasswordMismatchThrows() {
        assertThrows(InvalidCredentialException.class, () -> ReceptionistValidator.validateRegistration("Alice", "a@b.com", "abcdef", "123456"));
    }
}