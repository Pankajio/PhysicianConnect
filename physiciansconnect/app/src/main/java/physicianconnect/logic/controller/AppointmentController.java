package physicianconnect.logic.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;
import java.util.ArrayList;

import physicianconnect.logic.exceptions.InvalidAppointmentException;
import physicianconnect.logic.manager.AppointmentManager;
import physicianconnect.objects.Appointment;
import physicianconnect.presentation.physician.PhysicianApp;

/**
 * Controller for Appointment use‐cases.
 * Delegates persistence/validation to AppointmentManager.
 */
public class AppointmentController {
    private final AppointmentManager appointmentManager;
    private final List<Consumer<Appointment>> onAppointmentCreatedCallbacks = new ArrayList<>();
    private final List<Consumer<Appointment>> onAppointmentUpdatedCallbacks = new ArrayList<>();
    private final List<Consumer<Appointment>> onAppointmentDeletedCallbacks = new ArrayList<>();

    public AppointmentController(AppointmentManager appointmentManager) {
        this.appointmentManager = appointmentManager;
    }

    public void setOnAppointmentCreated(Consumer<Appointment> callback) {
        this.onAppointmentCreatedCallbacks.add(callback);
    }

    public void setOnAppointmentUpdated(Consumer<Appointment> callback) {
        this.onAppointmentUpdatedCallbacks.add(callback);
    }

    public void setOnAppointmentDeleted(Consumer<Appointment> callback) {
        this.onAppointmentDeletedCallbacks.add(callback);
    }

    private void notifyAppointmentCreated(Appointment appointment) {
        for (Consumer<Appointment> callback : onAppointmentCreatedCallbacks) {
            try {
                callback.accept(appointment);
            } catch (Exception e) {
                // Log error but continue notifying other callbacks
                e.printStackTrace();
            }
        }
    }

    private void notifyAppointmentUpdated(Appointment appointment) {
        for (Consumer<Appointment> callback : onAppointmentUpdatedCallbacks) {
            try {
                callback.accept(appointment);
            } catch (Exception e) {
                // Log error but continue notifying other callbacks
                e.printStackTrace();
            }
        }
    }

    private void notifyAppointmentDeleted(Appointment appointment) {
        for (Consumer<Appointment> callback : onAppointmentDeletedCallbacks) {
            try {
                callback.accept(appointment);
            } catch (Exception e) {
                // Log error but continue notifying other callbacks
                e.printStackTrace();
            }
        }
    }

    /**
     * Create and persist a new appointment.
     *
     * @param physicianId the ID of the physician
     * @param patientName the name of the patient
     * @param dateTime    the desired appointment date & time
     * @param notes       any optional notes (may be null or empty)
     * @throws InvalidAppointmentException if validation or slot‐conflict occurs
     */
    public void createAppointment(
            String physicianId,
            String patientName,
            LocalDateTime dateTime,
            String notes
    ) throws InvalidAppointmentException {
        // Build a new Appointment object
        Appointment appt = new Appointment(physicianId, patientName, dateTime);
        if (notes != null && !notes.trim().isEmpty()) {
            appt.setNotes(notes.trim());
        }
        // Delegate to manager, which will validate + persist
        appointmentManager.addAppointment(appt);
        notifyAppointmentCreated(appt);
    }

    /**
     * Update the notes on an existing appointment.
     *
     * @param appt     the Appointment object to modify
     * @param newNotes the new notes string
     * @throws InvalidAppointmentException if manager's validation fails
     */
    public void updateAppointmentNotes(
            Appointment appt,
            String newNotes
    ) throws InvalidAppointmentException {
        // Update the in-memory object
        appt.setNotes(newNotes == null ? "" : newNotes.trim());
        // Delegate to manager, which validates slot availability and persists
        appointmentManager.updateAppointment(appt);
        notifyAppointmentUpdated(appt);
    }

    /**
     * Delete an existing appointment.
     *
     * @param appt the Appointment to remove
     */
    public void deleteAppointment(Appointment appt) {
        appointmentManager.deleteAppointment(appt);
        notifyAppointmentDeleted(appt);
    }

    /**
     * Fetch all appointments for a given physician.
     *
     * @param physicianId the physician's ID
     * @return an unmodifiable List of Appointment objects
     */
    public List<Appointment> getAppointmentsForPhysician(String physicianId) {
        return appointmentManager.getAppointmentsForPhysician(physicianId);
    }

    public List<Appointment> getAllAppointments() {
    return appointmentManager.getAllAppointments();
}

    /**
     * Update the date and time of an existing appointment.
     *
     * @param appt     the Appointment object to modify
     * @param newDateTime the new date and time
     * @throws InvalidAppointmentException if manager's validation fails
     */
    public void updateAppointmentDateTime(
            Appointment appt,
            LocalDateTime newDateTime
    ) throws InvalidAppointmentException {
        // Create a new appointment with the updated date/time but preserve the ID
        Appointment updatedAppt = new Appointment(
            appt.getId(),
            appt.getPhysicianId(),
            appt.getPatientName(),
            newDateTime,
            appt.getNotes()
        );
        // Delegate to manager, which validates slot availability and persists
        appointmentManager.updateAppointment(updatedAppt);
        notifyAppointmentUpdated(updatedAppt);
    }
}
