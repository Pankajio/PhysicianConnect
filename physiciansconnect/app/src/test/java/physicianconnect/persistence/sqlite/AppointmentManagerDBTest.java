package physicianconnect.persistence.sqlite;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;


import org.junit.jupiter.api.*;

import physicianconnect.logic.manager.AppointmentManager;
import physicianconnect.logic.manager.PhysicianManager;
import physicianconnect.objects.Appointment;
import physicianconnect.objects.Physician;
import physicianconnect.persistence.sqlite.AppointmentDB;
import physicianconnect.persistence.sqlite.PhysicianDB;
import physicianconnect.persistence.sqlite.SchemaInitializer;

public class AppointmentManagerDBTest {

    private Connection conn;
    private AppointmentDB appointmentDb;
    private AppointmentManager appointmentMgr;
    private PhysicianDB physicianDb;

    @BeforeEach
    public void setup() throws Exception {
        conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        SchemaInitializer.initializeSchema(conn);

        physicianDb = new PhysicianDB(conn);
        physicianDb.addPhysician(new Physician("doc1", "Dr. Banner", "banner@avengers.com", "hulk"));
        physicianDb.addPhysician(new Physician("doc2", "Dr. Stark",  "stark@avengers.com",  "ironman"));

        appointmentDb  = new AppointmentDB(conn);

        // ─── Freeze “now” at 2025-06-01T00:00 in system default zone ───
        LocalDateTime fixedDateTime = LocalDateTime.of(2025, 6, 1, 0, 0);
        Instant fixedInstant = fixedDateTime.atZone(ZoneId.systemDefault()).toInstant();
        Clock fixedClock = Clock.fixed(fixedInstant, ZoneId.systemDefault());

        // Pass the fixed clock into AppointmentManager
        appointmentMgr = new AppointmentManager(appointmentDb, fixedClock);

        // Wipe any pre-existing appointments
        appointmentMgr.deleteAll();
    }

