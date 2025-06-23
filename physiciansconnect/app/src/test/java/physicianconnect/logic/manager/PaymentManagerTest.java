package physicianconnect.logic.manager;

import org.junit.jupiter.api.*;
import org.mockito.*;
import physicianconnect.objects.Payment;
import physicianconnect.persistence.interfaces.PaymentPersistence;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentManagerTest {

    @Mock
    private PaymentPersistence mockPersistence;

    private PaymentManager manager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        manager = new PaymentManager(mockPersistence);
    }

    @Test
    void testAddPaymentDelegates() {
        Payment payment = mock(Payment.class);
        manager.addPayment(payment);
        verify(mockPersistence).addPayment(payment);
    }

    @Test
    void testGetPaymentsByInvoiceDelegates() {
        when(mockPersistence.getPaymentsByInvoice("inv1")).thenReturn(List.of());
        assertNotNull(manager.getPaymentsByInvoice("inv1"));
    }

    @Test
    void testGetPaymentsByMonthDelegates() {
        when(mockPersistence.getPaymentsByMonth(2025, 6)).thenReturn(List.of());
        assertNotNull(manager.getPaymentsByMonth(2025, 6));
    }
}