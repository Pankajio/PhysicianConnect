package physicianconnect.objects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MedicationTest {

    @Test
    void testConstructorAndGetters() {
        Medication m = new Medication("Aspirin", "10mg", "daily", "Take with food");
        assertEquals("Aspirin", m.getName());
        assertEquals("10mg", m.getDosage());
        assertEquals("daily", m.getDefaultFrequency());
        assertEquals("Take with food", m.getDefaultNotes());
    }

    @Test
    void testToString() {
        Medication m = new Medication("Ibuprofen", "200mg", "twice daily", "");
        assertEquals("Ibuprofen - 200mg", m.toString());
    }
}