package physicianconnect.logic.controller;

import org.junit.jupiter.api.*;
import org.mockito.*;
import physicianconnect.logic.exceptions.InvalidAppointmentException;
import physicianconnect.logic.manager.AppointmentManager;
import physicianconnect.objects.Appointment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppointmentControllerTest {

    @Mock
    private AppointmentManager mockManager;

    private AppointmentController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new AppointmentController(mockManager);
    }

    @Test
    void testCreateAppointmentDelegatesAndNotifies() throws Exception {
        Appointment appt = new Appointment("doc1", "Alice", LocalDateTime.now().plusDays(1));
        AtomicBoolean notified = new AtomicBoolean(false);
        controller.setOnAppointmentCreated(a -> notified.set(true));
        doNothing().when(mockManager).addAppointment(any());

        controller.createAppointment("doc1", "Alice", appt.getDateTime(), "note");

        verify(mockManager).addAppointment(any());
        assertTrue(notified.get());
    }

    @Test
    void testUpdateAppointmentNotesDelegatesAndNotifies() throws Exception {
        Appointment appt = new Appointment("doc1", "Bob", LocalDateTime.now().plusDays(2));
        AtomicBoolean notified = new AtomicBoolean(false);
        controller.setOnAppointmentUpdated(a -> notified.set(true));
        doNothing().when(mockManager).updateAppointment(any());

        controller.updateAppointmentNotes(appt, "new notes");

        verify(mockManager).updateAppointment(appt);
        assertEquals("new notes", appt.getNotes());
        assertTrue(notified.get());
    }

    @Test
    void testDeleteAppointmentDelegatesAndNotifies() {
        Appointment appt = new Appointment("doc1", "Carol", LocalDateTime.now().plusDays(3));
        AtomicBoolean notified = new AtomicBoolean(false);
        controller.setOnAppointmentDeleted(a -> notified.set(true));
        doNothing().when(mockManager).deleteAppointment(any());

        controller.deleteAppointment(appt);

        verify(mockManager).deleteAppointment(appt);
        assertTrue(notified.get());
    }

    @Test
    void testGetAppointmentsForPhysician() {
        Appointment appt = new Appointment("doc1", "Alice", LocalDateTime.now().plusDays(1));
        when(mockManager.getAppointmentsForPhysician("doc1")).thenReturn(List.of(appt));
        List<Appointment> result = controller.getAppointmentsForPhysician("doc1");
        assertEquals(1, result.size());
        assertEquals("Alice", result.get(0).getPatientName());
    }

    @Test
    void testGetAllAppointments() {
        Appointment appt = new Appointment("doc1", "Alice", LocalDateTime.now().plusDays(1));
        when(mockManager.getAllAppointments()).thenReturn(List.of(appt));
        List<Appointment> result = controller.getAllAppointments();
        assertEquals(1, result.size());
    }
}