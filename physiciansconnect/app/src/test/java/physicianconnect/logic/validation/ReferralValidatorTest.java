package physicianconnect.logic.validation;

import org.junit.jupiter.api.Test;
import physicianconnect.logic.exceptions.InvalidReferralException;

import static org.junit.jupiter.api.Assertions.*;

public class ReferralValidatorTest {

    @Test
    void testValidateReferralReasonValid() {
        assertDoesNotThrow(() -> ReferralValidator.validateReferralReason("Needs specialist"));
    }

    @Test
    void testValidateReferralReasonNullThrows() {
        assertThrows(InvalidReferralException.class, () -> ReferralValidator.validateReferralReason(null));
    }

    @Test
    void testValidateReferralReasonBlankThrows() {
        assertThrows(InvalidReferralException.class, () -> ReferralValidator.validateReferralReason("   "));
    }

    @Test
    void testValidateReferredPhysicianIdValid() {
        assertDoesNotThrow(() -> ReferralValidator.validateReferredPhysicianId(1));
    }

    @Test
    void testValidateReferredPhysicianIdZeroThrows() {
        assertThrows(InvalidReferralException.class, () -> ReferralValidator.validateReferredPhysicianId(0));
    }

    @Test
    void testValidateReferredPhysicianIdNegativeThrows() {
        assertThrows(InvalidReferralException.class, () -> ReferralValidator.validateReferredPhysicianId(-5));
    }
}