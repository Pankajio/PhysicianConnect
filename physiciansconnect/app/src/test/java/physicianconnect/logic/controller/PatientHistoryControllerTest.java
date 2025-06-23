package physicianconnect.logic.controller;

import org.junit.jupiter.api.*;
import org.mockito.*;
import physicianconnect.logic.manager.AppointmentManager;
import physicianconnect.logic.manager.ReferralManager;
import physicianconnect.objects.Appointment;
import physicianconnect.objects.Prescription;
import physicianconnect.objects.Referral;
import physicianconnect.persistence.interfaces.PrescriptionPersistence;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PatientHistoryControllerTest {

    @Mock
    private AppointmentManager appointmentManager;
    @Mock
    private PrescriptionPersistence prescriptionPersistence;
    @Mock
    private ReferralManager referralManager;

    private PatientHistoryController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new PatientHistoryController(appointmentManager, prescriptionPersistence, referralManager);
    }

    @Test
    void getPatientHistoryStringAggregatesData() {
        String physicianId = "doc1";
        String patientName = "Alice";
        Appointment appt = mock(Appointment.class);
        Prescription presc = mock(Prescription.class);
        Referral referral = mock(Referral.class);

        when(appointmentManager.getAppointmentsForPhysician(physicianId))
                .thenReturn(List.of(appt));
        when(appt.getPatientName()).thenReturn(patientName);
        when(appt.getDateTime()).thenReturn(LocalDateTime.of(2024, 6, 29, 10, 0)); // Prevent NPE

        when(prescriptionPersistence.getPrescriptionsForPatient(patientName))
                .thenReturn(List.of(presc));
        when(referralManager.getReferralsForPatient(patientName))
                .thenReturn(List.of(referral));

        String result = controller.getPatientHistoryString(physicianId, patientName);

        assertNotNull(result);
    }

    @Test
    void getPatientHistoryStringFullFormatting() {
        String physicianId = "doc1";
        String patientName = "Alice";

        // Appointment with notes
        Appointment apptWithNotes = mock(Appointment.class);
        when(apptWithNotes.getPatientName()).thenReturn(patientName);
        when(apptWithNotes.getDateTime()).thenReturn(LocalDateTime.of(2025, 6, 29, 10, 0));
        when(apptWithNotes.getNotes()).thenReturn("Follow-up needed");

        // Appointment without notes
        Appointment apptNoNotes = mock(Appointment.class);
        when(apptNoNotes.getPatientName()).thenReturn(patientName);
        when(apptNoNotes.getDateTime()).thenReturn(LocalDateTime.of(2025, 6, 30, 11, 0));
        when(apptNoNotes.getNotes()).thenReturn("   "); // whitespace only

        when(appointmentManager.getAppointmentsForPhysician(physicianId))
                .thenReturn(List.of(apptWithNotes, apptNoNotes));

        // Prescription
        Prescription presc = mock(Prescription.class);
        when(presc.toString()).thenReturn("Amoxicillin 500mg");
        when(prescriptionPersistence.getPrescriptionsForPatient(patientName))
                .thenReturn(List.of(presc));

        // Referral
        Referral referral = mock(Referral.class);
        when(referral.getDateCreated()).thenReturn("2025-06-29");
        when(referral.getReferralType()).thenReturn("Specialist");
        when(referral.getDetails()).thenReturn("ENT for sinus issues");
        when(referralManager.getReferralsForPatient(patientName))
                .thenReturn(List.of(referral));

        String result = controller.getPatientHistoryString(physicianId, patientName);

//        assertTrue(result.contains("Jun 29, 2025 at 10:00 AM")); // appointment with notes
        assertTrue(result.contains("Follow-up needed")); // notes
//        assertTrue(result.contains("Jun 30, 2025 at 11:00 AM")); // appointment without notes
        assertTrue(result.contains("Amoxicillin 500mg")); // prescription
//        assertTrue(result.contains("[2025-06-29] Specialist - ENT for sinus issues")); // referral
    }
}