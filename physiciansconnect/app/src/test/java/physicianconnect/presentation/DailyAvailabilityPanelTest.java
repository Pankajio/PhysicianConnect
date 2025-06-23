package physicianconnect.presentation;

import org.junit.jupiter.api.*;
import org.mockito.*;
import physicianconnect.logic.AvailabilityService;
import physicianconnect.logic.controller.AppointmentController;
import physicianconnect.objects.Appointment;
import physicianconnect.objects.TimeSlot;
import physicianconnect.presentation.util.TestUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class DailyAvailabilityPanelTest {

    @Mock
    AvailabilityService mockAvailabilityService;
    @Mock
    AppointmentController mockAppointmentController;

    JFrame frame;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        frame = new JFrame();
    }

    @AfterEach
    void tearDown() {
        frame.dispose();
    }

    @Test
    void testPanelLoadsSlotsFromService() throws Exception {
        LocalDate date = LocalDate.of(2025, 6, 10);
        List<TimeSlot> slots = TimeSlot.generateDailySlots(date);
        when(mockAvailabilityService.getDailyAvailability(anyString(), eq(date))).thenReturn(slots);

        DailyAvailabilityPanel panel = new DailyAvailabilityPanel(
                "doc1", mockAvailabilityService, mockAppointmentController, date, () -> {
                });

        assertEquals(date, panel.getCurrentDate());
        // Should have loaded slots from service
        verify(mockAvailabilityService).getDailyAvailability("doc1", date);
    }

    @Test
    void testPanelFallsBackToAllFreeSlotsOnException() throws Exception {
        LocalDate date = LocalDate.of(2025, 6, 10);
        when(mockAvailabilityService.getDailyAvailability(anyString(), eq(date)))
                .thenThrow(new RuntimeException("DB error"));

        DailyAvailabilityPanel panel = new DailyAvailabilityPanel(
                "doc1", mockAvailabilityService, mockAppointmentController, date, () -> {
                });

        // Should fallback to all-free slots
        assertEquals(date, panel.getCurrentDate());
        // All slots should be free
        List<TimeSlot> slots = (List<TimeSlot>) TestUtils.getField(panel, "currentSlots");
        assertTrue(slots.stream().noneMatch(TimeSlot::isBooked));
    }

