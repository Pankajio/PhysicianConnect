package physicianconnect.presentation;

import org.junit.jupiter.api.*;
import org.mockito.*;
import physicianconnect.logic.controller.AppointmentController;
import physicianconnect.logic.exceptions.InvalidAppointmentException;
import physicianconnect.presentation.util.TestUtils;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AddAppointmentPanelTest {

    @Mock
    AppointmentController mockController;

    JFrame frame;

    @BeforeEach
    void setup() {
        frame = new JFrame();
    }

    @AfterEach
    void tearDown() {
        frame.dispose();
    }

    @Test
    void testFieldsAreEditableAndDefault() {
        AddAppointmentPanel panel = new AddAppointmentPanel(frame, mockController, "doc1") {
            {
                setModal(false);
            }
        };
        // Patient name field should be empty
        JTextField nameField = TestUtils.findTextField(panel, 0);
        assertNotNull(nameField);
        assertEquals("", nameField.getText());
        // Notes area should be empty
        JTextArea notesArea = TestUtils.findTextArea(panel, 0);
        assertNotNull(notesArea);
        assertEquals("", notesArea.getText());
    }

    @Test
    void testSaveAppointmentSuccess() throws Exception {
        Runnable callback = mock(Runnable.class);
        AddAppointmentPanel panel = new AddAppointmentPanel(frame, mockController, "doc1", callback) {
            {
                setModal(false);
            }
        };

        JTextField nameField = TestUtils.findTextField(panel, 0);
        nameField.setText("Test Patient");

        // Set date and time
        JSpinner dateSpinner = TestUtils.findSpinner(panel, 0);
        Calendar cal = Calendar.getInstance();
        cal.set(2025, Calendar.JUNE, 1, 9, 30, 0);
        Date date = cal.getTime();
        dateSpinner.setValue(date);

        JTextArea notesArea = TestUtils.findTextArea(panel, 0);
        notesArea.setText("Test notes");

        // Simulate save button click
        JButton saveBtn = TestUtils.getButton(panel, "Save");
        assertNotNull(saveBtn);
        TestUtils.pressOkOnAnyDialogAsync();
        SwingUtilities.invokeAndWait(saveBtn::doClick);

        TestUtils.pressOkOnAnyDialog();

        verify(mockController).createAppointment(eq("doc1"), eq("Test Patient"), any(LocalDateTime.class),
                eq("Test notes"));
        verify(callback).run();

    }

    @Test
    void testSaveAppointmentInvalidNameShowsDialog() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            AddAppointmentPanel panel = new AddAppointmentPanel(frame, mockController, "doc1") {
                {
                    setModal(false);
                }
            };
            JTextField nameField = TestUtils.findTextField(panel, 0);
            nameField.setText("   "); // Invalid

            try (MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {
                JButton saveBtn = TestUtils.getButton(panel, "Save");
                saveBtn.doClick();
                TestUtils.pressOkOnAnyDialog(); // <-- Auto-press OK after dialog appears
                mockedPane.verify(() -> JOptionPane.showMessageDialog(any(), eq("Please enter a patient name."), any(),
                        eq(JOptionPane.ERROR_MESSAGE)));
            }
            verify(mockController, never()).createAppointment(any(), any(), any(), any());
        });
    }

    @Test
    void testSaveAppointmentControllerThrowsInvalidAppointment() throws Exception {
        doThrow(new InvalidAppointmentException("bad date")).when(mockController)
                .createAppointment(any(), any(), any(), any());
        SwingUtilities.invokeAndWait(() -> {
            AddAppointmentPanel panel = new AddAppointmentPanel(frame, mockController, "doc1") {
                {
                    setModal(false);
                }
            };
            JTextField nameField = TestUtils.findTextField(panel, 0);
            nameField.setText("Test Patient");

            // Set date and time
            JSpinner dateSpinner = TestUtils.findSpinner(panel, 0);
            Date now = new Date();
            dateSpinner.setValue(now);

            JTextArea notesArea = TestUtils.findTextArea(panel, 0);
            notesArea.setText("Test notes");

            try (MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {
                JButton saveBtn = TestUtils.getButton(panel, "Save");
                saveBtn.doClick();
                TestUtils.pressOkOnAnyDialog(); // <-- Auto-press OK after dialog appears
                mockedPane.verify(() -> JOptionPane.showMessageDialog(any(), eq("bad date"), any(),
                        eq(JOptionPane.ERROR_MESSAGE)));
            }
        });
    }

    @Test
    void testSaveAppointmentControllerThrowsUnexpected() throws Exception {
        doThrow(new RuntimeException("boom")).when(mockController)
                .createAppointment(any(), any(), any(), any());
        SwingUtilities.invokeAndWait(() -> {
            AddAppointmentPanel panel = new AddAppointmentPanel(frame, mockController, "doc1") {
                {
                    setModal(false);
                }
            };
            JTextField nameField = TestUtils.findTextField(panel, 0);
            nameField.setText("Test Patient");

            // Set date and time
            JSpinner dateSpinner = TestUtils.findSpinner(panel, 0);
            Date now = new Date();
            dateSpinner.setValue(now);

            JTextArea notesArea = TestUtils.findTextArea(panel, 0);
            notesArea.setText("Test notes");

            try (MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {
                JButton saveBtn = TestUtils.getButton(panel, "Save");
                saveBtn.doClick();
                TestUtils.pressOkOnAnyDialog(); // <-- Auto-press OK after dialog appears
                mockedPane.verify(() -> JOptionPane.showMessageDialog(any(), contains("Unexpected error"), any(),
                        eq(JOptionPane.ERROR_MESSAGE)));
            }
        });
    }

    @Test
    void testCancelButtonDisposesDialog() throws Exception {
        AddAppointmentPanel panel = new AddAppointmentPanel(frame, mockController, "doc1") {
            {
                setModal(false);
            }
        };
        JButton cancelBtn = TestUtils.getButton(panel, "Cancel");
        assertNotNull(cancelBtn);
        SwingUtilities.invokeAndWait(cancelBtn::doClick);
        assertFalse(panel.isDisplayable());
    }

}