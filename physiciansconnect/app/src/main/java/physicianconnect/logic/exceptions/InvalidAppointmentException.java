package physicianconnect.logic.exceptions;

/** Thrown when attempting to add or modify an appointment with invalid data. */
public class InvalidAppointmentException extends RuntimeException {
    public InvalidAppointmentException(String message) {
        super(message);
    }
}
