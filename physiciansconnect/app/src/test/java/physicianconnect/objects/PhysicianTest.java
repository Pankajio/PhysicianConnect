package physicianconnect.objects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PhysicianTest {

    @Test
    void testConstructorAndGettersSetters() {
        Physician p = new Physician("id", "Dr. Alice", "alice@email.com", "pw");
        assertEquals("id", p.getId());
        assertEquals("Dr. Alice", p.getName());
        assertEquals("alice@email.com", p.getEmail());
        assertEquals("pw", p.getPassword());

        p.setName("Dr. Bob");
        assertEquals("Dr. Bob", p.getName());

        p.setSpecialty("Cardiology");
        assertEquals("Cardiology", p.getSpecialty());

        p.setOfficeHours("9-5");
        assertEquals("9-5", p.getOfficeHours());

        p.setNotifyAppointment(false);
        assertFalse(p.isNotifyAppointment());

        p.setNotifyBilling(false);
        assertFalse(p.isNotifyBilling());

        p.setNotifyMessages(false);
        assertFalse(p.isNotifyMessages());

        p.setPhone("123-4567");
        assertEquals("123-4567", p.getPhone());

        p.setOfficeAddress("123 Main St");
        assertEquals("123 Main St", p.getOfficeAddress());

        assertEquals("physician", p.getUserType());
        assertTrue(p.toString().contains("Dr. Bob"));
    }
}