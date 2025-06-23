package physicianconnect.logic.validation;

import physicianconnect.objects.Appointment;
import physicianconnect.logic.exceptions.*;
import java.time.Clock;
import java.time.LocalDateTime;

public class AppointmentValidator {

    /**
     * Production‐friendly entry point: uses the system default zone clock.
     */
    public static void validate(Appointment appointment) {
        validate(appointment, Clock.systemDefaultZone());
    }

    /**
     * Core validator: now compares appointment.getDateTime() against LocalDateTime.now(clock).
     * Tests will pass in a Clock.fixed(...) so that “now” is deterministic.
     */
    public static void validate(Appointment appointment, Clock clock) {
        if (appointment == null) {
            throw new InvalidAppointmentException("Appointment cannot be null.");
        }

        if (appointment.getPhysicianId() == null || appointment.getPhysicianId().isBlank()) {
            throw new InvalidAppointmentException("Physician ID is required.");
        }

        if (appointment.getPatientName() == null || appointment.getPatientName().isBlank()) {
            throw new InvalidAppointmentException("Patient name is required.");
        }

        if (appointment.getDateTime() == null) {
            throw new InvalidAppointmentException("Appointment date and time is required.");
        }

        // ← here is where “now” comes from the injected clock
        LocalDateTime now = LocalDateTime.now(clock);
        if (appointment.getDateTime().isBefore(now)) {
            throw new InvalidAppointmentException(
                    "Appointment date and time cannot be in the past (now == " + now + ")."
            );
        }
    }
}
