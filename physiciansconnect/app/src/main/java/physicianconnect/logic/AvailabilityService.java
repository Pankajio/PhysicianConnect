package physicianconnect.logic;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

import physicianconnect.persistence.sqlite.AppointmentDB;
import physicianconnect.objects.Appointment;
import physicianconnect.objects.TimeSlot;

public class AvailabilityService {
    private final AppointmentDB appointmentDb;

    public AvailabilityService(AppointmentDB appointmentDb) {
        this.appointmentDb = appointmentDb;
    }

    public List<TimeSlot> getDailyAvailability(String physicianId, LocalDate date)
            throws SQLException
    {
        List<TimeSlot> slots = TimeSlot.generateDailySlots(date);

        LocalDateTime start = date.atTime(8, 0);
        LocalDateTime end   = date.atTime(17, 0);

        List<Appointment> appts = appointmentDb.getAppointmentsForPhysicianInRange(
                physicianId,
                start,
                end
        );

        for (Appointment a : appts) {
            LocalDateTime apptStart = a.getDateTime();
            for (TimeSlot ts : slots) {
                if (ts.getStart().equals(apptStart)) {
                    ts.setBooked(true);
                    ts.setPatientName(a.getPatientName());
                    break;
                }
            }
        }

        return slots;
    }

    public Map<LocalDate, List<TimeSlot>> getWeeklyAvailability(
            String physicianId,
            LocalDate weekStart
    ) throws SQLException
    {
        Map<LocalDate, List<TimeSlot>> weekMap = new LinkedHashMap<>();
        for (int i = 0; i < 7; i++) {
            LocalDate day = weekStart.plusDays(i);
            List<TimeSlot> daily = getDailyAvailability(physicianId, day);
            weekMap.put(day, daily);
        }
        return weekMap;
    }
}
