package physicianconnect.presentation;

import physicianconnect.logic.controller.AppointmentController;   // NEW
import physicianconnect.objects.Appointment;
import physicianconnect.presentation.config.UIConfig;
import physicianconnect.presentation.config.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * Dialog for viewing / editing / deleting a single appointment.
 * All persistence now goes through AppointmentController.
 */
public class ViewAppointmentPanel extends JDialog {
    private final AppointmentController appointmentController;   // CHANGED
    private final Appointment appointment;
    private final Runnable onSuccess;       // may be null

    private JTextArea notesArea;
    private JSpinner dateSpinner;
    private JSpinner timeSpinner;

    // ──────────────────────────────────────────────────────────────────────────
    public ViewAppointmentPanel(JFrame parent, AppointmentController controller,   // CHANGED
                                 Appointment appt) {
        this(parent, controller, appt, null);
    }

    public ViewAppointmentPanel(JFrame parent,
                                 AppointmentController controller,   // CHANGED
                                 Appointment appt, Runnable onSuccess) {
                                    
        super(parent, UIConfig.VIEW_APPOINTMENT_DIALOG_TITLE, true);
        this.appointmentController = controller;   // CHANGED
        this.appointment           = appt;
        this.onSuccess             = onSuccess;
        initUI();
        setLocationRelativeTo(parent);
    }

    // ──────────────────────────────────────────────────────────────────────────
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(UITheme.BACKGROUND_COLOR);
        setSize(500, 600);

        /* ---------- Header ---------- */
        JPanel header = new JPanel(new BorderLayout(10, 10));
        header.setBackground(UITheme.BACKGROUND_COLOR);
        header.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel patient = new JLabel(
                UIConfig.PATIENT_LABEL + appointment.getPatientName());
        patient.setFont(UITheme.HEADER_FONT);
        patient.setForeground(UITheme.TEXT_COLOR);
        header.add(patient, BorderLayout.WEST);

        add(header, BorderLayout.NORTH);

        /* ---------- Main Content Panel ---------- */
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(UITheme.BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(0, 20, 0, 20));

