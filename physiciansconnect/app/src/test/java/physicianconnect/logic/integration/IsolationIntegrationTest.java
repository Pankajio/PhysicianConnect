package physicianconnect.logic.integration;

import org.junit.jupiter.api.*;

import physicianconnect.logic.manager.PhysicianManager;
import physicianconnect.objects.*;
import physicianconnect.persistence.*;

import static org.junit.jupiter.api.Assertions.*;

public class IsolationIntegrationTest {

    @BeforeEach
    public void setup() {
        PersistenceFactory.initialize(PersistenceType.TEST, true);
    }

    @AfterEach
    public void teardown() {
        PersistenceFactory.reset();
    }

    @Test
    public void testDataIsolation() {
        PhysicianManager manager = new PhysicianManager(PersistenceFactory.getPhysicianPersistence());
        manager.addPhysician(new Physician("iso", "Dr. Iso", "iso@iso.com", "iso"));
        assertNotNull(manager.getPhysicianById("iso"));

        // After reset, this physician should not exist
        manager.deleteAll();
        PersistenceFactory.reset();
        PersistenceFactory.initialize(PersistenceType.TEST, true);
        manager = new PhysicianManager(PersistenceFactory.getPhysicianPersistence());
        assertNull(manager.getPhysicianById("iso"));
    }
}