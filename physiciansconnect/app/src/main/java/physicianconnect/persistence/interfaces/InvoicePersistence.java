package physicianconnect.persistence.interfaces;

import physicianconnect.objects.Invoice;
import java.util.List;

public interface InvoicePersistence {
    void addInvoice(Invoice invoice);
    Invoice getInvoiceById(String id);
    List<Invoice> getInvoicesByMonth(int year, int month);
    List<Invoice> getAllInvoices();
    void updateInvoice(Invoice invoice);
    void deleteInvoiceById(String id);
}