        /* ---------- Date/Time Panel ---------- */
        JPanel dateTimePanel = new JPanel();
        dateTimePanel.setLayout(new BoxLayout(dateTimePanel, BoxLayout.Y_AXIS));
        dateTimePanel.setBackground(UITheme.BACKGROUND_COLOR);
        dateTimePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(UITheme.PRIMARY_COLOR, 1),
            "Appointment Time",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            UITheme.LABEL_FONT,
            UITheme.TEXT_COLOR
        ));

        // Date
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        datePanel.setBackground(UITheme.BACKGROUND_COLOR);
        JLabel dateLabel = new JLabel(UIConfig.DATE_LABEL);
        dateLabel.setFont(UITheme.LABEL_FONT);
        dateLabel.setForeground(UITheme.TEXT_COLOR);
        datePanel.add(dateLabel);

        dateSpinner = new JSpinner(new SpinnerDateModel());
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        dateSpinner.setFont(UITheme.LABEL_FONT);
        // Set initial date
        Date initialDate = Date.from(appointment.getDateTime().atZone(ZoneId.systemDefault()).toInstant());
        dateSpinner.setValue(initialDate);
        datePanel.add(dateSpinner);
        dateTimePanel.add(datePanel);

        // Add some vertical space
        dateTimePanel.add(Box.createVerticalStrut(10));

        // Time
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        timePanel.setBackground(UITheme.BACKGROUND_COLOR);
        JLabel timeLabel = new JLabel(UIConfig.TIME_LABEL);
        timeLabel.setFont(UITheme.LABEL_FONT);
        timeLabel.setForeground(UITheme.TEXT_COLOR);
        timePanel.add(timeLabel);

        timeSpinner = new JSpinner(new SpinnerDateModel());
        timeSpinner.setEditor(new JSpinner.DateEditor(timeSpinner, "HH:mm"));
        timeSpinner.setFont(UITheme.LABEL_FONT);
        // Set initial time
        timeSpinner.setValue(initialDate);
        timePanel.add(timeSpinner);
        dateTimePanel.add(timePanel);

        // Add some padding at the bottom
        dateTimePanel.add(Box.createVerticalStrut(10));

        mainPanel.add(dateTimePanel);
        mainPanel.add(Box.createVerticalStrut(20));

        /* ---------- Notes ---------- */
        JPanel notesPanel = new JPanel(new BorderLayout(10, 10));
        notesPanel.setBackground(UITheme.BACKGROUND_COLOR);
        notesPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(UITheme.PRIMARY_COLOR, 1),
            UIConfig.APPOINTMENT_NOTES_LABEL,
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            UITheme.LABEL_FONT,
            UITheme.TEXT_COLOR
        ));

        notesArea = new JTextArea(appointment.getNotes());
        notesArea.setFont(UITheme.LABEL_FONT);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setBackground(UITheme.BACKGROUND_COLOR);
        notesArea.setForeground(UITheme.TEXT_COLOR);
        notesArea.setRows(8); // Set a fixed number of rows

        JScrollPane scroll = new JScrollPane(notesArea);
        scroll.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        notesPanel.add(scroll, BorderLayout.CENTER);

        mainPanel.add(notesPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Wrap mainPanel in a scroll pane
        JScrollPane mainScroll = new JScrollPane(mainPanel);
        mainScroll.setBorder(null);
        add(mainScroll, BorderLayout.CENTER);

        /* ---------- Buttons ---------- */
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btns.setBackground(UITheme.BACKGROUND_COLOR);
        btns.setBorder(new EmptyBorder(10, 20, 20, 20));

        JButton update = style(UIConfig.BUTTON_UPDATE_NOTES, UITheme.SUCCESS_BUTTON_COLOR);
        JButton delete = style(UIConfig.BUTTON_DELETE_APPOINTMENT, UITheme.ERROR_BUTTON_COLOR);
        JButton close = style(UIConfig.BUTTON_CLOSE, UITheme.CANCEL_BUTTON_COLOR);

        // Update appointment
        update.addActionListener(e -> {
            try {
                // Get date and time from spinners
                Date datePart = (Date) dateSpinner.getValue();
                Date timePart = (Date) timeSpinner.getValue();

                Calendar cDate = Calendar.getInstance();
                cDate.setTime(datePart);

                Calendar cTime = Calendar.getInstance();
                cTime.setTime(timePart);

                cDate.set(Calendar.HOUR_OF_DAY, cTime.get(Calendar.HOUR_OF_DAY));
                cDate.set(Calendar.MINUTE, cTime.get(Calendar.MINUTE));
                cDate.set(Calendar.SECOND, 0);
                cDate.set(Calendar.MILLISECOND, 0);

                LocalDateTime newDateTime = cDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

                // Check if date/time has changed
                if (!newDateTime.equals(appointment.getDateTime())) {
                    // Update date/time
                    appointmentController.updateAppointmentDateTime(
                        appointment,
                        newDateTime
                    );
                }

                // Check if notes have changed
                if (!notesArea.getText().equals(appointment.getNotes())) {
                    // Update notes
                    appointmentController.updateAppointmentNotes(
                        appointment,
                        notesArea.getText()
                    );
                }

                JOptionPane.showMessageDialog(
                    this,
                    UIConfig.MESSAGE_NOTES_UPDATED,
                    UIConfig.SUCCESS_DIALOG_TITLE,
                    JOptionPane.INFORMATION_MESSAGE
                );
                if (onSuccess != null) onSuccess.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    this,
                    UIConfig.ERROR_UPDATING_NOTES + ex.getMessage(),
                    UIConfig.ERROR_DIALOG_TITLE,
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        // Delete
        delete.addActionListener(e -> {
            int c = JOptionPane.showConfirmDialog(
                    this, UIConfig.CONFIRM_DELETE_MESSAGE,
                    UIConfig.CONFIRM_DIALOG_TITLE,
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (c == JOptionPane.YES_OPTION) {
                try {
                    appointmentController.deleteAppointment(appointment);
                    if (onSuccess != null) onSuccess.run();
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            this, UIConfig.ERROR_DELETING_APPOINTMENT + ex.getMessage(),
                            UIConfig.ERROR_DIALOG_TITLE,
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        close.addActionListener(e -> dispose());

        btns.add(update);
        btns.add(delete);
        btns.add(close);
        add(btns, BorderLayout.SOUTH);
    }

    private JButton style(String text, Color base) {
        JButton b = new JButton(text);
        b.setFont(UITheme.BUTTON_FONT);
        b.setForeground(UITheme.BACKGROUND_COLOR);
        b.setBackground(base);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        UITheme.applyHoverEffect(b);
        return b;
    }
}