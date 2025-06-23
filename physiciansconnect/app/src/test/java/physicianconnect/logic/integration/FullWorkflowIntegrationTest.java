package physicianconnect.logic.integration;

import org.junit.jupiter.api.*;

import physicianconnect.logic.manager.AppointmentManager;
import physicianconnect.logic.manager.PhysicianManager;
import physicianconnect.logic.manager.ReferralManager;
import physicianconnect.objects.*;
import physicianconnect.persistence.*;
import physicianconnect.persistence.interfaces.AppointmentPersistence;
import physicianconnect.persistence.interfaces.MedicationPersistence;
import physicianconnect.persistence.interfaces.PhysicianPersistence;
import physicianconnect.persistence.interfaces.PrescriptionPersistence;
import physicianconnect.persistence.interfaces.ReferralPersistence;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class FullWorkflowIntegrationTest {


    private PhysicianManager physicianManager;
    private AppointmentManager appointmentManager;
    private ReferralManager referralManager;

    private MedicationPersistence medicationPersistence;
    private PrescriptionPersistence prescriptionPersistence;
    private ReferralPersistence referralPersistence;
    private AppointmentPersistence appointmentPersistence;
    private PhysicianPersistence physicianPersistence;

    @BeforeEach
    public void setup() {
        PersistenceFactory.initialize(PersistenceType.TEST, true); // Seed DB

        physicianPersistence = PersistenceFactory.getPhysicianPersistence();
        appointmentPersistence = PersistenceFactory.getAppointmentPersistence();
        medicationPersistence = PersistenceFactory.getMedicationPersistence();
        prescriptionPersistence = PersistenceFactory.getPrescriptionPersistence();
        referralPersistence = PersistenceFactory.getReferralPersistence();

        // Delete all data before each test
        prescriptionPersistence.deleteAllPrescriptions();
        medicationPersistence.deleteAllMedications();
        referralPersistence.deleteAllReferrals();
        appointmentPersistence.deleteAllAppointments();
        physicianPersistence.deleteAllPhysicians();

        physicianManager = new PhysicianManager(physicianPersistence);
        appointmentManager = new AppointmentManager(appointmentPersistence);
        referralManager = new ReferralManager(referralPersistence);
    }

    @AfterEach
    public void teardown() {
        PersistenceFactory.reset();
    }

    @Test
    public void testPhysicianCanSchedulePrescribeAndRefer() {
        // Add physician
        Physician doc = new Physician("p1", "Dr. House", "house@hospital.com", "vicodin");
        physicianManager.addPhysician(doc);

        medicationPersistence.deleteAllMedications();
        // Add medication
        Medication med = new Medication("Vicodin", "10mg", "Pain relief", "Take with water");
        medicationPersistence.addMedication(med);

        // Schedule appointment
        Appointment appt = new Appointment("p1", "Gregory", LocalDateTime.of(2025, 7, 1, 14, 0));
        appointmentManager.addAppointment(appt);

        // Prescribe medication
        Prescription pres = new Prescription(0, "p1", "Gregory", "Vicodin", "10mg", "10mg", "Once", "Take with water",
                "2025-07-01T14:00");
        prescriptionPersistence.addPrescription(pres);

        // Add referral
        referralPersistence
                .addReferral(new Referral(0, "p1", "Gregory", "Specialist", "See cardiologist", "2025-07-01"));

        // Assert all data is present
        assertNotNull(physicianManager.getPhysicianById("p1"));
        assertFalse(appointmentManager.getAppointmentsForPhysician("p1").isEmpty());
        assertFalse(medicationPersistence.getAllMedications().isEmpty());
        assertFalse(prescriptionPersistence.getPrescriptionsForPatient("Gregory").isEmpty());
        assertFalse(referralPersistence.getReferralsForPatient("Gregory").isEmpty());
    }
}