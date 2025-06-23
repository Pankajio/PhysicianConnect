package physicianconnect.logic.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvalidPrescriptionExceptionTest {

    @Test
    void testMessageIsSet() {
        InvalidPrescriptionException ex = new InvalidPrescriptionException("bad prescription");
        assertEquals("bad prescription", ex.getMessage());
    }

    @Test
    void testIsCheckedException() {
        assertTrue(Exception.class.isAssignableFrom(InvalidPrescriptionException.class));
        assertFalse(RuntimeException.class.isAssignableFrom(InvalidPrescriptionException.class));
    }
}