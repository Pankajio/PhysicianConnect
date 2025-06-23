package physicianconnect.persistence.interfaces;

import physicianconnect.objects.Prescription;
import java.util.List;

public interface PrescriptionPersistence {
    void addPrescription(Prescription prescription);
    List<Prescription> getPrescriptionsForPatient(String patientName);
    List<Prescription> getAllPrescriptions();
    void deletePrescriptionById(int id);
    void deleteAllPrescriptions();
}