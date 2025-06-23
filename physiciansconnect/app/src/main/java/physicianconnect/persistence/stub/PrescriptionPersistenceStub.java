package physicianconnect.persistence.stub;

import physicianconnect.objects.Prescription;
import physicianconnect.persistence.interfaces.PrescriptionPersistence;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class PrescriptionPersistenceStub implements PrescriptionPersistence {
    private final Map<Integer, Prescription> prescriptions = new HashMap<>();
    private int nextId = 1;

public PrescriptionPersistenceStub(boolean seed) {
        if (seed) {
            String now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            addPrescription(new Prescription(
                nextId++,
                "1", // Physician ID
                "John Doe", // Patient Name
                "Aspirin", // Medication Name
                "500mg", // Default Dosage
                "500mg", // Dosage
                "Twice a day", // Frequency
                "Take with food", // Notes
                now // Date Prescribed as String
            ));
            addPrescription(new Prescription(
                nextId++,
                "2",
                "Jane Smith",
                "Ibuprofen",
                "200mg",
                "200mg",
                "Once a day",
                "Take with water",
                now // Date Prescribed as String
            ));
        }
    }

    @Override
    public void addPrescription(Prescription prescription) {
        Prescription withId = new Prescription(
            nextId++,
            prescription.getPhysicianId(),
            prescription.getPatientName(),
            prescription.getMedicationName(),
            prescription.getDefaultDosage(),
            prescription.getDosage(),
            prescription.getFrequency(),
            prescription.getNotes(),
            prescription.getDatePrescribed()
        );
        prescriptions.put(withId.getId(), withId);
    }

    @Override
    public List<Prescription> getPrescriptionsForPatient(String patientName) {
        List<Prescription> result = new ArrayList<>();
        for (Prescription p : prescriptions.values()) {
            if (p.getPatientName().equalsIgnoreCase(patientName)) {
                result.add(p);
            }
        }
        return result;
    }

    @Override
    public void deletePrescriptionById(int id) {
        prescriptions.remove(id);
    }

    @Override
    public void deleteAllPrescriptions() {
        prescriptions.clear();
    }

    @Override
    public List<Prescription> getAllPrescriptions() {
        return new ArrayList<>(prescriptions.values());
    }
}