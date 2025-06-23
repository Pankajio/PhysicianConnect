package physicianconnect.logic.controller;

import org.junit.jupiter.api.*;
import org.mockito.*;
import physicianconnect.logic.manager.InvoiceManager;
import physicianconnect.logic.manager.PaymentManager;
import physicianconnect.logic.validation.BillingValidator;
import physicianconnect.objects.Invoice;
import physicianconnect.objects.Payment;
import physicianconnect.objects.ServiceItem;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BillingControllerTest {

    @Mock
    private InvoiceManager invoiceManager;
    @Mock
    private PaymentManager paymentManager;

    private BillingController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new BillingController(invoiceManager, paymentManager);
    }

    @Test
    void testCreateInvoiceDelegates() {
        ServiceItem service = new ServiceItem("Consult", 100);
        Invoice invoice = new Invoice("id", "appt1", "Alice", List.of(service), 0);
        doNothing().when(invoiceManager).addInvoice(any());
        
        Invoice result = controller.createInvoice("appt1", "Alice", List.of(service), 0);

        verify(invoiceManager).addInvoice(any());
        assertEquals("Alice", result.getPatientName());
    }

    @Test
    void testRecordPaymentUpdatesInvoice() {
        ServiceItem service = new ServiceItem("Consult", 100);
        Invoice invoice = new Invoice("id", "appt1", "Alice", List.of(service), 0);
        invoice.setBalance(100);
        when(invoiceManager.getInvoiceById("id")).thenReturn(invoice);

        controller.recordPayment("id", 100, "Cash");

        verify(paymentManager).addPayment(any());
        verify(invoiceManager).updateInvoice(invoice);
        assertEquals(0, invoice.getBalance());
        assertEquals("Paid", invoice.getStatus());
    }

    @Test
    void testGetInvoicesByMonthDelegates() {
        when(invoiceManager.getInvoicesByMonth(2025, 6)).thenReturn(List.of());
        List<Invoice> result = controller.getInvoicesByMonth(2025, 6);
        assertNotNull(result);
        verify(invoiceManager).getInvoicesByMonth(2025, 6);
    }

    @Test
    void testGetPaymentsByInvoiceDelegates() {
        when(paymentManager.getPaymentsByInvoice("id")).thenReturn(List.of());
        List<Payment> result = controller.getPaymentsByInvoice("id");
        assertNotNull(result);
        verify(paymentManager).getPaymentsByInvoice("id");
    }

    @Test
    void testGetAllInvoicesDelegates() {
        when(invoiceManager.getAllInvoices()).thenReturn(List.of());
        List<Invoice> result = controller.getAllInvoices();
        assertNotNull(result);
        verify(invoiceManager).getAllInvoices();
    }

    @Test
    void testGetInvoiceByIdDelegates() {
        Invoice invoice = new Invoice("id", "appt1", "Alice", List.of(new ServiceItem("Consult", 100)), 0);
        when(invoiceManager.getInvoiceById("id")).thenReturn(invoice);
        Invoice result = controller.getInvoiceById("id");
        assertEquals(invoice, result);
        verify(invoiceManager).getInvoiceById("id");
    }

    @Test
    void testDeleteInvoiceDelegates() {
        controller.deleteInvoice("id");
        verify(invoiceManager).deleteInvoice("id");
    }
}