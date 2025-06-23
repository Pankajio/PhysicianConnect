package physicianconnect.logic.controller;

import physicianconnect.logic.manager.InvoiceManager;
import physicianconnect.logic.manager.PaymentManager;
import physicianconnect.logic.validation.BillingValidator;
import physicianconnect.logic.exceptions.InvalidBillingException;
import physicianconnect.objects.Invoice;
import physicianconnect.objects.Payment;
import physicianconnect.objects.ServiceItem;

import java.util.List;
import java.util.UUID;

public class BillingController {
    private final InvoiceManager invoiceManager;
    private final PaymentManager paymentManager;

    public BillingController(InvoiceManager invoiceManager, PaymentManager paymentManager) {
        this.invoiceManager = invoiceManager;
        this.paymentManager = paymentManager;
    }

    public Invoice createInvoice(String appointmentId, String patientName, List<ServiceItem> services, double insuranceAdjustment) {
        BillingValidator.validateInvoiceServices(services);
        String id = UUID.randomUUID().toString();
        Invoice invoice = new Invoice(id, appointmentId, patientName, services, insuranceAdjustment);
        invoiceManager.addInvoice(invoice);
        return invoice;
    }

    public void recordPayment(String invoiceId, double amount, String method) {
        Invoice invoice = invoiceManager.getInvoiceById(invoiceId);
        BillingValidator.validatePaymentAmount(amount, invoice.getBalance());
        String paymentId = UUID.randomUUID().toString();
        Payment payment = new Payment(paymentId, invoiceId, amount, method);
        paymentManager.addPayment(payment);

        // Update invoice balance and status
        double newBalance = invoice.getBalance() - amount;
        invoice.setBalance(newBalance);
        if (newBalance == 0) {
            invoice.setStatus("Paid");
        } else {
            invoice.setStatus("Partial");
        }
        invoiceManager.updateInvoice(invoice);
    }

    public List<Invoice> getInvoicesByMonth(int year, int month) {
        return invoiceManager.getInvoicesByMonth(year, month);
    }

    public List<Payment> getPaymentsByInvoice(String invoiceId) {
        return paymentManager.getPaymentsByInvoice(invoiceId);
    }

    public List<Invoice> getAllInvoices() {
    return invoiceManager.getAllInvoices();
}

public Invoice getInvoiceById(String id) {
    return invoiceManager.getInvoiceById(id);
}

public void deleteInvoice(String id) {
    invoiceManager.deleteInvoice(id);
}
}