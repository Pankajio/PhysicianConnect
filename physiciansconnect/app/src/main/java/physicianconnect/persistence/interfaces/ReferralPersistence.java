package physicianconnect.persistence.interfaces;

import physicianconnect.objects.Referral;
import java.util.List;

public interface ReferralPersistence {
    void addReferral(Referral referral);
    List<Referral> getReferralsForPhysician(String physicianId);
    List<Referral> getReferralsForPatient(String patientName);
    void deleteReferralById(int id);
    void deleteAllReferrals();
}