package physicianconnect.persistence.stub;

import physicianconnect.objects.Medication;
import physicianconnect.persistence.interfaces.MedicationPersistence;

import java.util.ArrayList;
import java.util.List;

public class MedicationPersistenceStub implements MedicationPersistence {
    private final List<Medication> medications;

    public MedicationPersistenceStub(boolean seed) {
        medications = new ArrayList<>();
        if (seed) {
            medications.add(new Medication("Ibuprofen", "200mg", "Once a day", "Take with water"));
            medications.add(new Medication("Amoxicillin", "500mg", "Three times a day", "Take with food"));
        }
    }

    @Override
    public void addMedication(Medication medication) {
        if (medication != null) {
            medications.add(medication);
        }
    }

    @Override
    public void deleteMedication(Medication medication) {
        medications.remove(medication);
    }

    @Override
    public void deleteAllMedications() {
        medications.clear();
    }

    @Override
    public List<Medication> getAllMedications() {
        return new ArrayList<>(medications);
    }

    public void close() {
        medications.clear();
    }
}
