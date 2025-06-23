package physicianconnect.logic.integration;

import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import physicianconnect.logic.controller.PrescriptionController;
import physicianconnect.logic.exceptions.InvalidPrescriptionException;
import physicianconnect.objects.Prescription;
import physicianconnect.persistence.ConnectionManager;
import physicianconnect.persistence.sqlite.PrescriptionDB;
import physicianconnect.persistence.sqlite.SchemaInitializer;

/**
 * End-to-end test for “Physician prescribes medication”.
 * Layers involved:
 * controller ➜ persistence (PrescriptionDB) ➜ SQLite :memory:
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PrescriptionIntegrationTest {

    private Connection conn;
    private PrescriptionController controller;
    private PrescriptionDB prescriptionDB; // to read back rows

    /* ───────────── one-time boot-strap ───────────── */
    @BeforeAll
    void spinUpDatabase() throws Exception {
        // 1. Single in-memory connection
        ConnectionManager.initialize(":memory:");
        conn = ConnectionManager.get();

        // 2. Create full schema
        SchemaInitializer.initializeSchema(conn); // :contentReference[oaicite:0]{index=0}

        // 3. Seed one physician (FK requirement)
        try (Statement st = conn.createStatement()) {
            st.executeUpdate(
                    "INSERT INTO physicians (id, name, email, password) " +
                            "VALUES ('phys-1', 'Dr. Test', 'dr@test', 'pw')");
        }

        // 4. Wire persistence → controller
        prescriptionDB = new PrescriptionDB(conn);
        controller = new PrescriptionController(prescriptionDB);
    }

    @AfterAll
    void tearDown() {
        ConnectionManager.close(); // drops the :memory: DB
    }

    /*
     * ───────────────────────────────────────────────
     * HAPPY PATH: create → retrieve
     * ───────────────────────────────────────────────
     */
    @Test
    void physicianCanCreateAndRetrievePrescription() throws Exception {
        // GIVEN no prescriptions for patient “Alice”
        Assertions.assertTrue(
                prescriptionDB.getPrescriptionsForPatient("Alice").isEmpty());

        // WHEN Dr phys-1 writes a prescription
        controller.createPrescription(
                "phys-1",
                "Alice",
                "Amoxicillin",
                "500 mg", // default dosage from Medication table (string only)
                "500 mg",
                "Twice daily",
                "Take with food");

        // THEN it is persisted and readable via DB layer
        List<Prescription> list = prescriptionDB.getPrescriptionsForPatient("Alice");

        Assertions.assertEquals(1, list.size());
        Prescription p = list.get(0);

        Assertions.assertEquals("phys-1", p.getPhysicianId());
        Assertions.assertEquals("Alice", p.getPatientName());
        Assertions.assertEquals("Amoxicillin", p.getMedicationName());
        Assertions.assertEquals("500 mg", p.getDosage());
        Assertions.assertEquals("Twice daily", p.getFrequency());

        // Date is “today” in ISO_LOCAL_DATE format
        String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        Assertions.assertTrue(p.getDatePrescribed().startsWith(today));
    }

    @Test
    void invalidDosageFormatIsRejected() {
        Assertions.assertThrows(InvalidPrescriptionException.class, () -> controller.createPrescription(
                "phys-1",
                "Bob",
                "Ibuprofen",
                "200 mg", // default
                "twice", // invalid dosage format
                "Every 6 h",
                null));
    }

}
