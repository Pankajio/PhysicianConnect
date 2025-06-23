package physicianconnect.persistence.sqlite;

import org.junit.jupiter.api.*;
import physicianconnect.objects.Referral;
import physicianconnect.objects.Physician;
import physicianconnect.persistence.interfaces.ReferralPersistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ReferralDBTest {

    private Connection conn;
    private ReferralPersistence db;
    private PhysicianDB dbPhysician;

    @BeforeEach
    public void setup() throws Exception {
        conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        SchemaInitializer.initializeSchema(conn);
        dbPhysician = new PhysicianDB(conn);
        dbPhysician.addPhysician(new Physician("doc1", "Dr. Banner", "banner@avengers.com", "hulk"));
        dbPhysician.addPhysician(new Physician("doc2", "Dr. Stark", "stark@avengers.com", "ironman"));
        db = new ReferralDB(conn);
    }

    @AfterEach
    public void cleanup() throws Exception {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    @Test
    public void testAddAndGetReferral() {
        Referral r = new Referral(0, "doc1", "Patient A", "Lab Test", "Fasting", "2025-06-01");
        db.addReferral(r);

        List<Referral> all = db.getReferralsForPhysician("doc1");
        assertEquals(1, all.size());
        assertEquals("Patient A", all.get(0).getPatientName());
    }

    @Test
    public void testGetReferralsForPatient() {
        db.addReferral(new Referral(0, "doc1", "Patient A", "Lab Test", "Fasting", "2025-06-01"));
        db.addReferral(new Referral(0, "doc2", "Patient B", "Specialist", "Bring reports", "2025-06-02"));

        List<Referral> result = db.getReferralsForPatient("Patient B");
        assertEquals(1, result.size());
        assertEquals("Specialist", result.get(0).getReferralType());
    }

    @Test
    public void testDeleteReferralById() {
        db.addReferral(new Referral(0, "doc1", "Patient A", "Lab Test", "Fasting", "2025-06-01"));
        Referral r = db.getReferralsForPhysician("doc1").get(0);
        db.deleteReferralById(r.getId());
        assertTrue(db.getReferralsForPhysician("doc1").isEmpty());
    }

    @Test
    public void testDeleteAllReferrals() {
        db.addReferral(new Referral(0, "doc1", "Patient A", "Lab Test", "Fasting", "2025-06-01"));
        db.addReferral(new Referral(0, "doc2", "Patient B", "Specialist", "Bring reports", "2025-06-02"));
        db.deleteAllReferrals();
        assertTrue(db.getReferralsForPhysician("doc1").isEmpty());
        assertTrue(db.getReferralsForPhysician("doc2").isEmpty());
    }

    // --- Catch/exception coverage ---

    @Test
    public void testAddReferralCatchesSQLException() throws Exception {
        Referral r = new Referral(0, null, null, null, null, null);
        conn.close();
        Exception ex = assertThrows(RuntimeException.class, () -> db.addReferral(r));
        assertTrue(ex.getMessage().contains("Failed to add referral"));
    }

    @Test
    public void testGetReferralsForPhysicianCatchesSQLException() throws Exception {
        db.addReferral(new Referral(0, "doc1", "Patient A", "Lab Test", "Fasting", "2025-06-01"));
        conn.close();
        Exception ex = assertThrows(RuntimeException.class, () -> db.getReferralsForPhysician("doc1"));
        assertTrue(ex.getMessage().contains("Failed to fetch referrals"));
    }

    @Test
    public void testGetReferralsForPatientCatchesSQLException() throws Exception {
        db.addReferral(new Referral(0, "doc1", "Patient A", "Lab Test", "Fasting", "2025-06-01"));
        conn.close();
        Exception ex = assertThrows(RuntimeException.class, () -> db.getReferralsForPatient("Patient A"));
        assertTrue(ex.getMessage().contains("Failed to fetch referrals"));
    }

    @Test
    public void testDeleteReferralByIdCatchesSQLException() throws Exception {
        db.addReferral(new Referral(0, "doc1", "Patient A", "Lab Test", "Fasting", "2025-06-01"));
        int id = db.getReferralsForPhysician("doc1").get(0).getId();
        conn.close();
        Exception ex = assertThrows(RuntimeException.class, () -> db.deleteReferralById(id));
        assertTrue(ex.getMessage().contains("Failed to delete referral"));
    }

    @Test
    public void testDeleteAllReferralsCatchesSQLException() throws Exception {
        db.addReferral(new Referral(0, "doc1", "Patient A", "Lab Test", "Fasting", "2025-06-01"));
        conn.close();
        Exception ex = assertThrows(RuntimeException.class, () -> db.deleteAllReferrals());
        assertTrue(ex.getMessage().contains("Failed to delete all referrals"));
    }
}