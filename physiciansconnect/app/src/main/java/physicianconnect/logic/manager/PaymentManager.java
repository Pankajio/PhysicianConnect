package physicianconnect.logic.manager;

import physicianconnect.objects.Payment;
import physicianconnect.persistence.interfaces.PaymentPersistence;
import java.util.List;

public class PaymentManager {
    private final PaymentPersistence paymentDB;

    public PaymentManager(PaymentPersistence paymentDB) { this.paymentDB = paymentDB; }

    public void addPayment(Payment payment) { paymentDB.addPayment(payment); }
    public List<Payment> getPaymentsByInvoice(String invoiceId) { return paymentDB.getPaymentsByInvoice(invoiceId); }
    public List<Payment> getPaymentsByMonth(int year, int month) { return paymentDB.getPaymentsByMonth(year, month); }
}