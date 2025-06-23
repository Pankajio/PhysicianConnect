package physicianconnect.persistence.stub;

import physicianconnect.objects.Referral;
import physicianconnect.persistence.interfaces.ReferralPersistence;

import java.util.*;

public class ReferralPersistenceStub implements ReferralPersistence {
    private final List<Referral> referrals = new ArrayList<>();
    private int nextId = 1;

    public ReferralPersistenceStub(boolean seed) {
        if (seed) {
            // Example seeded data
            addReferral(new Referral(0, "1", "Alice Johnson", "Lab Test", "Fasting required", "2025-06-01"));
            addReferral(new Referral(0, "2", "Bob Brown", "Specialist", "Bring previous reports", "2025-06-02"));
        }
    }

    @Override
    public void addReferral(Referral referral) {
        referrals.add(new Referral(nextId++, referral.getPhysicianId(), referral.getPatientName(),
                referral.getReferralType(), referral.getDetails(), referral.getDateCreated()));
    }

    @Override
    public List<Referral> getReferralsForPhysician(String physicianId) {
        List<Referral> result = new ArrayList<>();
        for (Referral r : referrals) {
            if (r.getPhysicianId().equals(physicianId)) result.add(r);
        }
        return result;
    }

    @Override
    public List<Referral> getReferralsForPatient(String patientName) {
        List<Referral> result = new ArrayList<>();
        for (Referral r : referrals) {
            if (r.getPatientName().equals(patientName)) result.add(r);
        }
        return result;
    }

    @Override
    public void deleteReferralById(int id) {
        referrals.removeIf(r -> r.getId() == id);
    }

    @Override
    public void deleteAllReferrals() {
        referrals.clear();
    }
}