package physicianconnect.presentation.physician;

import java.awt.BorderLayout;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import physicianconnect.logic.controller.PatientHistoryController;
import physicianconnect.logic.manager.AppointmentManager;
import physicianconnect.objects.Appointment;
import physicianconnect.presentation.config.UITheme;

/**
 * PatientHistoryPanel displays the history (appointments, prescriptions, referrals)
 * for a selected patient of the current physician, delegating formatting to
 * PatientHistoryController.
 */
public class PatientHistoryPanel extends JPanel {
    private final PatientHistoryController historyController;
    private final String physicianId;

    private JComboBox<String> patientCombo;
    private JTextArea historyArea;

    /**
     * Constructor for dependency injection / production use.
     *
     * @param appointmentManager    used only to populate the patient dropdown
     * @param historyController     controller that returns a formatted history string
     * @param physicianId           the currently‐logged‐in physician’s ID
     */
    public PatientHistoryPanel(AppointmentManager appointmentManager,
                               PatientHistoryController historyController,
                               String physicianId) {
        this.historyController  = historyController;
        this.physicianId        = physicianId;

        setLayout(new BorderLayout(10, 10));
        setBackground(UITheme.BACKGROUND_COLOR);

        // ─── Populate patient names into a sorted set ───
        Set<String> patientNames = appointmentManager
                .getAppointmentsForPhysician(physicianId)
                .stream()
                .map(Appointment::getPatientName)
                .collect(Collectors.toCollection(TreeSet::new));

        patientCombo = new JComboBox<>(patientNames.toArray(new String[0]));
        patientCombo.setName("patientCombo");
        patientCombo.setFont(UITheme.LABEL_FONT);
        patientCombo.setBackground(UITheme.BACKGROUND_COLOR);
        patientCombo.setForeground(UITheme.TEXT_COLOR);
        patientCombo.addActionListener(e -> updateHistory());

        historyArea = new JTextArea(15, 40);
        historyArea.setEditable(false);
        historyArea.setName("historyArea");
        historyArea.setFont(UITheme.TEXTFIELD_FONT);
        historyArea.setBackground(UITheme.BACKGROUND_COLOR);
        historyArea.setForeground(UITheme.TEXT_COLOR);

        add(patientCombo, BorderLayout.NORTH);
        add(new JScrollPane(historyArea), BorderLayout.CENTER);

        if (!patientNames.isEmpty()) {
            patientCombo.setSelectedIndex(0);
            updateHistory();
        }
    }

    /**
     * Whenever the selected patient changes, query the controller for the full
     * history string and display it.
     */
    private void updateHistory() {
        String patient = (String) patientCombo.getSelectedItem();
        if (patient == null) {
            historyArea.setText("");
            return;
        }

        // Delegate formatting to the controller
        String historyText = historyController.getPatientHistoryString(physicianId, patient);
        historyArea.setText(historyText);
    }
}