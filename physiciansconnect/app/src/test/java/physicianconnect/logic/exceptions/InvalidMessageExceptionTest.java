package physicianconnect.logic.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvalidMessageExceptionTest {

    @Test
    void testMessageIsSet() {
        InvalidMessageException ex = new InvalidMessageException("bad message");
        assertEquals("bad message", ex.getMessage());
    }

    @Test
    void testIsCheckedException() {
        assertTrue(Exception.class.isAssignableFrom(InvalidMessageException.class));
        assertFalse(RuntimeException.class.isAssignableFrom(InvalidMessageException.class));
    }
}