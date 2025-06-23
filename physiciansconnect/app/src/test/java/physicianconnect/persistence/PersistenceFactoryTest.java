package physicianconnect.persistence;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class PersistenceFactoryTest {

    @BeforeEach
    public void setup() {
        PersistenceFactory.reset();
    }

    @AfterEach
    public void teardown() {
        PersistenceFactory.reset();
    }

    @Test
    public void testInitializeProd() {
        PersistenceFactory.initialize(PersistenceType.PROD, false);
        assertAllPersistenceNotNull();
    }

    @Test
    public void testInitializeTest() {
        PersistenceFactory.initialize(PersistenceType.TEST, false);
        assertAllPersistenceNotNull();
    }

    @Test
    public void testFallbackToStubs() {
        PersistenceFactory.initialize(PersistenceType.STUB, false);
        assertAllPersistenceNotNull();
    }

    @Test
    public void testResetClearsAll() {
        PersistenceFactory.initialize(PersistenceType.STUB, false);
        PersistenceFactory.reset();
        assertAllPersistenceNull();
    }

    @Test
    public void testFallbackToStubsOnException() {
        // Force ConnectionManager.get() to throw, causing fallbackToStubs to be called
        try (var connMock = mockStatic(ConnectionManager.class)) {
            connMock.when(() -> ConnectionManager.initialize(anyString())).thenThrow(new RuntimeException("fail"));
            PersistenceFactory.reset();
            PersistenceFactory.initialize(PersistenceType.PROD, false);
            assertAllPersistenceNotNull();
        }
    }

    @Test
    public void testNotificationPersistenceFallback() {
        // Force ConnectionManager.get() to throw in getNotificationPersistence
        try (var connMock = mockStatic(ConnectionManager.class)) {
            connMock.when(ConnectionManager::get).thenThrow(new RuntimeException("fail"));
            PersistenceFactory.reset();
            // notificationPersistence is null, so this will try to create NotificationDB and fail
            assertNotNull(PersistenceFactory.getNotificationPersistence());
        }
    }

    @Test
    public void testInjectTestUserForGraderAddsAppointments() {
        // Use STUB so appointmentPersistence is not null
        PersistenceFactory.initialize(PersistenceType.STUB, false);
        // This will call injectTestUserForGrader internally
        assertNotNull(PersistenceFactory.getAppointmentPersistence().getAllAppointments());
    }

    @Test
    void testInitializeSkipsIfAlreadyInitialized() {
        // First initialize
        PersistenceFactory.initialize(PersistenceType.STUB, false);
        Object oldPhysicianPersistence = PersistenceFactory.getPhysicianPersistence();

        // Second initialize should do nothing (guard clause triggers)
        PersistenceFactory.initialize(PersistenceType.PROD, false);

        // Should be the same object, not re-initialized
        assertSame(oldPhysicianPersistence, PersistenceFactory.getPhysicianPersistence());
    }

    private void assertAllPersistenceNotNull() {
        assertNotNull(PersistenceFactory.getPhysicianPersistence());
        assertNotNull(PersistenceFactory.getAppointmentPersistence());
        assertNotNull(PersistenceFactory.getMedicationPersistence());
        assertNotNull(PersistenceFactory.getPrescriptionPersistence());
        assertNotNull(PersistenceFactory.getReferralPersistence());
        assertNotNull(PersistenceFactory.getMessageRepository());
        assertNotNull(PersistenceFactory.getReceptionistPersistence());
        assertNotNull(PersistenceFactory.getInvoicePersistence());
        assertNotNull(PersistenceFactory.getPaymentPersistence());
        assertNotNull(PersistenceFactory.getNotificationPersistence());
    }

    private void assertAllPersistenceNull() {
        assertNull(PersistenceFactory.getPhysicianPersistence());
        assertNull(PersistenceFactory.getAppointmentPersistence());
        assertNull(PersistenceFactory.getMedicationPersistence());
        assertNull(PersistenceFactory.getPrescriptionPersistence());
        assertNull(PersistenceFactory.getReferralPersistence());
        assertNull(PersistenceFactory.getMessageRepository());
        assertNull(PersistenceFactory.getReceptionistPersistence());
        assertNull(PersistenceFactory.getInvoicePersistence());
        assertNull(PersistenceFactory.getPaymentPersistence());
        // NotificationPersistence may auto-initialize, so skip or check for null if possible
    }
}