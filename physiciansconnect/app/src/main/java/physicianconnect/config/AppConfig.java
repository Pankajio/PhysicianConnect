package physicianconnect.config;

import physicianconnect.persistence.PersistenceType;

public class AppConfig {
    private static PersistenceType persistenceType = PersistenceType.PROD;
    private static boolean seedData = true;

    public static void setPersistenceType(PersistenceType type) {
        persistenceType = type;
    }

    public static PersistenceType getPersistenceType() {
        return persistenceType;
    }

    public static void setSeedData(boolean seed) {
        seedData = seed;
    }

    public static boolean shouldSeedData() {
        return seedData;
    }
} 