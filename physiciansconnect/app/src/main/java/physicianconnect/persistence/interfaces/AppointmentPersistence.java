package physicianconnect.persistence.interfaces;

import physicianconnect.objects.Appointment;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import java.util.List;

public interface AppointmentPersistence {
    List<Appointment> getAppointmentsForPhysician(String physicianId);

    void addAppointment(Appointment appointment);

    void updateAppointment(Appointment appointment);

    void deleteAppointment(Appointment appointment);

    void deleteAllAppointments();

    List<Appointment> getAllAppointments();

    /**
     * Fetch all appointments for a given physician whose datetime is ≥ start AND < end.
     */
    List<Appointment> getAppointmentsForPhysicianInRange(
            String physicianId,
            LocalDateTime start,
            LocalDateTime end
    );
}
