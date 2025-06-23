package physicianconnect.logic;

import org.junit.jupiter.api.*;
import org.mockito.*;
import physicianconnect.objects.Appointment;
import physicianconnect.objects.TimeSlot;
import physicianconnect.persistence.sqlite.AppointmentDB;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AvailabilityServiceTest {

    @Mock
    private AppointmentDB appointmentDb;

    private AvailabilityService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new AvailabilityService(appointmentDb);
    }

    @Test
    void testGetDailyAvailabilityMarksBookedSlots() throws Exception {
        LocalDate date = LocalDate.of(2025, 6, 10);
        LocalDateTime apptTime = date.atTime(9, 0);
        Appointment appt = new Appointment("doc1", "Alice", apptTime);

        when(appointmentDb.getAppointmentsForPhysicianInRange(
                eq("doc1"),
                eq(date.atTime(8, 0)),
                eq(date.atTime(17, 0))
        )).thenReturn(List.of(appt));

        List<TimeSlot> slots = service.getDailyAvailability("doc1", date);

        assertTrue(slots.stream().anyMatch(ts -> ts.getStart().equals(apptTime) && ts.isBooked()));
        assertTrue(slots.stream().anyMatch(ts -> !ts.isBooked()));
    }

    @Test
    void testGetWeeklyAvailabilityReturnsSevenDays() throws Exception {
        LocalDate weekStart = LocalDate.of(2025, 6, 9);
        when(appointmentDb.getAppointmentsForPhysicianInRange(any(), any(), any()))
                .thenReturn(List.of());

        Map<LocalDate, List<TimeSlot>> week = service.getWeeklyAvailability("doc1", weekStart);

        assertEquals(7, week.size());
        for (int i = 0; i < 7; i++) {
            assertNotNull(week.get(weekStart.plusDays(i)));
        }
    }
}