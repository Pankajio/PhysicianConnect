package physicianconnect.logic.manager;

import physicianconnect.objects.Referral;
import physicianconnect.persistence.interfaces.ReferralPersistence;
import java.util.List;

public class ReferralManager {
    private final ReferralPersistence referralDB;

    public ReferralManager(ReferralPersistence referralDB) {
        this.referralDB = referralDB;
    }

    public void addReferral(Referral referral) {
        referralDB.addReferral(referral);
    }

    public List<Referral> getReferralsForPhysician(String physicianId) {
        return referralDB.getReferralsForPhysician(physicianId);
    }

    public List<Referral> getReferralsForPatient(String patientName) {
        return referralDB.getReferralsForPatient(patientName);
    }

    public void deleteReferralById(int id) {
        referralDB.deleteReferralById(id);
    }

    public void deleteAll() {
        referralDB.deleteAllReferrals();
    }
}