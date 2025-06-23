package physicianconnect.objects;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TimeSlot {
    private final LocalDateTime start;   // e.g. 2025-06-01T09:00
    private final LocalDateTime end;     // e.g. 2025-06-01T09:30
    private boolean booked;              // true = this slot is occupied
    private String patientName;          // optional, for tooltip

    public TimeSlot(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
        this.booked = false;
        this.patientName = null;
    }

    // getters and setters
    public LocalDateTime getStart() { return start; }
    public LocalDateTime getEnd()   { return end; }
    public boolean isBooked()       { return booked; }
    public void setBooked(boolean booked) { this.booked = booked; }
    public String getPatientName()       { return patientName; }
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public static List<TimeSlot> generateDailySlots(LocalDate date) {
        List<TimeSlot> slots = new ArrayList<>();
        LocalDateTime current = date.atTime(8, 0);
        LocalDateTime endOfDay = date.atTime(17, 0);
        while (current.isBefore(endOfDay)) {
            TimeSlot ts = new TimeSlot(current, current.plusMinutes(30));
            slots.add(ts);
            current = current.plusMinutes(30);
        }
        return slots;
    }
}