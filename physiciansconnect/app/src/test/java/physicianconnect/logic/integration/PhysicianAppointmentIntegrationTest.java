package physicianconnect.logic.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.*;

import physicianconnect.logic.manager.AppointmentManager;
import physicianconnect.logic.manager.PhysicianManager;
import physicianconnect.objects.Appointment;
import physicianconnect.objects.Physician;
import physicianconnect.persistence.PersistenceFactory;
import physicianconnect.persistence.PersistenceType;

public class PhysicianAppointmentIntegrationTest {

    private PhysicianManager physicianManager;
    private AppointmentManager appointmentManager;

    @BeforeEach
    public void setup() {
        PersistenceFactory.initialize(PersistenceType.TEST, false); // Do not re-seed if already seeded
        physicianManager = new PhysicianManager(PersistenceFactory.getPhysicianPersistence());
        appointmentManager = new AppointmentManager(PersistenceFactory.getAppointmentPersistence());
    }

    @AfterEach
    public void teardown() {
        PersistenceFactory.reset();
    }

@Test
public void testPhysicianAppointmentsArePersistedAndRetrieved() {
    // Step 1: Add physician
    Physician doc = new Physician("abc", "Dr. Who", "tardis@space.com", "timetravel");
    physicianManager.addPhysician(doc);

    // Step 2: Add appointment for that physician
    Appointment a = new Appointment("abc", "Amy Pond", LocalDateTime.of(2027, 1, 10, 9, 0));
    // Delete if already exists to avoid UNIQUE constraint error
    appointmentManager.deleteAppointment(a);

    appointmentManager.addAppointment(a);

    // Step 3: Verify Amy Pond's appointment is present
    List<Appointment> result = appointmentManager.getAppointmentsForPhysician("abc");

    boolean found = result.stream()
            .anyMatch(app -> app.getPatientName().equals("Amy Pond")
                    && app.getDateTime().equals(LocalDateTime.of(2027, 1, 10, 9, 0)));

    assertTrue(found, "Expected to find appointment for Amy Pond at 9:00 AM on Jan 10, 2027");
}

    @Test
    public void testAppointmentForUnknownPhysicianReturnsEmpty() {
        List<Appointment> result = appointmentManager.getAppointmentsForPhysician("nonexistent-id");
        assertTrue(result.isEmpty(), "Expected no appointments for a nonexistent physician ID");
    }
}
