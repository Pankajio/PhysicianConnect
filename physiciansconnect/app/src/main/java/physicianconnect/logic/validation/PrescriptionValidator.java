package physicianconnect.logic.validation;

import physicianconnect.logic.exceptions.InvalidPrescriptionException;
import java.time.LocalDate;

public final class PrescriptionValidator {

    private PrescriptionValidator() {
    }

    public static void validateMedicationName(String name) throws InvalidPrescriptionException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidPrescriptionException("Medication name cannot be empty.");
        }
    }

    public static void validateDosage(String dosage) throws InvalidPrescriptionException {
        if (dosage == null || dosage.trim().isEmpty()) {
            throw new InvalidPrescriptionException("Dosage cannot be empty.");
        }

        /*
         * Accept values like:
         * • "500 mg"
         * • "0.5 mL"
         * • "1 tablet"
         * • "2 tabs"
         * Units are case-insensitive and you can extend them as needed.
         */

        String pattern = "^[0-9]+(?:\\.[0-9]+)?\\s*(mg|g|µg|mcg|ml|mL|tablet|tab|tabs?|caps?|units?)$";
        if (!dosage.trim().toLowerCase().matches(pattern)) {
            throw new InvalidPrescriptionException(
                    "Dosage must look like \"500 mg\", \"1 tablet\", etc.");
        }
    }

    public static void validateStartDate(LocalDate date) throws InvalidPrescriptionException {
        if (date == null || date.isBefore(LocalDate.now().minusDays(1))) {
            throw new InvalidPrescriptionException("Start date must be today or in the future.");
        }
    }

    public static void validateDuration(int days) throws InvalidPrescriptionException {
        if (days <= 0) {
            throw new InvalidPrescriptionException("Duration must be a positive integer.");
        }
    }
}