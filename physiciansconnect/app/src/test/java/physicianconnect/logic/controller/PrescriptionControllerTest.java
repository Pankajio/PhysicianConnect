package physicianconnect.logic.controller;

import org.junit.jupiter.api.*;
import org.mockito.*;
import physicianconnect.logic.exceptions.InvalidPrescriptionException;
import physicianconnect.logic.validation.PrescriptionValidator;
import physicianconnect.objects.Prescription;
import physicianconnect.persistence.interfaces.PrescriptionPersistence;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PrescriptionControllerTest {

    @Mock
    private PrescriptionPersistence prescriptionPersistence;

    private PrescriptionController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new PrescriptionController(prescriptionPersistence);
    }

    @Test
    void testCreatePrescriptionDelegates() throws Exception {
        doNothing().when(prescriptionPersistence).addPrescription(any());
        controller.createPrescription("doc1", "Alice", "Med", "10mg", "10mg", "daily", "notes");
        verify(prescriptionPersistence).addPrescription(any());
    }

    @Test
    void testCreatePrescriptionInvalidThrows() {
        assertThrows(InvalidPrescriptionException.class, () ->
                controller.createPrescription("doc1", "Alice", "", "10mg", "10mg", "daily", "notes"));
    }
}