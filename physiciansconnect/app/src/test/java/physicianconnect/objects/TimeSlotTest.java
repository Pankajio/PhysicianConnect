package physicianconnect.objects;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TimeSlotTest {

    @Test
    void testConstructorAndGettersSetters() {
        LocalDateTime start = LocalDateTime.of(2025, 6, 10, 9, 0);
        LocalDateTime end = LocalDateTime.of(2025, 6, 10, 9, 30);
        TimeSlot ts = new TimeSlot(start, end);
        assertEquals(start, ts.getStart());
        assertEquals(end, ts.getEnd());
        assertFalse(ts.isBooked());
        assertNull(ts.getPatientName());

        ts.setBooked(true);
        assertTrue(ts.isBooked());
        ts.setPatientName("Alice");
        assertEquals("Alice", ts.getPatientName());
    }

    @Test
    void testGenerateDailySlots() {
        LocalDate date = LocalDate.of(2025, 6, 10);
        List<TimeSlot> slots = TimeSlot.generateDailySlots(date);
        assertEquals(18, slots.size()); // 8:00 to 17:00 in 30-min increments
        assertEquals(date.atTime(8, 0), slots.get(0).getStart());
        assertEquals(date.atTime(16, 30), slots.get(17).getStart());
    }
}