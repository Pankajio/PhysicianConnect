package physicianconnect.objects;

import java.time.LocalDateTime;
import java.util.List;

public class Invoice {
    private final String id;
    private final String appointmentId;
    private final String patientName;
    private final List<ServiceItem> services;
    private final double insuranceAdjustment;
    private double totalAmount;
    private double balance;
    private String status; // "Sent", "Partial", "Paid"
    private final LocalDateTime createdAt;

    public Invoice(String id, String appointmentId, String patientName, List<ServiceItem> services, double insuranceAdjustment) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.patientName = patientName;
        this.services = services;
        this.insuranceAdjustment = insuranceAdjustment;
        this.totalAmount = calculateTotal();
        this.balance = totalAmount;
        this.status = "Sent";
        this.createdAt = LocalDateTime.now();
    }

    private double calculateTotal() {
        double sum = services.stream().mapToDouble(ServiceItem::getCost).sum();
        return Math.max(0, sum - insuranceAdjustment);
    }

    // Getters and setters...
    public String getId() { return id; }
    public String getAppointmentId() { return appointmentId; }
    public String getPatientName() { return patientName; }
    public List<ServiceItem> getServices() { return services; }
    public double getInsuranceAdjustment() { return insuranceAdjustment; }
    public double getTotalAmount() { return totalAmount; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}