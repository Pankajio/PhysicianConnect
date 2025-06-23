package physicianconnect.objects;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentTest {

    @Test
    void testConstructorsAndGetters() {
        LocalDateTime now = LocalDateTime.now();
        Appointment a1 = new Appointment(5, "doc1", "Alice", now, "notes");
        assertEquals(5, a1.getId());
        assertEquals("doc1", a1.getPhysicianId());
        assertEquals("Alice", a1.getPatientName());
        assertEquals(now, a1.getDateTime());
        assertEquals("notes", a1.getNotes());

        Appointment a2 = new Appointment("doc2", "Bob", now);
        assertEquals("doc2", a2.getPhysicianId());
        assertEquals("Bob", a2.getPatientName());
        assertEquals(now, a2.getDateTime());
        assertEquals("", a2.getNotes());

        Appointment a3 = new Appointment("doc3", "Carol", now, "custom");
        assertEquals("doc3", a3.getPhysicianId());
        assertEquals("Carol", a3.getPatientName());
        assertEquals(now, a3.getDateTime());
        assertEquals("custom", a3.getNotes());
    }

    @Test
    void testSetNotes() {
        Appointment a = new Appointment("doc1", "Alice", LocalDateTime.now());
        a.setNotes("updated");
        assertEquals("updated", a.getNotes());
    }
}