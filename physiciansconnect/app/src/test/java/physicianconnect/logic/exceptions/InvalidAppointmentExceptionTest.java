package physicianconnect.logic.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvalidAppointmentExceptionTest {

    @Test
    void testMessageIsSet() {
        InvalidAppointmentException ex = new InvalidAppointmentException("bad appointment");
        assertEquals("bad appointment", ex.getMessage());
    }

    @Test
    void testIsRuntimeException() {
        assertTrue(RuntimeException.class.isAssignableFrom(InvalidAppointmentException.class));
    }
}