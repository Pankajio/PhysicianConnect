package physicianconnect.logic.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvalidReferralExceptionTest {

    @Test
    void testMessageIsSet() {
        InvalidReferralException ex = new InvalidReferralException("bad referral");
        assertEquals("bad referral", ex.getMessage());
    }

    @Test
    void testIsCheckedException() {
        assertTrue(Exception.class.isAssignableFrom(InvalidReferralException.class));
        assertFalse(RuntimeException.class.isAssignableFrom(InvalidReferralException.class));
    }
}