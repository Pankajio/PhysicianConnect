package physicianconnect.persistence.stub;

import org.junit.jupiter.api.*;
import physicianconnect.objects.Payment;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PaymentPersistenceStubTest {

    private PaymentPersistenceStub stub;

    @BeforeEach
    void setUp() {
        stub = new PaymentPersistenceStub(false);
    }

    @Test
    void testAddAndGetPaymentsByInvoice() {
        Payment p = new Payment("pid1", "inv1", 50.0, "Cash");
        stub.addPayment(p);
        List<Payment> payments = stub.getPaymentsByInvoice("inv1");
        assertEquals(1, payments.size());
        assertEquals(50.0, payments.get(0).getAmount());
    }

    @Test
    void testAddPaymentNullDoesNothing() {
        stub.addPayment(null);
        // Should not throw and list remains empty
        assertTrue(stub.getPaymentsByInvoice("any").isEmpty());
    }

    @Test
    void testGetPaymentsByInvoiceNotFound() {
        Payment p = new Payment("pid1", "inv1", 50.0, "Cash");
        stub.addPayment(p);
        List<Payment> payments = stub.getPaymentsByInvoice("inv2");
        assertTrue(payments.isEmpty());
    }

    @Test
    void testGetPaymentsByMonth() {
        Payment p = new Payment("pid1", "inv1", 50.0, "Cash");
        stub.addPayment(p);
        int year = p.getPaidAt().getYear();
        int month = p.getPaidAt().getMonthValue();
        List<Payment> payments = stub.getPaymentsByMonth(year, month);
        assertFalse(payments.isEmpty());
    }

    @Test
    void testGetPaymentsByMonthNotFound() {
        Payment p = new Payment("pid1", "inv1", 50.0, "Cash");
        stub.addPayment(p);
        // Use a year/month that does not match
        List<Payment> payments = stub.getPaymentsByMonth(1999, 1);
        assertTrue(payments.isEmpty());
    }

    @Test
    void testDeletePaymentById() {
        Payment p = new Payment("pid1", "inv1", 50.0, "Cash");
        stub.addPayment(p);
        stub.deletePaymentById("pid1");
        assertTrue(stub.getPaymentsByInvoice("inv1").isEmpty());
    }

    @Test
    void testDeletePaymentByIdNotFoundDoesNothing() {
        // Should not throw
        stub.deletePaymentById("notfound");
        // Still empty
        assertTrue(stub.getPaymentsByInvoice("inv1").isEmpty());
    }

    @Test
    void testDeleteAllPayments() {
        stub.addPayment(new Payment("pid1", "inv1", 50.0, "Cash"));
        stub.addPayment(new Payment("pid2", "inv2", 100.0, "Card"));
        stub.deleteAllPayments();
        assertTrue(stub.getPaymentsByInvoice("inv1").isEmpty());
        assertTrue(stub.getPaymentsByInvoice("inv2").isEmpty());
    }
}