package physicianconnect.objects;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InvoiceTest {

    @Test
    void testConstructorAndGetters() {
        ServiceItem s1 = new ServiceItem("Consult", 100);
        ServiceItem s2 = new ServiceItem("Lab", 50);
        Invoice invoice = new Invoice("inv1", "appt1", "Alice", List.of(s1, s2), 10);

        assertEquals("inv1", invoice.getId());
        assertEquals("appt1", invoice.getAppointmentId());
        assertEquals("Alice", invoice.getPatientName());
        assertEquals(2, invoice.getServices().size());
        assertEquals(10, invoice.getInsuranceAdjustment());
        assertEquals(140, invoice.getTotalAmount());
        assertEquals(140, invoice.getBalance());
        assertEquals("Sent", invoice.getStatus());
        assertNotNull(invoice.getCreatedAt());
    }

    @Test
    void testSetBalanceAndStatus() {
        Invoice invoice = new Invoice("inv2", "appt2", "Bob", List.of(new ServiceItem("XRay", 75)), 0);
        invoice.setBalance(20);
        assertEquals(20, invoice.getBalance());
        invoice.setStatus("Paid");
        assertEquals("Paid", invoice.getStatus());
    }
}