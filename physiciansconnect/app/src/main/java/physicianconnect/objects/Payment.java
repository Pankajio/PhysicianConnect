package physicianconnect.objects;

import java.time.LocalDateTime;

public class Payment {
    private final String id;
    private final String invoiceId;
    private final double amount;
    private final String method; // "Cash", "Card", "Insurance"
    private final LocalDateTime paidAt;

    public Payment(String id, String invoiceId, double amount, String method) {
        this.id = id;
        this.invoiceId = invoiceId;
        this.amount = amount;
        this.method = method;
        this.paidAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getInvoiceId() { return invoiceId; }
    public double getAmount() { return amount; }
    public String getMethod() { return method; }
    public LocalDateTime getPaidAt() { return paidAt; }
}