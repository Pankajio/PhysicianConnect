package physicianconnect.logic.validation;

import org.junit.jupiter.api.Test;
import physicianconnect.logic.exceptions.InvalidPrescriptionException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class PrescriptionValidatorTest {

    @Test
    void testValidateMedicationNameValid() {
        assertDoesNotThrow(() -> PrescriptionValidator.validateMedicationName("Aspirin"));
    }

    @Test
    void testValidateMedicationNameNullThrows() {
        assertThrows(InvalidPrescriptionException.class, () -> PrescriptionValidator.validateMedicationName(null));
    }

    @Test
    void testValidateMedicationNameBlankThrows() {
        assertThrows(InvalidPrescriptionException.class, () -> PrescriptionValidator.validateMedicationName("   "));
    }

    @Test
    void testValidateDosageValid() {
        assertDoesNotThrow(() -> PrescriptionValidator.validateDosage("10mg"));
    }

    @Test
    void testValidateDosageNullThrows() {
        assertThrows(InvalidPrescriptionException.class, () -> PrescriptionValidator.validateDosage(null));
    }

    @Test
    void testValidateDosageBlankThrows() {
        assertThrows(InvalidPrescriptionException.class, () -> PrescriptionValidator.validateDosage("   "));
    }

    @Test
    void testValidateStartDateValid() {
        assertDoesNotThrow(() -> PrescriptionValidator.validateStartDate(LocalDate.now()));
    }

    @Test
    void testValidateStartDateNullThrows() {
        assertThrows(InvalidPrescriptionException.class, () -> PrescriptionValidator.validateStartDate(null));
    }

    @Test
    void testValidateStartDatePastThrows() {
        assertThrows(InvalidPrescriptionException.class, () -> PrescriptionValidator.validateStartDate(LocalDate.now().minusDays(2)));
    }

    @Test
    void testValidateDurationValid() {
        assertDoesNotThrow(() -> PrescriptionValidator.validateDuration(10));
    }

    @Test
    void testValidateDurationZeroThrows() {
        assertThrows(InvalidPrescriptionException.class, () -> PrescriptionValidator.validateDuration(0));
    }

    @Test
    void testValidateDurationNegativeThrows() {
        assertThrows(InvalidPrescriptionException.class, () -> PrescriptionValidator.validateDuration(-5));
    }
}