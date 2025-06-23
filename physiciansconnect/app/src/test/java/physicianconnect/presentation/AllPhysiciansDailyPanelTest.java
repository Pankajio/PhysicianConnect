package physicianconnect.presentation;

import org.junit.jupiter.api.*;
import org.mockito.*;
import physicianconnect.logic.controller.AppointmentController;
import physicianconnect.logic.manager.PhysicianManager;
import physicianconnect.logic.AvailabilityService;
import physicianconnect.objects.Physician;
import physicianconnect.presentation.util.TestUtils; // <-- Add this import

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AllPhysiciansDailyPanelTest {

    @Mock
    PhysicianManager physicianManager;
    @Mock
    AppointmentController appointmentController;
    @Mock
    AvailabilityService availabilityService;

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
    void testPanelConstructsAndDisplaysPhysicians() {
        Physician p1 = new Physician("doc1", "Dr. Alice", "alice@email.com", "pw");
        Physician p2 = new Physician("doc2", "Dr. Bob", "bob@email.com", "pw");
        when(physicianManager.getAllPhysicians()).thenReturn(List.of(p1, p2));

        AtomicReference<LocalDate> changedDate = new AtomicReference<>();
        AllPhysiciansDailyPanel panel = new AllPhysiciansDailyPanel(
                physicianManager, appointmentController, availabilityService,
                LocalDate.of(2025, 6, 10), changedDate::set);

        frame.add(panel);
        frame.pack();

        // Should display two panels (one per physician)
        JPanel panelsContainer = (JPanel) TestUtils.getField(panel, "panelsContainer");
        assertEquals(2, panelsContainer.getComponentCount());
        // Each panel should have a label with the physician's name
        boolean foundAlice = false, foundBob = false;
        for (Component comp : panelsContainer.getComponents()) {
            JPanel p = (JPanel) comp;
            JLabel label = (JLabel) ((JPanel) p).getComponent(0);
            if (label.getText().contains("Alice")) foundAlice = true;
            if (label.getText().contains("Bob")) foundBob = true;
        }
        assertTrue(foundAlice && foundBob);
    }

    @Test
    void testPrevAndNextDayButtonsCallCallback() {
        Physician p1 = new Physician("doc1", "Dr. Alice", "alice@email.com", "pw");
        when(physicianManager.getAllPhysicians()).thenReturn(List.of(p1));
        AtomicReference<LocalDate> changedDate = new AtomicReference<>();
        LocalDate date = LocalDate.of(2025, 6, 10);

        AllPhysiciansDailyPanel panel = new AllPhysiciansDailyPanel(
                physicianManager, appointmentController, availabilityService,
                date, changedDate::set);

        JButton prevBtn = TestUtils.getButton(panel, "← Prev Day");
        JButton nextBtn = TestUtils.getButton(panel, "Next Day →");

        assertNotNull(prevBtn);
        assertNotNull(nextBtn);

        prevBtn.doClick();
        assertEquals(date.minusDays(1), changedDate.get());

        nextBtn.doClick();
        assertEquals(date.plusDays(1), changedDate.get());
    }

    @Test
    void testSearchFiltersPhysicians() throws Exception {
        Physician p1 = new Physician("doc1", "Dr. Alice", "alice@email.com", "pw");
        Physician p2 = new Physician("doc2", "Dr. Bob", "bob@email.com", "pw");
        when(physicianManager.getAllPhysicians()).thenReturn(List.of(p1, p2));

        AllPhysiciansDailyPanel panel = new AllPhysiciansDailyPanel(
                physicianManager, appointmentController, availabilityService,
                LocalDate.of(2025, 6, 10), d -> {});

        JTextField searchField = (JTextField) TestUtils.getField(panel, "searchField");
        JPanel panelsContainer = (JPanel) TestUtils.getField(panel, "panelsContainer");

        // Search for "Alice"
        searchField.setText("Alice");
        // Simulate document event
        searchField.postActionEvent();
        // Only Alice should be shown
        assertEquals(1, panelsContainer.getComponentCount());
        JPanel p = (JPanel) panelsContainer.getComponent(0);
        JLabel label = (JLabel) p.getComponent(0);
        assertTrue(label.getText().contains("Alice"));

        // Search for "Bob"
        searchField.setText("Bob");
        searchField.postActionEvent();
        assertEquals(1, panelsContainer.getComponentCount());
        p = (JPanel) panelsContainer.getComponent(0);
        label = (JLabel) p.getComponent(0);
        assertTrue(label.getText().contains("Bob"));

        // Search for "xyz" (no match)
        searchField.setText("xyz");
        searchField.postActionEvent();
        assertEquals(0, panelsContainer.getComponentCount());
    }
}