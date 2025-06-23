package physicianconnect.persistence.stub;

import physicianconnect.objects.Payment;
import physicianconnect.persistence.interfaces.PaymentPersistence;

import java.time.LocalDateTime;
import java.util.*;

public class PaymentPersistenceStub implements PaymentPersistence {
    private final List<Payment> payments;

    public PaymentPersistenceStub() {
        this(true);
    }

    public PaymentPersistenceStub(boolean seed) {
        payments = new ArrayList<>();
        if (seed) {
            // Example seeded payments
            payments.add(new Payment("pay-1", "inv-3", 75, "Card"));
            payments.add(new Payment("pay-2", "inv-4", 100, "Cash"));
        }
    }

    @Override
    public void addPayment(Payment payment) {
        if (payment != null) {
            payments.add(payment);
        }
    }

    @Override
    public List<Payment> getPaymentsByInvoice(String invoiceId) {
        List<Payment> result = new ArrayList<>();
        for (Payment p : payments) {
            if (p.getInvoiceId().equals(invoiceId)) result.add(p);
        }
        return result;
    }

    @Override
    public List<Payment> getPaymentsByMonth(int year, int month) {
        List<Payment> result = new ArrayList<>();
        for (Payment p : payments) {
            LocalDateTime paidAt = p.getPaidAt();
            if (paidAt.getYear() == year && paidAt.getMonthValue() == month)
                result.add(p);
        }
        return result;
    }

    public void deletePaymentById(String id) {
        payments.removeIf(p -> p.getId().equals(id));
    }

    public void deleteAllPayments() {
        payments.clear();
    }
}