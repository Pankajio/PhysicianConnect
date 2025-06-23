package physicianconnect.logic.validation;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import physicianconnect.logic.exceptions.InvalidAppointmentException;
import physicianconnect.objects.Appointment;

public class AppointmentValidatorTest {

    private static final Clock FIXED_CLOCK = Clock.fixed(
            Instant.parse("2025-06-10T12:00:00Z"), ZoneId.systemDefault());

    @Test
    public void testValidAppointmentPasses() {
        Appointment valid = new Appointment("doc-id", "Patient X", LocalDateTime.of(2025, 6, 10, 13, 0));
        assertDoesNotThrow(() -> AppointmentValidator.validate(valid, FIXED_CLOCK));
    }

    @Test
    public void testNullAppointmentThrows() {
        assertThrows(InvalidAppointmentException.class, () -> AppointmentValidator.validate(null, FIXED_CLOCK));
    }

    @Test
    public void testBlankPhysicianIdThrows() {
        Appointment a = new Appointment(" ", "Patient Y", LocalDateTime.of(2025, 6, 10, 13, 0));
        assertThrows(InvalidAppointmentException.class, () -> AppointmentValidator.validate(a, FIXED_CLOCK));
    }

    @Test
    public void testBlankPatientNameThrows() {
        Appointment a = new Appointment("doc-id", "   ", LocalDateTime.of(2025, 6, 10, 13, 0));
        assertThrows(InvalidAppointmentException.class, () -> AppointmentValidator.validate(a, FIXED_CLOCK));
    }

    @Test
    public void testNullDateTimeThrows() {
        Appointment a = new Appointment("doc-id", "Patient Z", null);
        assertThrows(InvalidAppointmentException.class, () -> AppointmentValidator.validate(a, FIXED_CLOCK));
    }

    @Test
    public void testPastDateTimeThrows() {
        Appointment a = new Appointment("doc-id", "Patient A", LocalDateTime.of(2025, 6, 9, 13, 0));
        assertThrows(InvalidAppointmentException.class, () -> AppointmentValidator.validate(a, FIXED_CLOCK));
    }
}