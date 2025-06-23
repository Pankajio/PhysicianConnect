package physicianconnect.logic.integration;

import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import physicianconnect.logic.controller.ReferralController;
import physicianconnect.logic.exceptions.InvalidReferralException;
import physicianconnect.logic.manager.ReferralManager;
import physicianconnect.objects.Referral;
import physicianconnect.persistence.ConnectionManager;
import physicianconnect.persistence.sqlite.ReferralDB;
import physicianconnect.persistence.sqlite.SchemaInitializer;

/**
 * End-to-end tests for the Referral feature.
 *
 * • Happy-path: create a referral, verify it is persisted.
 * • Negative path: blank referral reason is rejected.
 *
 * Touches: ReferralController ▸ ReferralManager ▸ ReferralDB ▸ SQLite :memory:
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReferralIntegrationTest {

    private Connection conn;
    private ReferralController controller;
    private ReferralDB referralDB; // read-back helper

    /* ───────────────── one-time bootstrap ───────────────── */
    @BeforeAll
    void setupDatabase() throws Exception {
        // 1️⃣ open a fresh in-memory database
        ConnectionManager.initialize(":memory:");
        conn = ConnectionManager.get();

        // 2️⃣ create the full PhysiciansConnect schema
        SchemaInitializer.initializeSchema(conn); // :contentReference[oaicite:1]{index=1}

        // 3️⃣ seed a physician row (FK requirement)
        try (Statement st = conn.createStatement()) {
            st.executeUpdate(
                    "INSERT INTO physicians (id, name, email, password) " +
                            "VALUES ('phys-1','Dr. Seed','seed@clinic.test','pw')");
        }

        // 4️⃣ wire persistence ➜ manager ➜ controller
        referralDB = new ReferralDB(conn);
        ReferralManager manager = new ReferralManager(referralDB);
        controller = new ReferralController(manager);
    }

    @AfterAll
    void tearDown() {
        ConnectionManager.close(); // destroys :memory: DB
    }

    /*
     * ──────────────────────────────────────────────
     * HAPPY PATH: create → retrieve
     * ──────────────────────────────────────────────
     */
    @Test
    void physicianCanCreateAndRetrieveReferral() throws Exception {
        // GIVEN no referrals exist for Dr phys-1
        Assertions.assertTrue(
                referralDB.getReferralsForPhysician("phys-1").isEmpty());

        // WHEN: a referral is created
        controller.createReferral(
                "phys-1",
                "Alice Patient",
                "Lab Test", // referralType
                "Complete blood count");

        // THEN: it is persisted and retrievable
        List<Referral> list = referralDB.getReferralsForPhysician("phys-1");

        Assertions.assertEquals(1, list.size());
        Referral r = list.get(0);

        Assertions.assertEquals("phys-1", r.getPhysicianId());
        Assertions.assertEquals("Alice Patient", r.getPatientName());
        Assertions.assertEquals("Lab Test", r.getReferralType());
        Assertions.assertEquals("Complete blood count", r.getDetails());

        // date_created is today (YYYY-MM-DD)
        Assertions.assertEquals(LocalDate.now().toString(), r.getDateCreated());
        Assertions.assertTrue(r.getId() > 0); // auto-PK assigned
    }

    /*
     * ──────────────────────────────────────────────
     * NEGATIVE PATH: blank referral reason rejected
     * ──────────────────────────────────────────────
     */
    @Test
    void blankReferralReasonIsRejected() {
        Assertions.assertThrows(InvalidReferralException.class, () -> controller.createReferral(
                "phys-1",
                "Bob Patient",
                "   ",
                null));
    }
}
