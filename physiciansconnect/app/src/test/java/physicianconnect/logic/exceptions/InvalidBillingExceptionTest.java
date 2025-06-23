package physicianconnect.logic.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvalidBillingExceptionTest {

    @Test
    void testMessageIsSet() {
        InvalidBillingException ex = new InvalidBillingException("bad billing");
        assertEquals("bad billing", ex.getMessage());
    }

    @Test
    void testIsRuntimeException() {
        assertTrue(RuntimeException.class.isAssignableFrom(InvalidBillingException.class));
    }
}