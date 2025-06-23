package physicianconnect.logic.manager;

import org.junit.jupiter.api.*;
import org.mockito.*;
import physicianconnect.logic.exceptions.InvalidAppointmentException;
import physicianconnect.objects.Appointment;
import physicianconnect.persistence.interfaces.AppointmentPersistence;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppointmentManagerTest {

    @Mock
    private AppointmentPersistence mockPersistence;

    private AppointmentManager manager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        manager = new AppointmentManager(mockPersistence, Clock.systemDefaultZone());
    }

    @Test
    void testAddAppointmentDelegates() {
        Appointment appt = new Appointment("doc1", "Alice", LocalDateTime.now().plusDays(1));
        when(mockPersistence.getAppointmentsForPhysician("doc1")).thenReturn(List.of());
        manager.addAppointment(appt);
        verify(mockPersistence).addAppointment(appt);
    }

    @Test
    void testAddAppointmentThrowsIfSlotTaken() {
        LocalDateTime slot = LocalDateTime.now().plusDays(1);
        Appointment appt = new Appointment("doc1", "Alice", slot);
        when(mockPersistence.getAppointmentsForPhysician("doc1")).thenReturn(List.of(appt));
        assertThrows(InvalidAppointmentException.class, () -> manager.addAppointment(appt));
    }

    @Test
    void testUpdateAppointmentDelegates() {
        LocalDateTime slot = LocalDateTime.now().plusDays(2);
        Appointment appt = new Appointment("doc1", "Bob", slot);
        when(mockPersistence.getAppointmentsForPhysician("doc1")).thenReturn(List.of(appt));
        manager.updateAppointment(appt);
        verify(mockPersistence).updateAppointment(appt);
    }

    @Test
    void testUpdateAppointmentThrowsIfSlotTaken() {
        LocalDateTime slot = LocalDateTime.now().plusDays(2);
        Appointment appt = new Appointment("doc1", "Bob", slot);
        Appointment other = new Appointment("doc1", "Other", slot);
        when(mockPersistence.getAppointmentsForPhysician("doc1")).thenReturn(List.of(other));
        assertThrows(InvalidAppointmentException.class, () -> manager.updateAppointment(appt));
    }

    @Test
    void testDeleteAppointmentDelegates() {
        Appointment appt = new Appointment("doc1", "Alice", LocalDateTime.now().plusDays(1));
        manager.deleteAppointment(appt);
        verify(mockPersistence).deleteAppointment(appt);
    }

    @Test
    void testGetAppointmentsForPhysician() {
        Appointment appt = new Appointment("doc1", "Alice", LocalDateTime.now().plusDays(1));
        when(mockPersistence.getAppointmentsForPhysician("doc1")).thenReturn(List.of(appt));
        List<Appointment> result = manager.getAppointmentsForPhysician("doc1");
        assertEquals(1, result.size());
        assertEquals("Alice", result.get(0).getPatientName());
    }

    @Test
    void testDeleteAllDelegates() {
        manager.deleteAll();
        verify(mockPersistence).deleteAllAppointments();
    }

    @Test
    void testIsSlotAvailable() {
        LocalDateTime slot = LocalDateTime.now().plusDays(1);
        Appointment appt = new Appointment("doc1", "Alice", slot);
        when(mockPersistence.getAppointmentsForPhysician("doc1")).thenReturn(List.of(appt));
        assertFalse(manager.isSlotAvailable("doc1", slot));
        assertTrue(manager.isSlotAvailable("doc1", slot.plusHours(1)));
    }

    @Test
void testIsSlotAvailableForUpdateSameRecordSkips() {
    LocalDateTime slot = LocalDateTime.now().plusDays(1);
    Appointment appt = new Appointment("doc1", "Alice", slot);
    // List contains the same appointment (should skip and return true)
    when(mockPersistence.getAppointmentsForPhysician("doc1")).thenReturn(List.of(appt));
    assertTrue(manager.isSlotAvailableForUpdate("doc1", slot, appt));
}

@Test
void testIsSlotAvailableForUpdateSlotTaken() {
    LocalDateTime slot = LocalDateTime.now().plusDays(1);
    Appointment appt = new Appointment("doc1", "Alice", slot.plusHours(1));
    Appointment other = new Appointment("doc1", "Other", slot);
    when(mockPersistence.getAppointmentsForPhysician("doc1")).thenReturn(List.of(other));
    // Should return false because slot is taken by "other"
    assertFalse(manager.isSlotAvailableForUpdate("doc1", slot, appt));
}

@Test
void testGetAllAppointmentsDelegates() {
    Appointment appt = new Appointment("doc1", "Alice", LocalDateTime.now().plusDays(1));
    when(mockPersistence.getAllAppointments()).thenReturn(List.of(appt));
    List<Appointment> result = manager.getAllAppointments();
    assertEquals(1, result.size());
    assertEquals("Alice", result.get(0).getPatientName());
}

@Test
void testObserverPatternAddRemoveNotify() {
    Runnable listener = mock(Runnable.class);
    // Add listener
    manager.addChangeListener(listener);
    // Trigger notifyListeners via addAppointment
    Appointment appt = new Appointment("doc1", "Alice", LocalDateTime.now().plusDays(2));
    when(mockPersistence.getAppointmentsForPhysician("doc1")).thenReturn(List.of());
    manager.addAppointment(appt);
    verify(listener, atLeastOnce()).run();

    // Remove listener and verify it is not called again
    reset(listener);
    manager.removeChangeListener(listener);
    manager.deleteAll();
    verify(listener, never()).run();
}
}