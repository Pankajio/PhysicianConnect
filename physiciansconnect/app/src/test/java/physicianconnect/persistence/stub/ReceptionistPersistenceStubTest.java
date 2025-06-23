package physicianconnect.persistence.stub;

import org.junit.jupiter.api.*;
import physicianconnect.objects.Receptionist;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReceptionistPersistenceStubTest {

    private ReceptionistPersistenceStub stub;

    @BeforeEach
    void setUp() {
        stub = new ReceptionistPersistenceStub(false);
    }

    @Test
    void testAddAndGetReceptionist() {
        Receptionist r = new Receptionist("id1", "Alice", "alice@email.com", "pw");
        stub.addReceptionist(r);
        assertEquals(r.getName(), stub.getReceptionistById("id1").getName());
    }

    @Test
    void testAddReceptionistNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> stub.addReceptionist(null));
    }

    @Test
    void testAddReceptionistNullIdThrows() {
        Receptionist r = new Receptionist(null, "Alice", "alice@email.com", "pw");
        assertThrows(IllegalArgumentException.class, () -> stub.addReceptionist(r));
    }

    @Test
    void testAddReceptionistBlankIdThrows() {
        Receptionist r = new Receptionist("   ", "Alice", "alice@email.com", "pw");
        assertThrows(IllegalArgumentException.class, () -> stub.addReceptionist(r));
    }

    @Test
    void testAddReceptionistDuplicateIgnored() {
        Receptionist r1 = new Receptionist("id1", "Alice", "alice@email.com", "pw");
        Receptionist r2 = new Receptionist("id1", "Bob", "bob@email.com", "pw");
        stub.addReceptionist(r1);
        stub.addReceptionist(r2);
        // Should not overwrite the first
        assertEquals("Alice", stub.getReceptionistById("id1").getName());
    }

    @Test
    void testGetReceptionistByEmail() {
        Receptionist r = new Receptionist("id1", "Alice", "alice@email.com", "pw");
        stub.addReceptionist(r);
        assertEquals(r, stub.getReceptionistByEmail("alice@email.com"));
    }

    @Test
    void testGetReceptionistByEmailCaseInsensitive() {
        Receptionist r = new Receptionist("id1", "Alice", "alice@email.com", "pw");
        stub.addReceptionist(r);
        assertEquals(r, stub.getReceptionistByEmail("ALICE@email.com"));
    }

    @Test
    void testGetReceptionistByEmailNotFound() {
        assertNull(stub.getReceptionistByEmail("notfound@email.com"));
    }

    @Test
    void testGetReceptionistByIdNotFound() {
        assertNull(stub.getReceptionistById("notfound"));
    }

    @Test
    void testGetAllReceptionists() {
        stub.addReceptionist(new Receptionist("id1", "Alice", "alice@email.com", "pw"));
        stub.addReceptionist(new Receptionist("id2", "Bob", "bob@email.com", "pw"));
        List<Receptionist> all = stub.getAllReceptionists();
        assertEquals(2, all.size());
    }

    @Test
    void testUpdateReceptionist() {
        Receptionist r = new Receptionist("id1", "Alice", "alice@email.com", "pw");
        stub.addReceptionist(r);
        r.setName("Updated");
        stub.updateReceptionist(r);
        assertEquals("Updated", stub.getReceptionistById("id1").getName());
    }

    @Test
    void testUpdateReceptionistNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> stub.updateReceptionist(null));
    }

    @Test
    void testUpdateReceptionistNullIdThrows() {
        Receptionist r = new Receptionist(null, "Alice", "alice@email.com", "pw");
        assertThrows(IllegalArgumentException.class, () -> stub.updateReceptionist(r));
    }

    @Test
    void testUpdateReceptionistNotFoundThrows() {
        Receptionist r = new Receptionist("id1", "Alice", "alice@email.com", "pw");
        assertThrows(IllegalArgumentException.class, () -> stub.updateReceptionist(r));
    }

    @Test
    void testDeleteReceptionist() {
        stub.addReceptionist(new Receptionist("id1", "Alice", "alice@email.com", "pw"));
        stub.deleteReceptionist("id1");
        assertNull(stub.getReceptionistById("id1"));
    }

    @Test
    void testDeleteReceptionistNotFoundDoesNothing() {
        // Should not throw
        assertDoesNotThrow(() -> stub.deleteReceptionist("notfound"));
    }

    @Test
    void testDeleteAllReceptionists() {
        stub.addReceptionist(new Receptionist("id1", "Alice", "alice@email.com", "pw"));
        stub.deleteAllReceptionists();
        assertTrue(stub.getAllReceptionists().isEmpty());
    }

    @Test
    void testCloseSetsReceptionistsToNull() {
        stub.close();
        // Reflection to check private field is null
        try {
            var field = ReceptionistPersistenceStub.class.getDeclaredField("receptionists");
            field.setAccessible(true);
            assertNull(field.get(stub));
        } catch (Exception e) {
            fail("Reflection failed");
        }
    }
}