package physicianconnect.logic.manager;

import org.junit.jupiter.api.*;

import physicianconnect.logic.manager.ReferralManager;
import physicianconnect.objects.Referral;
import physicianconnect.persistence.stub.ReferralPersistenceStub;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ReferralManagerTest {

    private ReferralManager manager;

    @BeforeEach
    public void setup() {
        manager = new ReferralManager(new ReferralPersistenceStub(false));
    }

    @Test
    public void testAddAndRetrieveReferral() {
        Referral r = new Referral(0, "doc1", "Patient A", "Lab Test", "Fasting", "2025-06-01");
        manager.addReferral(r);

        List<Referral> list = manager.getReferralsForPhysician("doc1");
        assertEquals(1, list.size());
        assertEquals("Patient A", list.get(0).getPatientName());
    }

    @Test
    public void testGetReferralsForPatient() {
        manager.addReferral(new Referral(0, "doc1", "Patient A", "Lab Test", "Fasting", "2025-06-01"));
        manager.addReferral(new Referral(0, "doc2", "Patient B", "Specialist", "Bring reports", "2025-06-02"));

        List<Referral> result = manager.getReferralsForPatient("Patient B");
        assertEquals(1, result.size());
        assertEquals("Specialist", result.get(0).getReferralType());
    }

    @Test
    public void testDeleteReferralById() {
        manager.addReferral(new Referral(0, "doc1", "Patient A", "Lab Test", "Fasting", "2025-06-01"));
        Referral r = manager.getReferralsForPhysician("doc1").get(0);
        manager.deleteReferralById(r.getId());
        assertTrue(manager.getReferralsForPhysician("doc1").isEmpty());
    }

    @Test
    public void testDeleteAll() {
        manager.addReferral(new Referral(0, "doc1", "Patient A", "Lab Test", "Fasting", "2025-06-01"));
        manager.addReferral(new Referral(0, "doc2", "Patient B", "Specialist", "Bring reports", "2025-06-02"));
        manager.deleteAll();
        assertTrue(manager.getReferralsForPhysician("doc1").isEmpty());
        assertTrue(manager.getReferralsForPhysician("doc2").isEmpty());
    }
}