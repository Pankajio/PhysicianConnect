package physicianconnect.logic.controller;

import physicianconnect.logic.exceptions.InvalidReferralException;
import physicianconnect.logic.manager.ReferralManager;
import physicianconnect.logic.validation.ReferralValidator;
import physicianconnect.objects.Referral;

import java.time.LocalDate;

/**
 * Controller for referral‐related use cases.
 * Delegates validation to ReferralValidator and persistence to ReferralManager.
 */
public class ReferralController {

    private final ReferralManager referralManager;

    public ReferralController(ReferralManager referralManager) {
        this.referralManager = referralManager;
    }

    /**
     * Create and persist a new Referral record.
     *
     * @param referringPhysicianId the ID of the physician making the referral
     * @param patientName          the patient’s name
     * @param referralType         the reason/type of referral
     * @param details              additional details (may be empty)
     * @throws InvalidReferralException if any validation rule fails
     */
    public void createReferral(
            String referringPhysicianId,
            String patientName,
            String referralType,
            String details
    ) throws InvalidReferralException {
        // 1) Basic checks
        if (patientName == null || patientName.trim().isEmpty()) {
            throw new InvalidReferralException("Patient name cannot be empty.");
        }
        // 2) Validate the referral reason/type
        ReferralValidator.validateReferralReason(referralType);
        // 3) We don’t have a referred‐physician ID in this UI, so skip validateReferredPhysicianId.
        // 4) Build the date string as today’s date (ISO format)
        String dateCreated = LocalDate.now().toString();

        // 5) Construct the Referral domain object
        //    (ID of 0 → let persistence layer assign an auto‐incremented ID)
        Referral referral = new Referral(
                0,                      // id
                referringPhysicianId,   // who made the referral
                patientName.trim(),     // patient’s name
                referralType.trim(),    // referral reason/type
                (details == null ? "" : details.trim()), // any additional details
                dateCreated             // creation date
        );

        // 6) Delegate to manager to persist
        referralManager.addReferral(referral);
    }
}