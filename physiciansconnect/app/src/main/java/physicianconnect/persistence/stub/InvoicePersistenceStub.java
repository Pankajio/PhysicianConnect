package physicianconnect.persistence.stub;

import physicianconnect.objects.Invoice;
import physicianconnect.objects.ServiceItem;
import physicianconnect.persistence.interfaces.InvoicePersistence;

import java.time.LocalDateTime;
import java.util.*;

public class InvoicePersistenceStub implements InvoicePersistence {
    private final Map<String, Invoice> invoices;
    private int nextId = 1;

    public InvoicePersistenceStub() {
        this(true);
    }

    public InvoicePersistenceStub(boolean seed) {
        invoices = new HashMap<>();
        if (seed) {
            // Example seeded invoices
            invoices.put("inv-1", new Invoice("inv-1", "1", "Alice Johnson",
                    Arrays.asList(new ServiceItem("Consult", 100), new ServiceItem("Lab", 50)),
                    0));
            invoices.put("inv-2", new Invoice("inv-2", "2", "Bob Brown",
                    Collections.singletonList(new ServiceItem("Consult", 100)),
                    20));
            invoices.put("inv-3", new Invoice("inv-3", "3", "Charlie Davis",
                    Arrays.asList(new ServiceItem("Consult", 100), new ServiceItem("XRay", 75)),
                    0));
            invoices.put("inv-4", new Invoice("inv-4", "4", "Diana Evans",
                    Collections.singletonList(new ServiceItem("Consult", 100)),
                    0));
            // Set balances and statuses for variety
            invoices.get("inv-3").setBalance(100);
            invoices.get("inv-3").setStatus("Partial");
            invoices.get("inv-4").setBalance(0);
            invoices.get("inv-4").setStatus("Paid");
        }
    }

    @Override
    public void addInvoice(Invoice invoice) {
        if (invoice != null && invoice.getId() != null) {
            invoices.put(invoice.getId(), invoice);
        }
    }

    @Override
    public Invoice getInvoiceById(String id) {
        return invoices.get(id);
    }

    @Override
    public List<Invoice> getInvoicesByMonth(int year, int month) {
        List<Invoice> result = new ArrayList<>();
        for (Invoice inv : invoices.values()) {
            LocalDateTime created = inv.getCreatedAt();
            if (created.getYear() == year && created.getMonthValue() == month) {
                result.add(inv);
            }
        }
        return result;
    }

    @Override
    public List<Invoice> getAllInvoices() {
        return new ArrayList<>(invoices.values());
    }

    @Override
    public void updateInvoice(Invoice invoice) {
        if (invoice != null && invoice.getId() != null) {
            invoices.put(invoice.getId(), invoice);
        }
    }

    public void deleteInvoiceById(String id) {
        invoices.remove(id);
    }

    public void deleteAllInvoices() {
        invoices.clear();
    }
}