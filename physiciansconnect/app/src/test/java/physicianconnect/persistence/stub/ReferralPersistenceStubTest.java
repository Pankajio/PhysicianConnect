package physicianconnect.persistence.stub;

import org.junit.jupiter.api.*;
import physicianconnect.objects.Referral;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ReferralPersistenceStubTest {

    private ReferralPersistenceStub stub;

    @BeforeEach
    public void setup() {
        stub = new ReferralPersistenceStub(false);
    }

    @Test
    public void testAddAndGetReferral() {
        Referral r = new Referral(0, "doc1", "Patient A", "Lab Test", "Fasting", "2025-06-01");
        stub.addReferral(r);

        List<Referral> all = stub.getReferralsForPhysician("doc1");
        assertEquals(1, all.size());
        assertEquals("Patient A", all.get(0).getPatientName());
        assertEquals("Lab Test", all.get(0).getReferralType());
    }

    @Test
    public void testGetReferralsForPatient() {
        stub.addReferral(new Referral(0, "doc1", "Patient A", "Lab Test", "Fasting", "2025-06-01"));
        stub.addReferral(new Referral(0, "doc2", "Patient B", "Specialist", "Bring reports", "2025-06-02"));

        List<Referral> result = stub.getReferralsForPatient("Patient B");
        assertEquals(1, result.size());
        assertEquals("Specialist", result.get(0).getReferralType());
    }

    @Test
    public void testDeleteReferralById() {
        stub.addReferral(new Referral(0, "doc1", "Patient A", "Lab Test", "Fasting", "2025-06-01"));
        Referral r = stub.getReferralsForPhysician("doc1").get(0);
        stub.deleteReferralById(r.getId());
        assertTrue(stub.getReferralsForPhysician("doc1").isEmpty());
    }

    @Test
    public void testDeleteAllReferrals() {
        stub.addReferral(new Referral(0, "doc1", "Patient A", "Lab Test", "Fasting", "2025-06-01"));
        stub.addReferral(new Referral(0, "doc2", "Patient B", "Specialist", "Bring reports", "2025-06-02"));
        stub.deleteAllReferrals();
        assertTrue(stub.getReferralsForPhysician("doc1").isEmpty());
        assertTrue(stub.getReferralsForPhysician("doc2").isEmpty());
    }
}