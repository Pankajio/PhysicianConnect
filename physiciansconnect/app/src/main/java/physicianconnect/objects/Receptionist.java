package physicianconnect.objects;

public class Receptionist {
    private final String id;
    private String name;
    private final String email;
    private final String password;
    private boolean notifyAppointment;
    private boolean notifyBilling;
    private boolean notifyMessages;

    public Receptionist(String id, String name, String email, String password) {
        this(id, name, email, password, true, true, true);
    }

    public Receptionist(String id, String name, String email, String password,
            boolean notifyAppointment, boolean notifyBilling, boolean notifyMessages) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.notifyAppointment = notifyAppointment;
        this.notifyBilling = notifyBilling;
        this.notifyMessages = notifyMessages;
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

    public String getUserType() {
        return "receptionist";
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isNotifyAppointment() {
        return notifyAppointment;
    }

    public void setNotifyAppointment(boolean notifyAppointment) {
        this.notifyAppointment = notifyAppointment;
    }

    public boolean isNotifyBilling() {
        return notifyBilling;
    }

    public void setNotifyBilling(boolean notifyBilling) {
        this.notifyBilling = notifyBilling;
    }

    public boolean isNotifyMessages() {
        return notifyMessages;
    }

    public void setNotifyMessages(boolean notifyMessages) {
        this.notifyMessages = notifyMessages;
    }

}