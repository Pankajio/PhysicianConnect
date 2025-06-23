package physicianconnect.logic.manager;

import physicianconnect.objects.Invoice;
import physicianconnect.persistence.interfaces.InvoicePersistence;
import java.util.List;

public class InvoiceManager {
    private final InvoicePersistence invoiceDB;

    public InvoiceManager(InvoicePersistence invoiceDB) { this.invoiceDB = invoiceDB; }

    public void addInvoice(Invoice invoice) { invoiceDB.addInvoice(invoice); }
    public Invoice getInvoiceById(String id) { return invoiceDB.getInvoiceById(id); }
    public List<Invoice> getInvoicesByMonth(int year, int month) { return invoiceDB.getInvoicesByMonth(year, month); }
    public List<Invoice> getAllInvoices() { return invoiceDB.getAllInvoices(); }
    public void updateInvoice(Invoice invoice) { invoiceDB.updateInvoice(invoice); }
    public void deleteInvoice(String id) {
    invoiceDB.deleteInvoiceById(id);
}
}