package physicianconnect.presentation.physician;

import physicianconnect.persistence.interfaces.MedicationPersistence;
import physicianconnect.logic.controller.PrescriptionController;
import physicianconnect.logic.exceptions.InvalidPrescriptionException;
import physicianconnect.logic.manager.AppointmentManager;
import physicianconnect.objects.Medication;
import physicianconnect.presentation.NotificationBanner;
import physicianconnect.presentation.NotificationPanel;
import physicianconnect.presentation.config.UIConfig;
import physicianconnect.presentation.config.UITheme;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * PrescribeMedicinePanel lets a physician select a patient,
 * pick a medication, auto‐fill its defaults, and then issue a prescription.
 * All validation and persistence are delegated to PrescriptionController.
 */
public class PrescribeMedicinePanel extends JPanel {
    private final PrescriptionController prescriptionController;
    private final String physicianId;
    private final Runnable onPrescriptionAdded;
    private final NotificationPanel notificationPanel;
    private final NotificationBanner notificationBanner;
    private final JFrame parentFrame;

    private JComboBox<String> patientCombo;
    private JComboBox<Medication> medicineCombo;
    private JTextField dosageField;
    private JTextField frequencyField;
    private JTextArea notesArea;
    private JButton prescribeButton;

    public PrescribeMedicinePanel(AppointmentManager appointmentManager,
                                  MedicationPersistence medicationPersistence,
                                  PrescriptionController prescriptionController,
                                  String physicianId,
                                  Runnable onPrescriptionAdded,
                                  NotificationPanel notificationPanel,
                                  NotificationBanner notificationBanner,
                                  JFrame parentFrame) {
        this.prescriptionController = prescriptionController;
        this.physicianId            = physicianId;
        this.onPrescriptionAdded    = onPrescriptionAdded;
        this.notificationPanel       = notificationPanel;
        this.notificationBanner      = notificationBanner;
        this.parentFrame           = parentFrame;

        setLayout(new GridBagLayout());
        setBackground(UITheme.BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        // ─────────── Patient ComboBox ───────────
        Set<String> patientNames = new TreeSet<>(appointmentManager
                .getAppointmentsForPhysician(physicianId)
                .stream()
                .map(a -> a.getPatientName())
                .collect(Collectors.toSet()));
        patientCombo = new JComboBox<>(patientNames.toArray(new String[0]));
        patientCombo.setFont(UITheme.LABEL_FONT);
        patientCombo.setBackground(UITheme.BACKGROUND_COLOR);
        patientCombo.setForeground(UITheme.TEXT_COLOR);

        // ─────────── Medicine ComboBox ───────────
        List<Medication> meds = medicationPersistence.getAllMedications();
        medicineCombo = new JComboBox<>(meds.toArray(new Medication[0]));
        medicineCombo.setFont(UITheme.LABEL_FONT);
        medicineCombo.setBackground(UITheme.BACKGROUND_COLOR);
        medicineCombo.setForeground(UITheme.TEXT_COLOR);

        // ─────────── Dosage, Frequency, Notes ───────────
        dosageField    = new JTextField();
        dosageField.setFont(UITheme.LABEL_FONT);
        frequencyField = new JTextField();
        frequencyField.setFont(UITheme.LABEL_FONT);
        notesArea      = new JTextArea(3, 20);
        notesArea.setFont(UITheme.TEXTFIELD_FONT);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane notesScroll = new JScrollPane(notesArea);

        // ─────────── Auto-fill on medication change ───────────
        medicineCombo.addActionListener(e -> {
            Medication med = (Medication) medicineCombo.getSelectedItem();
            if (med != null) {
                dosageField.setText(med.getDosage());
                frequencyField.setText(med.getDefaultFrequency());
                notesArea.setText(med.getDefaultNotes());
            }
        });
        if (medicineCombo.getItemCount() > 0) {
            medicineCombo.setSelectedIndex(0);
            Medication med = (Medication) medicineCombo.getSelectedItem();
            if (med != null) {
                dosageField.setText(med.getDosage());
                frequencyField.setText(med.getDefaultFrequency());
                notesArea.setText(med.getDefaultNotes());
            }
        }

        // ─────────── Add Components ───────────
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel(UIConfig.PATIENT_LABEL), gbc);
        gbc.gridx = 1;
        add(patientCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel(UIConfig.MEDICINE_LABEL), gbc);
        gbc.gridx = 1;
        add(medicineCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel(UIConfig.DOSAGE_LABEL), gbc);
        gbc.gridx = 1;
        add(dosageField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel(UIConfig.FREQUENCY_LABEL), gbc);
        gbc.gridx = 1;
        add(frequencyField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        add(new JLabel(UIConfig.NOTES_LABEL), gbc);
        gbc.gridx = 1;
        gbc.fill  = GridBagConstraints.BOTH;
        add(notesScroll, gbc);
        gbc.fill  = GridBagConstraints.HORIZONTAL;

        prescribeButton = new JButton(UIConfig.PRESCRIBE_BUTTON_TEXT);
        prescribeButton.setFont(UITheme.BUTTON_FONT);
        prescribeButton.setBackground(UITheme.PRIMARY_COLOR);
        prescribeButton.setForeground(UITheme.BACKGROUND_COLOR);
        prescribeButton.setFocusPainted(false);
        prescribeButton.setBorderPainted(false);
        prescribeButton.setOpaque(true);
        UITheme.applyHoverEffect(prescribeButton);

        gbc.gridx = 1; gbc.gridy = 5;
        add(prescribeButton, gbc);

        // ─────────── Action Listener ───────────
        prescribeButton.addActionListener(e -> {
            String patient   = (String) patientCombo.getSelectedItem();
            Medication med   = (Medication) medicineCombo.getSelectedItem();
            String dosage    = dosageField.getText().trim();
            String frequency = frequencyField.getText().trim();
            String notes     = notesArea.getText().trim();

            // Basic required‐field check
            if (patient == null || med == null || dosage.isEmpty() || frequency.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        UIConfig.ERROR_REQUIRED_FIELD,
                        UIConfig.ERROR_DIALOG_TITLE,
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // Delegate to controller
            try {
                prescriptionController.createPrescription(
                        physicianId,
                        patient,
                        med.getName(),
                        med.getDosage(),
                        dosage,
                        frequency,
                        notes
                );

                String message = String.format("New prescription added for %s: %s", patient, med.getName());
                
                // Show success pop-up
                JOptionPane.showMessageDialog(
                    this,
                    message,
                    UIConfig.SUCCESS_DIALOG_TITLE,
                    JOptionPane.INFORMATION_MESSAGE
                );
                
                // Show banner notification
                if (notificationBanner != null) {
                    notificationBanner.show(message, event -> {
                        // Refresh the views
                        if (onPrescriptionAdded != null) {
                            onPrescriptionAdded.run();
                        }
                        // Close the dialog
                        Window window = SwingUtilities.getWindowAncestor(this);
                        if (window != null) {
                            window.dispose();
                        }
                    });
                }
                
                // Add to notification panel
                if (notificationPanel != null) {
                    notificationPanel.addNotification(message, "New Prescription!");
                }

                // Close the dialog
                Window window = SwingUtilities.getWindowAncestor(this);
                if (window != null) {
                    window.dispose();
                }

            } catch (InvalidPrescriptionException ex) {
                // Display the validator's message
                JOptionPane.showMessageDialog(
                        this,
                        ex.getMessage(),
                        UIConfig.ERROR_DIALOG_TITLE,
                        JOptionPane.ERROR_MESSAGE
                );
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                        this,
                        UIConfig.ERROR_INVALID_INPUT + ex.getMessage(),
                        UIConfig.ERROR_DIALOG_TITLE,
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}