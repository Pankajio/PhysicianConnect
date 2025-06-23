package physicianconnect.objects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReceptionistTest {

    @Test
    void testConstructorAndGettersSetters() {
        Receptionist r = new Receptionist("id", "Alice", "alice@email.com", "pw");
        assertEquals("id", r.getId());
        assertEquals("Alice", r.getName());
        assertEquals("alice@email.com", r.getEmail());
        assertEquals("pw", r.getPassword());
        assertEquals("receptionist", r.getUserType());

        r.setName("Bob");
        assertEquals("Bob", r.getName());
    }
}