package physicianconnect.objects;

public class Physician {
    private final String id;
    private String name;
    private final String email;
    private final String password;

    private String phone;
    private String officeAddress;
    private String specialty;
    private String officeHours;
    private boolean notifyAppointment;
    private boolean notifyBilling;
    private boolean notifyMessages;

    public Physician(String id, String name, String email, String password) {
        this(id, name, email, password, "", "", true, true, true, "", "");
    }

    public Physician(String id, String name, String email, String password,
            String specialty, String officeHours,
            boolean notifyAppointment, boolean notifyBilling, boolean notifyMessages,
            String phone, String officeAddress) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.specialty = specialty;
        this.officeHours = officeHours;
        this.notifyAppointment = notifyAppointment;
        this.notifyBilling = notifyBilling;
        this.notifyMessages = notifyMessages;
        this.phone = phone;
        this.officeAddress = officeAddress;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getSpecialty() {
        return this.specialty;
    }

    public String getOfficeHours() {
        return this.officeHours;
    }

    public boolean isNotifyAppointment() {
        return notifyAppointment;
    }

    public boolean isNotifyBilling() {
        return notifyBilling;
    }

    public boolean isNotifyMessages() {
        return notifyMessages;
    }

    public String getPhone() {
        return phone;
    }

    public String getOfficeAddress() {
        return officeAddress;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public void setOfficeHours(String officeHours) {
        this.officeHours = officeHours;
    }

    public void setNotifyAppointment(boolean notifyAppointment) {
        this.notifyAppointment = notifyAppointment;
    }

    public void setNotifyBilling(boolean notifyBilling) {
        this.notifyBilling = notifyBilling;
    }

    public void setNotifyMessages(boolean notifyMessages) {
        this.notifyMessages = notifyMessages;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setOfficeAddress(String officeAddress) {
        this.officeAddress = officeAddress;
    }

    @Override
    public String toString() {
        return getName() + " (" + getEmail() + ")";
    }

    public String getUserType() {
        return "physician";
    }
}
