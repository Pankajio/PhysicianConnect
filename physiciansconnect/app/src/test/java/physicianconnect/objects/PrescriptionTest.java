package physicianconnect.objects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PrescriptionTest {

    @Test
    void testConstructorAndGetters() {
        Prescription p = new Prescription(1, "doc1", "Alice", "Aspirin", "10mg", "10mg", "daily", "notes", "2025-06-10");
        assertEquals(1, p.getId());
        assertEquals("doc1", p.getPhysicianId());
        assertEquals("Alice", p.getPatientName());
        assertEquals("Aspirin", p.getMedicationName());
        assertEquals("10mg", p.getDefaultDosage());
        assertEquals("10mg", p.getDosage());
        assertEquals("daily", p.getFrequency());
        assertEquals("notes", p.getNotes());
        assertEquals("2025-06-10", p.getDatePrescribed());
    }

    @Test
    void testToString() {
        Prescription p = new Prescription(1, "doc1", "Alice", "Aspirin", "10mg", "10mg", "daily", "notes", "2025-06-10");
        assertTrue(p.toString().contains("Aspirin"));
        assertTrue(p.toString().contains("10mg"));
        assertTrue(p.toString().contains("Frequency: daily"));
        assertTrue(p.toString().contains("Notes: notes"));
    }
}