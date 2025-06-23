package physicianconnect.config;

import org.junit.jupiter.api.*;
import physicianconnect.persistence.PersistenceType;

import static org.junit.jupiter.api.Assertions.*;

class AppConfigTest {

    @BeforeEach
    void resetConfig() {
        // Reset to defaults before each test
        AppConfig.setPersistenceType(PersistenceType.PROD);
        AppConfig.setSeedData(true);
    }

    @Test
    void testSetAndGetPersistenceType() {
        AppConfig.setPersistenceType(PersistenceType.TEST);
        assertEquals(PersistenceType.TEST, AppConfig.getPersistenceType());

        AppConfig.setPersistenceType(PersistenceType.PROD);
        assertEquals(PersistenceType.PROD, AppConfig.getPersistenceType());
    }

    @Test
    void testSetAndGetSeedData() {
        AppConfig.setSeedData(false);
        assertFalse(AppConfig.shouldSeedData());

        AppConfig.setSeedData(true);
        assertTrue(AppConfig.shouldSeedData());
    }
}