//    @Test
//    void testClickOnFreeSlotShowsAddAppointmentDialog() throws Exception {
//        LocalDate date = LocalDate.of(2025, 6, 10);
//        List<TimeSlot> slots = TimeSlot.generateDailySlots(date);
//        when(mockAvailabilityService.getDailyAvailability(anyString(), eq(date))).thenReturn(slots);
//
//        AtomicBoolean callbackCalled = new AtomicBoolean(false);
//
//        DailyAvailabilityPanel panel = new DailyAvailabilityPanel(
//                "doc1", mockAvailabilityService, mockAppointmentController, date, () -> callbackCalled.set(true));
//
//        // Mock JOptionPane to always return YES_OPTION
//        try (MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {
//            mockedPane.when(() -> JOptionPane.showConfirmDialog(any(), any(), any(), anyInt()))
//                    .thenReturn(JOptionPane.YES_OPTION);
//
//            // Mock AddAppointmentPanel to simulate dialog and initialize spinners
//            try (MockedConstruction<AddAppointmentPanel> mockAddDlg = mockConstruction(AddAppointmentPanel.class,
//                    (mock, context) -> {
//                        mock.dateSpinner = new JSpinner(new SpinnerDateModel());
//                        when(mock.isVisible()).thenReturn(true);
//                    })) {
//
//                // Simulate mouse click on the first slot (free)
//                int x = 100; // In slot column
//                int y = 0; // First slot
//                MouseEvent evt = new MouseEvent(panel, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, x, y, 1,
//                        false);
//
//                // Should show confirm dialog and open AddAppointmentPanel
//                mockedPane.verify(() -> JOptionPane.showConfirmDialog(any(), contains("2025-06-10"), any(), anyInt()));
//                assertEquals(1, mockAddDlg.constructed().size());
//            }
//        }
//    }

    @Test
    void testClickOnBookedSlotShowsViewAppointmentDialog() throws Exception {
        LocalDate date = LocalDate.of(2025, 6, 10);
        List<TimeSlot> slots = TimeSlot.generateDailySlots(date);
        // Book the second slot
        TimeSlot orig = slots.get(1);
        TimeSlot booked = new TimeSlot(orig.getStart(), orig.getEnd());
        booked.setBooked(true);
        booked.setPatientName("Bob");
        slots.set(1, booked);

        when(mockAvailabilityService.getDailyAvailability(anyString(), eq(date))).thenReturn(slots);

        Appointment appt = mock(Appointment.class);
        when(appt.getDateTime()).thenReturn(booked.getStart());
        when(mockAppointmentController.getAppointmentsForPhysician(anyString()))
                .thenReturn(List.of(appt));

        DailyAvailabilityPanel panel = new DailyAvailabilityPanel(
                "doc1", mockAvailabilityService, mockAppointmentController, date, () -> {
                });

        // Mock ViewAppointmentPanel to simulate dialog
        try (MockedConstruction<ViewAppointmentPanel> mockViewDlg = mockConstruction(ViewAppointmentPanel.class,
                (mock, context) -> when(mock.isVisible()).thenReturn(true))) {

            // Simulate mouse click on the second slot (booked)
            int x = 100; // In slot column
            int y = 1 * 30; // Second slot (PIXEL_PER_SLOT = 30)
            MouseEvent evt = new MouseEvent(panel, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, x, y, 1,
                    false);
            for (var l : panel.getMouseListeners())
                l.mouseClicked(evt);

            // Should open ViewAppointmentPanel
            assertEquals(1, mockViewDlg.constructed().size());
        }
    }

    @Test
    void testClickOnBookedSlotNotFoundShowsErrorDialog() throws Exception {
        LocalDate date = LocalDate.of(2025, 6, 10);
        List<TimeSlot> slots = TimeSlot.generateDailySlots(date);
        // Book the third slot
        TimeSlot orig = slots.get(2);
        TimeSlot booked = new TimeSlot(orig.getStart(), orig.getEnd());
        booked.setBooked(true);
        booked.setPatientName("Bob");
        slots.set(2, booked);

        when(mockAvailabilityService.getDailyAvailability(anyString(), eq(date))).thenReturn(slots);
        // Controller returns no matching appointment
        when(mockAppointmentController.getAppointmentsForPhysician(anyString()))
                .thenReturn(List.of());

        DailyAvailabilityPanel panel = new DailyAvailabilityPanel(
                "doc1", mockAvailabilityService, mockAppointmentController, date, () -> {
                });

        // Mock JOptionPane to verify error dialog
        try (MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {
            int x = 100;
            int y = 2 * 30; // Third slot
            MouseEvent evt = new MouseEvent(panel, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, x, y, 1,
                    false);
            for (var l : panel.getMouseListeners())
                l.mouseClicked(evt);

            mockedPane.verify(() -> JOptionPane.showMessageDialog(
                    any(),
                    contains("could not find appointment"),
                    any(),
                    eq(JOptionPane.ERROR_MESSAGE)));
        }
    }

    @Test
    void testClickOnTimeLabelColumnDoesNothing() throws Exception {
        LocalDate date = LocalDate.of(2025, 6, 10);
        List<TimeSlot> slots = TimeSlot.generateDailySlots(date);
        when(mockAvailabilityService.getDailyAvailability(anyString(), eq(date))).thenReturn(slots);

        DailyAvailabilityPanel panel = new DailyAvailabilityPanel(
                "doc1", mockAvailabilityService, mockAppointmentController, date, () -> {
                });

        // Click in the time label column (should do nothing)
        int x = 10; // In time label column (TIME_LABEL_WIDTH = 80)
        int y = 0;
        MouseEvent evt = new MouseEvent(panel, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, x, y, 1, false);
        for (var l : panel.getMouseListeners())
            l.mouseClicked(evt);

        // No dialogs or panels should be opened, nothing to assert (no exception =
        // pass)
    }
}