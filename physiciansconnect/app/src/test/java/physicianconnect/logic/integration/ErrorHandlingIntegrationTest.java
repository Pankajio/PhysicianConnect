package physicianconnect.logic.integration;

import org.junit.jupiter.api.*;

import physicianconnect.logic.manager.PhysicianManager;
import physicianconnect.objects.*;
import physicianconnect.persistence.*;

import static org.junit.jupiter.api.Assertions.*;

public class ErrorHandlingIntegrationTest {

    private PhysicianManager physicianManager;

    @BeforeEach
    public void setup() {
        PersistenceFactory.initialize(PersistenceType.TEST, true);
        physicianManager = new PhysicianManager(PersistenceFactory.getPhysicianPersistence());
    }

    @AfterEach
    public void teardown() {
        PersistenceFactory.reset();
    }

    @Test
    public void testAddPhysicianWithNullIdThrows() {
        Physician badDoc = new Physician(null, "NoId", "noid@fail.com", "fail");
        assertThrows(RuntimeException.class, () -> physicianManager.addPhysician(badDoc));
    }
}