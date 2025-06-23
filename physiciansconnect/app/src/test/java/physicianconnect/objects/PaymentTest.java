package physicianconnect.objects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    @Test
    void testConstructorAndGetters() {
        Payment p = new Payment("pid", "inv1", 50.0, "Cash");
        assertEquals("pid", p.getId());
        assertEquals("inv1", p.getInvoiceId());
        assertEquals(50.0, p.getAmount());
        assertEquals("Cash", p.getMethod());
        assertNotNull(p.getPaidAt());
    }
}