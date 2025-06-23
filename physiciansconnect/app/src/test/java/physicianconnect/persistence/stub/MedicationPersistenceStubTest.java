package physicianconnect.persistence.stub;

import org.junit.jupiter.api.*;
import physicianconnect.objects.Medication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MedicationPersistenceStubTest {

    private MedicationPersistenceStub stub;

    @BeforeEach
    void setUp() {
        stub = new MedicationPersistenceStub(false);
    }

    @Test
    void testAddAndGetMedication() {
        Medication m = new Medication("Aspirin", "10mg", "daily", "notes");
        stub.addMedication(m);
        List<Medication> meds = stub.getAllMedications();
        assertEquals(1, meds.size());
        assertEquals("Aspirin", meds.get(0).getName());
    }

    @Test
    void testDeleteMedication() {
        Medication m = new Medication("Aspirin", "10mg", "daily", "notes");
        stub.addMedication(m);
        stub.deleteMedication(m);
        assertTrue(stub.getAllMedications().isEmpty());
    }

    @Test
    void testDeleteAllMedications() {
        stub.addMedication(new Medication("Ibuprofen", "200mg", "Once a day", "Take with water"));
        stub.deleteAllMedications();
        assertTrue(stub.getAllMedications().isEmpty());
    }

    @Test
    void testCloseClearsMedications() {
        stub.addMedication(new Medication("Ibuprofen", "200mg", "Once a day", "Take with water"));
        stub.close();
        assertTrue(stub.getAllMedications().isEmpty());
    }
}