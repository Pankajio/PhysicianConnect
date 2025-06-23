package physicianconnect.persistence.stub;

import physicianconnect.objects.Appointment;
import physicianconnect.persistence.interfaces.AppointmentPersistence;

import java.time.LocalDateTime;
import java.util.*;

public class AppointmentPersistenceStub implements AppointmentPersistence {
    private final List<Appointment> appointments;

    public AppointmentPersistenceStub(boolean seed) {
        appointments = new ArrayList<>();
        if (seed) {
            appointments.add(new Appointment("1", "Alice Johnson", java.time.LocalDateTime.of(2025, 5, 30, 10, 0)));
            appointments.add(new Appointment("2", "Bob Brown", java.time.LocalDateTime.of(2025, 6, 1, 14, 30)));
        }
    }

    @Override
    public List<Appointment> getAppointmentsForPhysician(String physicianId) {
        List<Appointment> result = new ArrayList<>();
        for (Appointment appt : appointments) {
            if (appt.getPhysicianId().equals(physicianId)) {
                result.add(appt);
            }
        }
        return result;
    }

    @Override
    public void addAppointment(Appointment appointment) {
        appointments.add(appointment);
    }

    @Override
    public void updateAppointment(Appointment appointment) {
        for (int i = 0; i < appointments.size(); i++) {
            Appointment existing = appointments.get(i);
            if (existing.getPhysicianId().equals(appointment.getPhysicianId()) &&
                    existing.getPatientName().equals(appointment.getPatientName()) &&
                    existing.getDateTime().equals(appointment.getDateTime())) {
                appointments.set(i, appointment);
                break;
            }
        }
    }

    @Override
    public void deleteAppointment(Appointment appointment) {
        appointments.removeIf(a -> a.getPhysicianId().equals(appointment.getPhysicianId()) &&
                a.getPatientName().equals(appointment.getPatientName()) &&
                a.getDateTime().equals(appointment.getDateTime()));
    }

    @Override
    public void deleteAllAppointments() {
        appointments.clear();
    }

    @Override
    public List<Appointment> getAppointmentsForPhysicianInRange(
            String physicianId,
            LocalDateTime start,
            LocalDateTime end) {
        List<Appointment> result = new ArrayList<>();
        for (Appointment appt : appointments) {
            if (appt.getPhysicianId().equals(physicianId)) {
                LocalDateTime dt = appt.getDateTime();
                // include appts where start <= dt < end
                if (!dt.isBefore(start) && dt.isBefore(end)) {
                    result.add(appt);
                }
            }
        }
        return result;
    }

    @Override
    public List<Appointment> getAllAppointments() {
        return new ArrayList<>(appointments);
    }

    public void close() {
        appointments.clear();
    }
}
