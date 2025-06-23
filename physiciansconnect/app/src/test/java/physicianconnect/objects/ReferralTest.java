package physicianconnect.objects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReferralTest {

    @Test
    void testConstructorAndGetters() {
        Referral r = new Referral(1, "doc1", "Alice", "Specialist", "details", "2025-06-10");
        assertEquals(1, r.getId());
        assertEquals("doc1", r.getPhysicianId());
        assertEquals("Alice", r.getPatientName());
        assertEquals("Specialist", r.getReferralType());
        assertEquals("details", r.getDetails());
        assertEquals("2025-06-10", r.getDateCreated());
    }
}