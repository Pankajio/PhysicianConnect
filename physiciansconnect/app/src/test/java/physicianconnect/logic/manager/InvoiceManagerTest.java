package physicianconnect.logic.manager;

import org.junit.jupiter.api.*;
import org.mockito.*;
import physicianconnect.objects.Invoice;
import physicianconnect.persistence.interfaces.InvoicePersistence;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InvoiceManagerTest {

    @Mock
    private InvoicePersistence mockPersistence;

    private InvoiceManager manager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        manager = new InvoiceManager(mockPersistence);
    }

    @Test
    void testAddInvoiceDelegates() {
        Invoice invoice = mock(Invoice.class);
        manager.addInvoice(invoice);
        verify(mockPersistence).addInvoice(invoice);
    }

    @Test
    void testGetInvoiceByIdDelegates() {
        Invoice invoice = mock(Invoice.class);
        when(mockPersistence.getInvoiceById("id")).thenReturn(invoice);
        assertEquals(invoice, manager.getInvoiceById("id"));
    }

    @Test
    void testGetInvoicesByMonthDelegates() {
        when(mockPersistence.getInvoicesByMonth(2025, 6)).thenReturn(List.of());
        assertNotNull(manager.getInvoicesByMonth(2025, 6));
    }

    @Test
    void testGetAllInvoicesDelegates() {
        when(mockPersistence.getAllInvoices()).thenReturn(List.of());
        assertNotNull(manager.getAllInvoices());
    }

    @Test
    void testUpdateInvoiceDelegates() {
        Invoice invoice = mock(Invoice.class);
        manager.updateInvoice(invoice);
        verify(mockPersistence).updateInvoice(invoice);
    }

    @Test
    void testDeleteInvoiceDelegates() {
        manager.deleteInvoice("id");
        verify(mockPersistence).deleteInvoiceById("id");
    }
}