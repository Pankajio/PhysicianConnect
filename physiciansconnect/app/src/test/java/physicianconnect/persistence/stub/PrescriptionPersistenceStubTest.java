package physicianconnect.persistence.stub;

import org.junit.jupiter.api.*;
import physicianconnect.objects.Prescription;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PrescriptionPersistenceStubTest {

    private PrescriptionPersistenceStub stub;

    @BeforeEach
    public void setup() {
        stub = new PrescriptionPersistenceStub(false);
    }

    @Test
    public void testAddAndGetPrescription() {
        Prescription p = new Prescription(0, "doc1", "Patient A", "Ibuprofen", "200mg", "200mg", "Once", "Take with food", "2025-06-01T10:00");
        stub.addPrescription(p);

        List<Prescription> all = stub.getAllPrescriptions();
        assertEquals(1, all.size());
        assertEquals("Ibuprofen", all.get(0).getMedicationName());
    }

    @Test
    public void testDeletePrescriptionById() {
        stub.addPrescription(new Prescription(0, "doc1", "Patient A", "Ibuprofen", "200mg", "200mg", "Once", "", "2025-06-01T10:00"));
        int id = stub.getAllPrescriptions().get(0).getId();
        stub.deletePrescriptionById(id);
        assertTrue(stub.getAllPrescriptions().isEmpty());
    }

    @Test
    public void testDeleteAllPrescriptions() {
        stub.addPrescription(new Prescription(0, "doc1", "A", "Ibuprofen", "200mg", "200mg", "Once", "", "2025-06-01T10:00"));
        stub.addPrescription(new Prescription(0, "doc2", "B", "Amoxicillin", "500mg", "500mg", "Twice", "", "2025-06-02T09:00"));
        stub.deleteAllPrescriptions();
        assertTrue(stub.getAllPrescriptions().isEmpty());
    }

    @Test
public void testGetPrescriptionsForPatientPositive() {
    Prescription p1 = new Prescription(0, "doc1", "Patient A", "Ibuprofen", "200mg", "200mg", "Once", "", "2025-06-01T10:00");
    Prescription p2 = new Prescription(0, "doc2", "Patient B", "Amoxicillin", "500mg", "500mg", "Twice", "", "2025-06-02T09:00");
    stub.addPrescription(p1);
    stub.addPrescription(p2);

    List<Prescription> result = stub.getPrescriptionsForPatient("Patient A");
    assertEquals(1, result.size());
    assertEquals("Ibuprofen", result.get(0).getMedicationName());
}

@Test
public void testGetPrescriptionsForPatientCaseInsensitive() {
    Prescription p = new Prescription(0, "doc1", "Patient A", "Ibuprofen", "200mg", "200mg", "Once", "", "2025-06-01T10:00");
    stub.addPrescription(p);

    List<Prescription> result = stub.getPrescriptionsForPatient("patient a");
    assertEquals(1, result.size());
}

@Test
public void testGetPrescriptionsForPatientNotFound() {
    Prescription p = new Prescription(0, "doc1", "Patient A", "Ibuprofen", "200mg", "200mg", "Once", "", "2025-06-01T10:00");
    stub.addPrescription(p);

    List<Prescription> result = stub.getPrescriptionsForPatient("Nonexistent");
    assertTrue(result.isEmpty());
}
}