package physicianconnect.objects;

public class Medication {
    private final String name;
    private final String dosage;
    private final String defaultFrequency;
    private final String defaultNotes;

    public Medication(String name, String dosage, String defaultFrequency, String defaultNotes) {
        this.name = name;
        this.dosage = dosage;
        this.defaultFrequency = defaultFrequency;
        this.defaultNotes = defaultNotes;
    }

    public String getName() { return name; }
    public String getDosage() { return dosage; }
    public String getDefaultFrequency() { return defaultFrequency; }
    public String getDefaultNotes() { return defaultNotes; }

    // Being Used in prescription
    @Override
    public String toString() {
        return name + " - " + dosage;
    }
}