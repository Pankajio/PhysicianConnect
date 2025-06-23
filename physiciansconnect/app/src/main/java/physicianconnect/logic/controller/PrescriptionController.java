package physicianconnect.logic.controller;

import physicianconnect.logic.exceptions.InvalidPrescriptionException;
import physicianconnect.logic.validation.PrescriptionValidator;
import physicianconnect.objects.Prescription;
import physicianconnect.persistence.interfaces.PrescriptionPersistence;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controller for prescription‐related use cases.
 * Delegates validation to PrescriptionValidator and persistence to PrescriptionPersistence.
 */
public class PrescriptionController {

    private final PrescriptionPersistence prescriptionPersistence;

    public PrescriptionController(PrescriptionPersistence prescriptionPersistence) {
        this.prescriptionPersistence = prescriptionPersistence;
    }

    /**
     * Create and persist a new Prescription record.
     *
     * @param physicianId               the ID of the physician issuing the prescription
     * @param patientName               the patient’s name
     * @param medicationName            the name of the medication
     * @param defaultMedicationDosage   the medication’s default dosage (from Medication object)
     * @param dosage                    the dosage instructions (entered or auto‐filled)
     * @param frequency                 how often the patient should take it
     * @param notes                     any optional notes (may be blank)
     * @throws InvalidPrescriptionException if any validation rule fails
     */
    public void createPrescription(
            String physicianId,
            String patientName,
            String medicationName,
            String defaultMedicationDosage,
            String dosage,
            String frequency,
            String notes
    ) throws InvalidPrescriptionException {
        // 1) Validate raw inputs
        PrescriptionValidator.validateMedicationName(medicationName);
        PrescriptionValidator.validateDosage(dosage);
        // We treat the “start date” as today’s date
        PrescriptionValidator.validateStartDate(LocalDate.now());
        // Duration is not entered by the UI, so skip validateDuration

        // 2) Build the timestamp
        String createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        // 3) Construct a new domain object (ID=0 → auto‐assigned by persistence)
        Prescription presc = new Prescription(
                0,                      // id
                physicianId,            // who wrote it
                patientName,            // patient’s name
                medicationName,         // med name
                defaultMedicationDosage,// default dosage
                dosage,                 // final dosage instructions
                frequency,              // frequency
                notes,                  // notes
                createdAt               // creation timestamp
        );

        // 4) Delegate to persistence
        prescriptionPersistence.addPrescription(presc);
    }
}