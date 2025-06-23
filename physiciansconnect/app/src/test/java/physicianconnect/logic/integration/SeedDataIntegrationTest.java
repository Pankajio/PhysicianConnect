package physicianconnect.logic.integration;

import org.junit.jupiter.api.*;
import physicianconnect.objects.*;
import physicianconnect.persistence.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SeedDataIntegrationTest {

    @BeforeEach
    public void setup() {
        PersistenceFactory.initialize(PersistenceType.TEST, true);
    }

    @AfterEach
    public void teardown() {
        PersistenceFactory.reset();
    }

    @Test
    public void testSeededPhysiciansExist() {
        List<Physician> physicians = PersistenceFactory.getPhysicianPersistence().getAllPhysicians();
        assertFalse(physicians.isEmpty(), "Seeded physicians should exist");
    }

    @Test
    public void testSeededMedicationsExist() {
        List<Medication> meds = PersistenceFactory.getMedicationPersistence().getAllMedications();
        assertFalse(meds.isEmpty(), "Seeded medications should exist");
    }

    @Test
public void testSeededReferralsExist() {
    List<Referral> referrals = PersistenceFactory.getReferralPersistence().getReferralsForPhysician("1");
    assertFalse(referrals.isEmpty(), "Seeded referrals should exist");
}
}