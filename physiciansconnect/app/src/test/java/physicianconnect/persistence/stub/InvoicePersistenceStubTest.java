package physicianconnect.persistence.stub;

import org.junit.jupiter.api.*;
import physicianconnect.objects.Invoice;
import physicianconnect.objects.ServiceItem;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InvoicePersistenceStubTest {

    private InvoicePersistenceStub stub;

    @BeforeEach
    void setUp() {
        stub = new InvoicePersistenceStub(false);
    }

    @Test
    void testAddAndGetInvoice() {
        Invoice inv = new Invoice("inv1", "1", "Alice", List.of(new ServiceItem("Consult", 100)), 0);
        stub.addInvoice(inv);
        assertEquals("Alice", stub.getInvoiceById("inv1").getPatientName());
    }

    @Test
    void testAddInvoiceNullDoesNothing() {
        // Should not throw or add anything
        stub.addInvoice(null);
        assertTrue(stub.getAllInvoices().isEmpty());
    }

    @Test
    void testAddInvoiceNullIdDoesNothing() {
        Invoice inv = new Invoice(null, "1", "Alice", List.of(new ServiceItem("Consult", 100)), 0);
        stub.addInvoice(inv);
        assertTrue(stub.getAllInvoices().isEmpty());
    }

    @Test
    void testGetAllInvoices() {
        stub.addInvoice(new Invoice("inv1", "1", "Alice", List.of(new ServiceItem("Consult", 100)), 0));
        stub.addInvoice(new Invoice("inv2", "2", "Bob", List.of(new ServiceItem("Lab", 50)), 0));
        List<Invoice> all = stub.getAllInvoices();
        assertEquals(2, all.size());
    }

    @Test
    void testUpdateInvoice() {
        Invoice inv = new Invoice("inv1", "1", "Alice", List.of(new ServiceItem("Consult", 100)), 0);
        stub.addInvoice(inv);
        inv.setStatus("Paid");
        stub.updateInvoice(inv);
        assertEquals("Paid", stub.getInvoiceById("inv1").getStatus());
    }

    @Test
    void testUpdateInvoiceNullDoesNothing() {
        // Should not throw or update anything
        stub.updateInvoice(null);
        // Still empty
        assertTrue(stub.getAllInvoices().isEmpty());
    }

    @Test
    void testUpdateInvoiceNullIdDoesNothing() {
        Invoice inv = new Invoice(null, "1", "Alice", List.of(new ServiceItem("Consult", 100)), 0);
        stub.updateInvoice(inv);
        assertTrue(stub.getAllInvoices().isEmpty());
    }

    @Test
    void testDeleteInvoice() {
        stub.addInvoice(new Invoice("inv1", "1", "Alice", List.of(new ServiceItem("Consult", 100)), 0));
        stub.deleteInvoiceById("inv1");
        assertNull(stub.getInvoiceById("inv1"));
    }

    @Test
    void testDeleteAllInvoices() {
        stub.addInvoice(new Invoice("inv1", "1", "Alice", List.of(new ServiceItem("Consult", 100)), 0));
        stub.deleteAllInvoices();
        assertTrue(stub.getAllInvoices().isEmpty());
    }

    @Test
    void testGetInvoicesByMonth() {
        Invoice inv = new Invoice("inv1", "1", "Alice", List.of(new ServiceItem("Consult", 100)), 0);
        stub.addInvoice(inv);
        LocalDateTime created = inv.getCreatedAt();
        List<Invoice> result = stub.getInvoicesByMonth(created.getYear(), created.getMonthValue());
        assertEquals(1, result.size());
    }

    @Test
    void testGetInvoicesByMonthNotFound() {
        Invoice inv = new Invoice("inv1", "1", "Alice", List.of(new ServiceItem("Consult", 100)), 0);
        stub.addInvoice(inv);
        List<Invoice> result = stub.getInvoicesByMonth(1999, 1);
        assertTrue(result.isEmpty());
    }

    @Test
    void testConstructorWithSeed() {
        InvoicePersistenceStub seededStub = new InvoicePersistenceStub(true);
        assertFalse(seededStub.getAllInvoices().isEmpty());
    }
}