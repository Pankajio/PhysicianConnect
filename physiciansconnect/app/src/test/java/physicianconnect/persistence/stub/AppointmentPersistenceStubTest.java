package physicianconnect.persistence.stub;

import org.junit.jupiter.api.*;
import physicianconnect.objects.Appointment;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentPersistenceStubTest {

    private AppointmentPersistenceStub stub;

    @BeforeEach
    void setUp() {
        stub = new AppointmentPersistenceStub(false);
    }

    @Test
    void testAddAndGetAppointmentsForPhysician() {
        Appointment appt = new Appointment("doc1", "Alice", LocalDateTime.of(2025, 6, 10, 9, 0));
        stub.addAppointment(appt);
        List<Appointment> list = stub.getAppointmentsForPhysician("doc1");
        assertEquals(1, list.size());
        assertEquals("Alice", list.get(0).getPatientName());
    }

    @Test
    void testUpdateAppointment() {
        Appointment appt = new Appointment("doc1", "Alice", LocalDateTime.of(2025, 6, 10, 9, 0));
        stub.addAppointment(appt);
        appt.setNotes("Updated");
        stub.updateAppointment(appt);
        List<Appointment> list = stub.getAppointmentsForPhysician("doc1");
        assertEquals("Updated", list.get(0).getNotes());
    }

@Test
void testUpdateAppointmentNoMatchDoesNothing() {
    Appointment appt = new Appointment("doc1", "Alice", LocalDateTime.of(2025, 6, 10, 9, 0));
    stub.addAppointment(appt);
    Appointment notFound = new Appointment("doc2", "Bob", LocalDateTime.of(2025, 6, 10, 9, 0));
    notFound.setNotes("Should not update");
    stub.updateAppointment(notFound);
    List<Appointment> list = stub.getAppointmentsForPhysician("doc1");
    assertFalse(list.isEmpty());
    assertEquals("Alice", list.get(0).getPatientName());
    assertEquals("", list.get(0).getNotes()); // Not updated, should be empty string
}

    @Test
    void testDeleteAppointment() {
        Appointment appt = new Appointment("doc1", "Alice", LocalDateTime.of(2025, 6, 10, 9, 0));
        stub.addAppointment(appt);
        stub.deleteAppointment(appt);
        List<Appointment> list = stub.getAppointmentsForPhysician("doc1");
        assertTrue(list.isEmpty());
    }

    @Test
    void testDeleteAppointmentNoMatchDoesNothing() {
        Appointment appt = new Appointment("doc1", "Alice", LocalDateTime.of(2025, 6, 10, 9, 0));
        stub.addAppointment(appt);
        Appointment notFound = new Appointment("doc2", "Bob", LocalDateTime.of(2025, 6, 10, 9, 0));
        stub.deleteAppointment(notFound);
        List<Appointment> list = stub.getAppointmentsForPhysician("doc1");
        assertFalse(list.isEmpty());
    }

    @Test
    void testGetAppointmentsForPhysicianInRange() {
        Appointment appt = new Appointment("doc1", "Alice", LocalDateTime.of(2025, 6, 10, 9, 0));
        stub.addAppointment(appt);
        List<Appointment> list = stub.getAppointmentsForPhysicianInRange("doc1",
                LocalDateTime.of(2025, 6, 10, 8, 0),
                LocalDateTime.of(2025, 6, 10, 10, 0));
        assertEquals(1, list.size());
    }

    @Test
    void testGetAppointmentsForPhysicianInRangeNoMatch() {
        Appointment appt = new Appointment("doc1", "Alice", LocalDateTime.of(2025, 6, 10, 9, 0));
        stub.addAppointment(appt);
        // Wrong physicianId
        List<Appointment> list = stub.getAppointmentsForPhysicianInRange("doc2",
                LocalDateTime.of(2025, 6, 10, 8, 0),
                LocalDateTime.of(2025, 6, 10, 10, 0));
        assertTrue(list.isEmpty());
        // Out of range
        list = stub.getAppointmentsForPhysicianInRange("doc1",
                LocalDateTime.of(2025, 6, 10, 10, 0),
                LocalDateTime.of(2025, 6, 10, 11, 0));
        assertTrue(list.isEmpty());
    }

    @Test
    void testDeleteAllAppointments() {
        stub.addAppointment(new Appointment("doc1", "Alice", LocalDateTime.now()));
        stub.deleteAllAppointments();
        assertTrue(stub.getAllAppointments().isEmpty());
    }

    @Test
    void testCloseClearsAppointments() {
        stub.addAppointment(new Appointment("doc1", "Alice", LocalDateTime.now()));
        stub.close();
        assertTrue(stub.getAllAppointments().isEmpty());
    }
}