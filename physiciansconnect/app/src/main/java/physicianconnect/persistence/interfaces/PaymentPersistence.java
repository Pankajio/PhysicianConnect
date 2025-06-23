package physicianconnect.persistence.interfaces;

import physicianconnect.objects.Payment;
import java.util.List;

public interface PaymentPersistence {
    void addPayment(Payment payment);
    List<Payment> getPaymentsByInvoice(String invoiceId);
    List<Payment> getPaymentsByMonth(int year, int month);
}