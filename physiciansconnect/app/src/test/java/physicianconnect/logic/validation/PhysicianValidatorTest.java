package physicianconnect.logic.validation;

import org.junit.jupiter.api.Test;
import physicianconnect.logic.exceptions.InvalidCredentialException;

import static org.junit.jupiter.api.Assertions.*;

public class PhysicianValidatorTest {

    @Test
    void testValidateEmailValid() {
        assertDoesNotThrow(() -> PhysicianValidator.validateEmail("doc@example.com"));
    }

    @Test
    void testValidateEmailNullThrows() {
        assertThrows(InvalidCredentialException.class, () -> PhysicianValidator.validateEmail(null));
    }

    @Test
    void testValidateEmailBlankThrows() {
        assertThrows(InvalidCredentialException.class, () -> PhysicianValidator.validateEmail("   "));
    }

    @Test
    void testValidateEmailNoAtThrows() {
        assertThrows(InvalidCredentialException.class, () -> PhysicianValidator.validateEmail("doc.example.com"));
    }

    @Test
    void testValidatePasswordValid() {
        assertDoesNotThrow(() -> PhysicianValidator.validatePassword("abcdef"));
    }

    @Test
    void testValidatePasswordNullThrows() {
        assertThrows(InvalidCredentialException.class, () -> PhysicianValidator.validatePassword(null));
    }

    @Test
    void testValidatePasswordTooShortThrows() {
        assertThrows(InvalidCredentialException.class, () -> PhysicianValidator.validatePassword("abc"));
    }
}