    @AfterEach
    public void cleanup() throws Exception {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    @Test
    public void testAddAndFetchAppointment() {
        // Arrange: one appointment in the future
        LocalDateTime slot = LocalDateTime.now().plusMinutes(5);
        Appointment a = new Appointment("doc1", "Bruce Banner", slot);
        a.setNotes("Gamma‐radiation follow‐up");
        appointmentMgr.addAppointment(a);

        // Act: fetch all appointments for doc1
        List<Appointment> list = appointmentDb.getAppointmentsForPhysician("doc1");

        // Assert
        assertEquals(1, list.size(),   "Exactly one appointment should have been inserted");
        assertEquals("Bruce Banner", list.get(0).getPatientName());
        assertEquals(slot, list.get(0).getDateTime());
        assertEquals("Gamma‐radiation follow‐up", list.get(0).getNotes());
    }

    @Test
    public void testConflictDetection() {
        LocalDateTime slot = LocalDateTime.of(2025, 6, 2, 11, 0);

        // Add first appointment at 9:00
        Appointment a1 = new Appointment("doc1", "Bruce Banner", slot);
        appointmentMgr.addAppointment(a1);

        // Now isSlotAvailable("doc1", slot) should be false
        assertFalse(appointmentMgr.isSlotAvailable("doc1", slot));

        // But a different physician can still use that slot
        assertTrue(appointmentMgr.isSlotAvailable("doc2", slot));
    }

    @Test
    public void testDeleteAppointmentAndVerifyGone() {
        // Arrange: add two appointments, then delete one
        LocalDateTime slot1 = LocalDateTime.of(2025, 6, 2, 12, 0);
        LocalDateTime slot2 = LocalDateTime.of(2025, 6, 2, 12, 30);
        Appointment a1 = new Appointment("doc1", "Bruce Banner", slot1);
        Appointment a2 = new Appointment("doc1", "Tony Stark",    slot2);

        appointmentMgr.addAppointment(a1);
        appointmentMgr.addAppointment(a2);

        // Sanity check both exist
        List<Appointment> before = appointmentDb.getAppointmentsForPhysician("doc1");
        assertEquals(2, before.size());

        // Act: delete a1
        appointmentMgr.deleteAppointment(a1);

        // Assert: now only one remains, and its patientName is “Tony Stark”
        List<Appointment> after = appointmentDb.getAppointmentsForPhysician("doc1");
        assertEquals(1, after.size());
        assertEquals("Tony Stark", after.get(0).getPatientName());
    }

    @Test
    public void testDeleteAllAppointments() {
        // Arrange: add one to doc1 and one to doc2
        LocalDateTime slotA = LocalDateTime.now().plusMinutes(5);
        LocalDateTime slotB = LocalDateTime.now().plusMinutes(10);
        appointmentMgr.addAppointment(new Appointment("doc1", "A", slotA));
        appointmentMgr.addAppointment(new Appointment("doc2", "B", slotB));

        // Act: wipe all
        appointmentDb.deleteAllAppointments();

        // Assert: both doc1 and doc2 have no appointments left
        assertTrue(appointmentDb.getAppointmentsForPhysician("doc1").isEmpty());
        assertTrue(appointmentDb.getAppointmentsForPhysician("doc2").isEmpty());
    }

    @Test
    public void testUpdateAppointmentNotes() {
        // Arrange: add single appointment
        LocalDateTime slot = LocalDateTime.now().plusMinutes(5);
        Appointment a = new Appointment("doc1", "Bruce Banner", slot);
        a.setNotes("Initial note");
        appointmentMgr.addAppointment(a);

        // Fetch it (it will have an auto‐generated ID); update the notes
        List<Appointment> list = appointmentDb.getAppointmentsForPhysician("doc1");
        assertEquals(1, list.size());
        Appointment fetched = list.get(0);
        fetched.setNotes("Updated radiation note");
        appointmentMgr.updateAppointment(fetched);

        // Re‐fetch and verify the notes changed
        Appointment reFetched = appointmentDb.getAppointmentsForPhysician("doc1").get(0);
        assertEquals("Updated radiation note", reFetched.getNotes());
    }

    @Test
    public void testAddAppointmentCatchesSQLException() {
        // If we pass nulls into Appointment(constructor), it should cause a NOT NULL or type‐error
        // inside appointmentDb.addAppointment(...), which we expect to bubble up as RuntimeException
        Appointment bad = new Appointment(null, null, null);
        assertThrows(RuntimeException.class, () -> appointmentMgr.addAppointment(bad));
    }

    @Test
    public void testRetrieveAppointmentsInRangeAndDeleteOne() {
        // Integration test: add three appointments on different days, fetch only those within a range, then delete one
        LocalDateTime d1 = LocalDateTime.of(2025, 6, 2, 9, 0);
        LocalDateTime d2 = LocalDateTime.of(2025, 6, 3, 9, 0);
        LocalDateTime d3 = LocalDateTime.of(2025, 6, 5, 9, 0);

        appointmentMgr.addAppointment(new Appointment("doc1", "Bruce Banner", d1));
        appointmentMgr.addAppointment(new Appointment("doc1", "Tony Stark",    d2));
        appointmentMgr.addAppointment(new Appointment("doc1", "Steve Rogers",  d3));

        // Now get all between 2025‐06‐02 00:00 and 2025‐06‐04 00:00
        LocalDate startRange = LocalDate.of(2025, 6, 2);
        LocalDate endRange   = LocalDate.of(2025, 6, 4);
        List<Appointment> rangeList = appointmentDb.getAppointmentsForPhysicianInRange(
                "doc1",
                startRange.atStartOfDay(),
                endRange.atStartOfDay()
        );
        // Expect exactly 2: d1 and d2
        assertEquals(2, rangeList.size());
        assertTrue(rangeList.stream().anyMatch(a -> a.getPatientName().equals("Bruce Banner")));
        assertTrue(rangeList.stream().anyMatch(a -> a.getPatientName().equals("Tony Stark")));

        // Delete the “Tony Stark” appointment
        Appointment toDelete = rangeList.stream()
                .filter(a -> a.getPatientName().equals("Tony Stark"))
                .findFirst()
                .get();
        appointmentMgr.deleteAppointment(toDelete);

        // Fetch again in range; now only “Bruce Banner” remains
        List<Appointment> afterDel = appointmentDb.getAppointmentsForPhysicianInRange(
                "doc1",
                startRange.atStartOfDay(),
                endRange.atStartOfDay()
        );
        assertEquals(1, afterDel.size());
        assertEquals("Bruce Banner", afterDel.get(0).getPatientName());
    }
}
