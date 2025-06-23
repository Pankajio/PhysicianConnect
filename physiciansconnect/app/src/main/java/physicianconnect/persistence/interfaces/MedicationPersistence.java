package physicianconnect.persistence.interfaces;

import physicianconnect.objects.Medication;

import java.util.List;

public interface MedicationPersistence {
    void addMedication(Medication medication);

    void deleteMedication(Medication medication);

    void deleteAllMedications();

    List<Medication> getAllMedications(); // optional global list
}
