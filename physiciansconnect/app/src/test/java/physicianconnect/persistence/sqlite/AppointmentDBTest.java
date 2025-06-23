package physicianconnect.persistence.sqlite;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.*;

import physicianconnect.objects.Appointment;
import physicianconnect.objects.Physician;

public class AppointmentDBTest {

    private Connection conn;
    private AppointmentDB db;
    private PhysicianDB dbPhysician;

    @BeforeEach
    public void setup() throws Exception {
        conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        SchemaInitializer.initializeSchema(conn);
        db = new AppointmentDB(conn);
        dbPhysician = new PhysicianDB(conn);

        // Add physicians for foreign key constraints
        dbPhysician.addPhysician(new Physician("doc1", "Dr. Banner", "banner@avengers.com", "hulk"));
        dbPhysician.addPhysician(new Physician("doc2", "Dr. Stark", "stark@avengers.com", "ironman"));
    }

    @AfterEach
    public void cleanup() throws Exception {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    @Test
    public void testAddAndFetchAppointment() {
        Appointment a = new Appointment("doc1", "Bruce Banner", LocalDateTime.now().plusMinutes(5));
        db.addAppointment(a);

        List<Appointment> list = db.getAppointmentsForPhysician("doc1");
        assertEquals(1, list.size());
        assertEquals("Bruce Banner", list.get(0).getPatientName());
    }

    @Test
    public void testNoAppointmentsForUnknownPhysician() {
        List<Appointment> result = db.getAppointmentsForPhysician("unknown");
        assertTrue(result.isEmpty());
    }

    @Test
    public void testDeleteAppointment() {
        Appointment a = new Appointment("doc1", "Delete Me", LocalDateTime.now().plusMinutes(5));
        db.addAppointment(a);
        db.deleteAppointment(a);
        List<Appointment> list = db.getAppointmentsForPhysician("doc1");
        assertTrue(list.stream().noneMatch(appt -> appt.getPatientName().equals("Delete Me")));
    }

    @Test
    public void testDeleteAllAppointments() {
        db.addAppointment(new Appointment("doc1", "A", LocalDateTime.now().plusMinutes(5)));
        db.addAppointment(new Appointment("doc2", "B", LocalDateTime.now().plusMinutes(5)));
        db.deleteAllAppointments();
        assertTrue(db.getAppointmentsForPhysician("doc1").isEmpty());
        assertTrue(db.getAppointmentsForPhysician("doc2").isEmpty());
    }

    @Test
    public void testAddAppointmentCatchesSQLException() {
        Appointment a = new Appointment(null, null, null);
        assertThrows(RuntimeException.class, () -> db.addAppointment(a));
    }

    @Test
    public void testGetAllAppointmentsReturnsAll() {
        db.addAppointment(new Appointment("doc1", "A", LocalDateTime.now().plusMinutes(5)));
        db.addAppointment(new Appointment("doc2", "B", LocalDateTime.now().plusMinutes(10)));
        List<Appointment> all = db.getAllAppointments();
        assertEquals(2, all.size());
        assertTrue(all.stream().anyMatch(a -> a.getPatientName().equals("A")));
        assertTrue(all.stream().anyMatch(a -> a.getPatientName().equals("B")));
    }

    @Test
    public void testGetAllAppointmentsCatchesSQLException() throws Exception {
        conn.close();
        assertThrows(RuntimeException.class, () -> db.getAllAppointments());
    }

    @Test
    public void testDeleteAllAppointmentsCatchesSQLException() throws Exception {
        conn.close();
        assertThrows(RuntimeException.class, () -> db.deleteAllAppointments());
    }

    @Test
    public void testDeleteAppointmentCatchesSQLException() throws Exception {
        conn.close();
        Appointment a = new Appointment("doc1", "Bruce Banner", LocalDateTime.now().plusMinutes(5));
        assertThrows(RuntimeException.class, () -> db.deleteAppointment(a));
    }
}