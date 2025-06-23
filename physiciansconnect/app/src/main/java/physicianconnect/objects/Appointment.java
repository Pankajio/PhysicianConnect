package physicianconnect.objects;

import java.time.LocalDateTime;

public class Appointment {
    private final int id; // <-- add this
    private final String physicianId;
    private final String patientName;
    private final LocalDateTime dateTime;
    private String notes;

    // Constructor for loading from DB (with id)
    public Appointment(int id, String physicianId, String patientName, LocalDateTime dateTime, String notes) {
        this.id = id;
        this.physicianId = physicianId;
        this.patientName = patientName;
        this.dateTime = dateTime;
        this.notes = notes;
    }

    // Constructor for creating new (id not known yet)
    public Appointment(String physicianId, String patientName, LocalDateTime dateTime) {
        this(-1, physicianId, patientName, dateTime, "");
    }

    public Appointment(String physicianId, String patientName, LocalDateTime dateTime, String notes) {
        this(-1, physicianId, patientName, dateTime, notes);
    }

    public int getId() {
        return id;
    }

    public String getPhysicianId() {
        return physicianId;
    }

    public String getPatientName() {
        return patientName;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Being used in dashboard
    @Override
    public String toString() {
        return "Appointment with " + patientName + " on " +
               dateTime.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a"));
    }
}