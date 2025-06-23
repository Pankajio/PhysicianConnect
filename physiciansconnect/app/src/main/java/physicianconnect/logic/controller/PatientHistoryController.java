package physicianconnect.logic.controller;

import physicianconnect.persistence.interfaces.PrescriptionPersistence;
import physicianconnect.logic.manager.AppointmentManager;
import physicianconnect.logic.manager.ReferralManager;
import physicianconnect.objects.Appointment;
import physicianconnect.objects.Prescription;
import physicianconnect.objects.Referral;
import physicianconnect.presentation.config.UIConfig;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller to aggregate and format a patient’s history (appointments, prescriptions, referrals)
 * for a given physician.
 */
public class PatientHistoryController {

    private final AppointmentManager appointmentManager;
    private final PrescriptionPersistence prescriptionPersistence;
    private final ReferralManager referralManager;

    public PatientHistoryController(
            AppointmentManager appointmentManager,
            PrescriptionPersistence prescriptionPersistence,
            ReferralManager referralManager
    ) {
        this.appointmentManager      = appointmentManager;
        this.prescriptionPersistence = prescriptionPersistence;
        this.referralManager         = referralManager;
    }

    /**
     * Returns a single formatted string containing:
     *  • All appointments for the given physician & patient
     *  • All prescriptions for that patient
     *  • All referrals for that patient
     *
     * @param physicianId the ID of the physician
     * @param patientName the name of the patient
     * @return a multi‐line string to display in the UI
     */
    public String getPatientHistoryString(String physicianId, String patientName) {
        StringBuilder sb = new StringBuilder();

        // ─── Appointments Section ───
        sb.append(UIConfig.HISTORY_SECTION_APPOINTMENTS).append("\n");
        List<Appointment> appointments = appointmentManager
                .getAppointmentsForPhysician(physicianId)
                .stream()
                .filter(a -> a.getPatientName().equals(patientName))
                .collect(Collectors.toList());

        for (Appointment a : appointments) {
            sb.append("  ")
                    .append(a.getDateTime().format(UIConfig.HISTORY_DATE_FORMATTER))
                    .append("\n");
            if (a.getNotes() != null && !a.getNotes().trim().isEmpty()) {
                sb.append("    ")
                        .append(UIConfig.HISTORY_LABEL_NOTES)
                        .append(a.getNotes())
                        .append("\n");
            }
        }

        // ─── Prescriptions Section ───
        sb.append("\n").append(UIConfig.HISTORY_SECTION_PRESCRIPTIONS).append("\n");
        List<Prescription> prescriptions = prescriptionPersistence
                .getPrescriptionsForPatient(patientName);
        for (Prescription p : prescriptions) {
            sb.append("  ").append(p.toString()).append("\n");
        }

        // ─── Referrals Section ───
        sb.append("\n").append(UIConfig.HISTORY_SECTION_REFERRALS).append("\n");
        List<Referral> referrals = referralManager.getReferralsForPatient(patientName);
        for (Referral r : referrals) {
            sb.append("  [")
                    .append(r.getDateCreated())
                    .append("] ")
                    .append(r.getReferralType())
                    .append(" - ")
                    .append(r.getDetails())
                    .append("\n");
        }

        return sb.toString();
    }
}