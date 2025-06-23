package physicianconnect.logic.validation;

import physicianconnect.logic.exceptions.InvalidReferralException;

public final class ReferralValidator {

    private ReferralValidator() { }

    public static void validateReferralReason(String reason) throws InvalidReferralException {
        if (reason == null || reason.trim().isEmpty()) {
            throw new InvalidReferralException("Referral reason cannot be empty.");
        }
    }

    public static void validateReferredPhysicianId(int referredId) throws InvalidReferralException {
        if (referredId <= 0) {
            throw new InvalidReferralException("Referred physician ID must be a positive integer.");
        }
    }
}