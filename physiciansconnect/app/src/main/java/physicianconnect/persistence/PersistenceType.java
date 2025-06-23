package physicianconnect.persistence;

public enum PersistenceType {
    STUB, // In-memory storage with optional seeding
    TEST, // Lightweight for unit/integration tests (possibly no seed, resettable)
    PROD // Real DB for production or full-stack testing
}
