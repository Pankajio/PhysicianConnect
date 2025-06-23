package physicianconnect.objects;

public class Prescription {
    private final int id;
    private final String physicianId;
    private final String patientName;
    private final String medicationName;
    private final String defaultDosage;
    private final String dosage;
    private final String frequency;
    private final String notes;
    private final String datePrescribed;

    public Prescription(int id, String physicianId, String patientName, String medicationName, String defaultDosage, String dosage, String frequency, String notes, String datePrescribed) {
        this.id = id;
        this.physicianId = physicianId;
        this.patientName = patientName;
        this.medicationName = medicationName;
        this.defaultDosage = defaultDosage;
        this.dosage = dosage;
        this.frequency = frequency;
        this.notes = notes;
        this.datePrescribed = datePrescribed;
    }

    public int getId() { return id; }
    public String getPhysicianId() { return physicianId; }
    public String getPatientName() { return patientName; }
    public String getMedicationName() { return medicationName; }
    public String getDefaultDosage() { return defaultDosage; }
    public String getDosage() { return dosage; }
    public String getFrequency() { return frequency; }
    public String getNotes() { return notes; }
    public String getDatePrescribed() { return datePrescribed; }

    @Override
    public String toString() {
        return medicationName + " (" + (dosage != null && !dosage.isEmpty() ? dosage : defaultDosage) + "), " +
               "Frequency: " + frequency + (notes != null && !notes.isEmpty() ? ", Notes: " + notes : "");
    }
}