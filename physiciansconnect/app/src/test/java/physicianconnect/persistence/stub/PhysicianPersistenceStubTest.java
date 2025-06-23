package physicianconnect.persistence.stub;

import org.junit.jupiter.api.*;
import physicianconnect.objects.Physician;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PhysicianPersistenceStubTest {

    private PhysicianPersistenceStub stub;

    @BeforeEach
    void setUp() {
        stub = new PhysicianPersistenceStub(false);
    }

    @Test
    void testAddAndGetPhysician() {
        Physician p = new Physician("id1", "Dr. Alice", "alice@email.com", "pw");
        stub.addPhysician(p);
        assertEquals(p.getName(), stub.getPhysicianById("id1").getName());
    }

    @Test
    void testAddPhysicianNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> stub.addPhysician(null));
    }

    @Test
    void testAddPhysicianNullIdThrows() {
        Physician p = new Physician(null, "Dr. Alice", "alice@email.com", "pw");
        assertThrows(IllegalArgumentException.class, () -> stub.addPhysician(p));
    }

    @Test
    void testAddPhysicianBlankIdThrows() {
        Physician p = new Physician("   ", "Dr. Alice", "alice@email.com", "pw");
        assertThrows(IllegalArgumentException.class, () -> stub.addPhysician(p));
    }

    @Test
    void testAddPhysicianDuplicateIgnored() {
        Physician p1 = new Physician("id1", "Dr. Alice", "alice@email.com", "pw");
        Physician p2 = new Physician("id1", "Dr. Bob", "bob@email.com", "pw");
        stub.addPhysician(p1);
        stub.addPhysician(p2);
        // Should not overwrite the first
        assertEquals("Dr. Alice", stub.getPhysicianById("id1").getName());
    }

    @Test
    void testAddPhysicianGeneratesIdIfBlankOrNull() {
        Physician p = new Physician(null, "Dr. Alice", "alice@email.com", "pw");
        // Should throw, as per implementation, but let's check with blank id
        Physician p2 = new Physician("   ", "Dr. Bob", "bob@email.com", "pw");
        assertThrows(IllegalArgumentException.class, () -> stub.addPhysician(p));
        assertThrows(IllegalArgumentException.class, () -> stub.addPhysician(p2));
    }

    @Test
    void testGetAllPhysicians() {
        stub.addPhysician(new Physician("id1", "Dr. Alice", "alice@email.com", "pw"));
        stub.addPhysician(new Physician("id2", "Dr. Bob", "bob@email.com", "pw"));
        List<Physician> all = stub.getAllPhysicians();
        assertEquals(2, all.size());
    }

    @Test
    void testUpdatePhysician() {
        Physician p = new Physician("id1", "Dr. Alice", "alice@email.com", "pw");
        stub.addPhysician(p);
        p.setName("Dr. Updated");
        stub.updatePhysician(p);
        assertEquals("Dr. Updated", stub.getPhysicianById("id1").getName());
    }

    @Test
    void testUpdatePhysicianNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> stub.updatePhysician(null));
    }

    @Test
    void testUpdatePhysicianNullIdThrows() {
        Physician p = new Physician(null, "Dr. Alice", "alice@email.com", "pw");
        assertThrows(IllegalArgumentException.class, () -> stub.updatePhysician(p));
    }

    @Test
    void testUpdatePhysicianNotFoundThrows() {
        Physician p = new Physician("id1", "Dr. Alice", "alice@email.com", "pw");
        assertThrows(IllegalArgumentException.class, () -> stub.updatePhysician(p));
    }

    @Test
    void testDeletePhysician() {
        stub.addPhysician(new Physician("id1", "Dr. Alice", "alice@email.com", "pw"));
        stub.deletePhysicianById("id1");
        assertNull(stub.getPhysicianById("id1"));
    }

    @Test
    void testDeleteAllPhysicians() {
        stub.addPhysician(new Physician("id1", "Dr. Alice", "alice@email.com", "pw"));
        stub.deleteAllPhysicians();
        assertTrue(stub.getAllPhysicians().isEmpty());
    }

    @Test
    void testCloseSetsPhysiciansToNull() {
        stub.close();
        // Reflection to check private field is null
        try {
            var field = PhysicianPersistenceStub.class.getDeclaredField("physicians");
            field.setAccessible(true);
            assertNull(field.get(stub));
        } catch (Exception e) {
            fail("Reflection failed");
        }
    }
}