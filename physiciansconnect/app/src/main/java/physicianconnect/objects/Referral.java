package physicianconnect.objects;

public class Referral {
    private int id;
    private String physicianId;
    private String patientName;
    private String referralType; // e.g., "Lab Test", "Specialist"
    private String details;      // instructions/details for the patient
    private String dateCreated;

    public Referral(int id, String physicianId, String patientName, String referralType, String details, String dateCreated) {
        this.id = id;
        this.physicianId = physicianId;
        this.patientName = patientName;
        this.referralType = referralType;
        this.details = details;
        this.dateCreated = dateCreated;
    }

    // Getters and setters
    public int getId() { return id; }
    public String getPhysicianId() { return physicianId; }
    public String getPatientName() { return patientName; }
    public String getReferralType() { return referralType; }
    public String getDetails() { return details; }
    public String getDateCreated() { return dateCreated; }
}