package physicianconnect.persistence.sqlite;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import org.junit.jupiter.api.*;

import physicianconnect.logic.manager.AppointmentManager;
import physicianconnect.objects.Appointment;
import physicianconnect.objects.Physician;

public class AppointmentDBDateRangeTest {

    private Connection conn;
    private AppointmentDB db;
    private PhysicianDB dbPhysician;

    @BeforeEach
    void setup() throws Exception {
        conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        SchemaInitializer.initializeSchema(conn);
        db = new AppointmentDB(conn);
        dbPhysician = new PhysicianDB(conn);

        // Insert two physicians so FK constraint is satisfied
        dbPhysician.addPhysician(new Physician("p1", "Dr. A", "a@doc.com", "pw"));
        dbPhysician.addPhysician(new Physician("p2", "Dr. B", "b@doc.com", "pw"));
    }

    @AfterEach
    void cleanup() throws Exception {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    @Test
    @DisplayName("Adding multiple appointments and fetching by date‐range for same physician")
    void testGetAppointmentsForPhysicianInRange() {
        String docId = "p1";

        // Create three distinct times
        LocalDateTime t1 = LocalDateTime.of(2025, Month.JUNE, 1, 9, 0);
        LocalDateTime t2 = LocalDateTime.of(2025, Month.JUNE, 1, 10, 0);
        LocalDateTime t3 = LocalDateTime.of(2025, Month.JUNE, 1, 11, 0);

        Appointment a1 = new Appointment(docId, "PatientX", t1);
        Appointment a2 = new Appointment(docId, "PatientY", t2);
        Appointment a3 = new Appointment(docId, "PatientZ", t3);

        db.addAppointment(a1);
        db.addAppointment(a2);
        db.addAppointment(a3);

        // Query range that only includes t2
        LocalDateTime start = t2.minusMinutes(1);
        LocalDateTime end   = t2.plusMinutes(1);

        List<Appointment> single = db.getAppointmentsForPhysicianInRange(docId, start, end);
        assertEquals(1, single.size());
        assertEquals("PatientY", single.get(0).getPatientName());

        // Query a wider range that includes all three
        start = t1.minusHours(1);
        end   = t3.plusHours(1);
        List<Appointment> allThree = db.getAppointmentsForPhysicianInRange(docId, start, end);
        assertEquals(3, allThree.size());

        // Query a range that excludes everyone
        LocalDateTime outsideStart = LocalDateTime.of(2025, Month.JUNE, 2, 0, 0);
        LocalDateTime outsideEnd   = LocalDateTime.of(2025, Month.JUNE, 2, 23, 59);
        List<Appointment> empty = db.getAppointmentsForPhysicianInRange(docId, outsideStart, outsideEnd);
        assertTrue(empty.isEmpty());
    }

    @Test
    @DisplayName("Date‐range query does not return other physician’s appointments")
    void testDateRangeExcludesOtherPhysician() {
        // Create overlapping times for p1 and p2
        LocalDateTime sharedSlot = LocalDateTime.of(2025, Month.JUNE, 3, 14, 0);

        Appointment p1Appt = new Appointment("p1", "Alpha", sharedSlot);
        Appointment p2Appt = new Appointment("p2", "Beta", sharedSlot);

        db.addAppointment(p1Appt);
        db.addAppointment(p2Appt);

        // Query for p1 only
        LocalDateTime start = sharedSlot.minusMinutes(5);
        LocalDateTime end   = sharedSlot.plusMinutes(5);

        List<Appointment> p1List = db.getAppointmentsForPhysicianInRange("p1", start, end);
        assertEquals(1, p1List.size());
        assertEquals("Alpha", p1List.get(0).getPatientName());

        // Query for p2 only
        List<Appointment> p2List = db.getAppointmentsForPhysicianInRange("p2", start, end);
        assertEquals(1, p2List.size());
        assertEquals("Beta", p2List.get(0).getPatientName());
    }

    @Test
    @DisplayName("Date‐range on empty physician yields empty list")
    void testGetAppointmentsEmptyPhysicianInRange() {
        // No appointments added for "p3"
        LocalDateTime start = LocalDateTime.of(2025, Month.JUNE, 10, 0, 0);
        LocalDateTime end   = LocalDateTime.of(2025, Month.JUNE, 10, 23, 59);
        List<Appointment> none = db.getAppointmentsForPhysicianInRange("p3", start, end);
        assertTrue(none.isEmpty());
    }
}
