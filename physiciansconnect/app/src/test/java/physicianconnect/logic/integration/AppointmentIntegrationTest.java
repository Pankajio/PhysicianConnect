package physicianconnect.logic.integration;

import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

import physicianconnect.logic.controller.AppointmentController;
import physicianconnect.logic.exceptions.InvalidAppointmentException;
import physicianconnect.logic.manager.AppointmentManager;
import physicianconnect.objects.Appointment;
import physicianconnect.persistence.ConnectionManager;
import physicianconnect.persistence.sqlite.AppointmentDB;
import physicianconnect.persistence.sqlite.SchemaInitializer;

/**
 * End-to-end test for the Appointment feature.
 *
 * ▸ Uses ConnectionManager → single in-memory SQLite connection
 * ▸ Builds the schema with SchemaInitializer
 * ▸ Seeds a physician row (FK required)
 * ▸ Exercises AppointmentController through Manager → DB
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AppointmentIntegrationTest {

    private Connection conn;
    private AppointmentController controller;

    /*
     * ──────────────────────────────────────────────
     * Boot-strap once for the class
     * ──────────────────────────────────────────────
     */
    @BeforeAll
    void setUpDatabase() throws Exception {
        // 1. Open :memory: DB via ConnectionManager
        ConnectionManager.initialize(":memory:"); // -> jdbc:sqlite::memory:
        conn = ConnectionManager.get();

        // 2. Build the full PhysiciansConnect schema
        SchemaInitializer.initializeSchema(conn);

        // 3. Seed one physician (appointments FK)
        try (Statement st = conn.createStatement()) {
            st.executeUpdate(
                    "INSERT INTO physicians (id,name,email,password) " +
                            "VALUES ('phys-1','Dr. Ada','ada@clinic.test','secret')");
        }

        // 4. Wire persistence → manager → controller
        AppointmentDB apptDB = new AppointmentDB(conn);
        AppointmentManager manager = new AppointmentManager(apptDB);
        controller = new AppointmentController(manager);
    }

    @AfterAll
    void tearDownDatabase() {
        ConnectionManager.close(); // drops the in-memory DB
    }

    /*
     * ──────────────────────────────────────────────
     * Happy-path: create → retrieve
     * ──────────────────────────────────────────────
     */
    @Test
    void receptionistCanCreateAndRetrieveAppointment() throws Exception {
        // GIVEN database is empty
        Assertions.assertTrue(
                controller.getAppointmentsForPhysician("phys-1").isEmpty());

        // WHEN a new appointment is booked
        LocalDateTime slot = LocalDateTime.now().plusDays(1)
                .withSecond(0).withNano(0);

        controller.createAppointment("phys-1",
                "John Doe",
                slot,
                "Initial consult");

        // THEN it can be read back intact
        List<Appointment> results = controller.getAppointmentsForPhysician("phys-1");

        Assertions.assertEquals(1, results.size());
        Appointment a = results.get(0);

        Assertions.assertEquals("phys-1", a.getPhysicianId());
        Assertions.assertEquals("John Doe", a.getPatientName());
        Assertions.assertEquals(slot, a.getDateTime());
        Assertions.assertEquals("Initial consult", a.getNotes());
        Assertions.assertTrue(a.getId() > 0); // DB-assigned PK
    }

    @Test
    void duplicateAppointmentIsRejected() throws Exception {
        LocalDateTime slot = LocalDateTime.now().plusDays(2)
                .withHour(9).withMinute(0)
                .withSecond(0).withNano(0);

        // First booking succeeds
        controller.createAppointment("phys-1", "Alice", slot, null);

        // Second booking – same physician, same patient, same timestamp – must fail
        Assertions.assertThrows(InvalidAppointmentException.class, () -> controller.createAppointment("phys-1",
                "Alice", // same patient
                slot, // same datetime
                "Duplicate"));
    }

}
