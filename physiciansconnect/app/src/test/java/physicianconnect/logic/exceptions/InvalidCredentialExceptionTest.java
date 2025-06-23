package physicianconnect.logic.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvalidCredentialExceptionTest {

    @Test
    void testMessageIsSet() {
        InvalidCredentialException ex = new InvalidCredentialException("bad credentials");
        assertEquals("bad credentials", ex.getMessage());
    }

    @Test
    void testIsCheckedException() {
        assertTrue(Exception.class.isAssignableFrom(InvalidCredentialException.class));
        assertFalse(RuntimeException.class.isAssignableFrom(InvalidCredentialException.class));
    }
}