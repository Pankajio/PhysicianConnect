package physicianconnect.presentation;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.border.EmptyBorder;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JComboBox;
import java.util.List;
import java.util.ArrayList;



import physicianconnect.logic.controller.AppointmentController;
import physicianconnect.logic.exceptions.InvalidAppointmentException;
import physicianconnect.presentation.config.UIConfig;
import physicianconnect.presentation.config.UITheme;

/**
 * AddAppointmentDialog supports:
 *  • A 3-arg constructor  (parent, controller, physicianId)
 *  • A 4-arg constructor  (parent, controller, physicianId, onSuccessCallback)
 */
public class AddAppointmentPanel extends JDialog {

    /*------------------------------------------------------------------*/
    /* Fields                                                           */
    /*------------------------------------------------------------------*/
    private final AppointmentController appointmentController;
    private final String physicianId;
    private final Runnable onSuccessCallback;  // may be null

    private JTextField patientNameField;
    public JSpinner dateSpinner;
    public JComboBox<String> timeCombo;
    private JTextArea notesArea;

    /*------------------------------------------------------------------*/
    /* Constructors                                                     */
    /*------------------------------------------------------------------*/
    public AddAppointmentPanel(JFrame parent,
                                AppointmentController appointmentController,
                                String physicianId) {
        this(parent, appointmentController, physicianId, null);
    }

    public AddAppointmentPanel(JFrame parent,
                                AppointmentController appointmentController,
                                String physicianId,
                                Runnable onSuccessCallback) {
        super(parent, UIConfig.ADD_APPOINTMENT_DIALOG_TITLE, true);
        this.appointmentController = appointmentController;
        this.physicianId = physicianId;
        this.onSuccessCallback = onSuccessCallback;
        initializeUI();
        setLocationRelativeTo(parent);
    }

    /*------------------------------------------------------------------*/
    /* UI Setup                                                         */
    /*------------------------------------------------------------------*/
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(UITheme.BACKGROUND_COLOR);
        setSize(500, 500);

        // ─── Title ───
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(UITheme.BACKGROUND_COLOR);
        JLabel titleLabel = new JLabel(UIConfig.ADD_APPOINTMENT_DIALOG_TITLE);
        titleLabel.setFont(UITheme.HEADER_FONT);
        titleLabel.setForeground(UITheme.TEXT_COLOR);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // ─── Form ───
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UITheme.BACKGROUND_COLOR);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Patient name
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        JLabel nameLabel = new JLabel(UIConfig.PATIENT_NAME_LABEL);
        nameLabel.setFont(UITheme.LABEL_FONT);
        nameLabel.setForeground(UITheme.TEXT_COLOR);
        formPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        patientNameField = new JTextField(20);
        patientNameField.setFont(UITheme.TEXTFIELD_FONT);
        formPanel.add(patientNameField, gbc);

        // Date
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        JLabel dateLabel = new JLabel(UIConfig.DATE_LABEL);
        dateLabel.setFont(UITheme.LABEL_FONT);
        dateLabel.setForeground(UITheme.TEXT_COLOR);
        formPanel.add(dateLabel, gbc);

        gbc.gridx = 1;
        dateSpinner = new JSpinner(new SpinnerDateModel());
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        dateSpinner.setFont(UITheme.LABEL_FONT);
        formPanel.add(dateSpinner, gbc);

        // Time
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        JLabel timeLabel = new JLabel(UIConfig.TIME_LABEL);
        timeLabel.setFont(UITheme.LABEL_FONT);
        timeLabel.setForeground(UITheme.TEXT_COLOR);
        formPanel.add(timeLabel, gbc);

        gbc.gridx = 1;
        List<String> slots = buildTimeSlots(8, 17);            // pick your open/close
        timeCombo = new JComboBox<>(slots.toArray(new String[0]));
        timeCombo.setFont(UITheme.LABEL_FONT);
        timeCombo.setSelectedIndex(0);
        formPanel.add(timeCombo, gbc);

        // Notes
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        JLabel notesLabel = new JLabel(UIConfig.NOTES_LABEL);
        notesLabel.setFont(UITheme.LABEL_FONT);
        notesLabel.setForeground(UITheme.TEXT_COLOR);
        formPanel.add(notesLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.5;
        notesArea = new JTextArea(3, 20);
        notesArea.setFont(UITheme.TEXTFIELD_FONT);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane noteScroll = new JScrollPane(notesArea);
        noteScroll.setBorder(BorderFactory.createLineBorder(UITheme.PRIMARY_COLOR, 1));
        formPanel.add(noteScroll, gbc);

        add(formPanel, BorderLayout.CENTER);

        // ─── Buttons ───
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(UITheme.BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        JButton saveBtn = createStyledButton(UIConfig.SAVE_BUTTON_TEXT);
        JButton cancelBtn = createStyledButton(UIConfig.CANCEL_BUTTON_TEXT);

        saveBtn.addActionListener(e -> saveAppointment());
        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Build a list of “HH:mm” strings from openHour:00 to (closeHour-1):30.
     * e.g. openHour=9, closeHour=17 → 9:00, 9:30, …, 16:30
     */
    private List<String> buildTimeSlots(int openHour, int closeHour) {
        List<String> slots = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime t = LocalTime.of(openHour, 0);
        LocalTime last = LocalTime.of(closeHour - 1, 30);
        while (!t.isAfter(last)) {
            slots.add(t.format(fmt));
            t = t.plusMinutes(30);
        }
        return slots;
    }

    /*------------------------------------------------------------------*/
    /* Save logic                                                       */
    /*------------------------------------------------------------------*/
    private void saveAppointment() {
        try {
            // 1) Patient name
            String patient = patientNameField.getText().trim();
            if (patient.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        UIConfig.ERROR_INVALID_NAME,
                        UIConfig.ERROR_DIALOG_TITLE,
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 2) Combine date + selected slot → LocalDateTime
            Date datePart = (Date) dateSpinner.getValue();
            String sel = (String) timeCombo.getSelectedItem();
            DateTimeFormatter slotFmt = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime slot = LocalTime.parse(sel, slotFmt);

            Calendar cDate = Calendar.getInstance();
            cDate.setTime(datePart);
            cDate.set(Calendar.HOUR_OF_DAY, slot.getHour());
            cDate.set(Calendar.MINUTE, slot.getMinute());
            cDate.set(Calendar.SECOND, 0);
            cDate.set(Calendar.MILLISECOND, 0);

            LocalDateTime chosen = cDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            // 3) Delegate to controller
            appointmentController.createAppointment(
                    physicianId, patient, chosen, notesArea.getText()
            );

            JOptionPane.showMessageDialog(this,
                    UIConfig.SUCCESS_APPOINTMENT_ADDED,
                    UIConfig.SUCCESS_DIALOG_TITLE,
                    JOptionPane.INFORMATION_MESSAGE);

            // 4) Callback (refresh calendars)
            if (onSuccessCallback != null) {
                onSuccessCallback.run();
            }
            dispose();

        } catch (InvalidAppointmentException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    UIConfig.ERROR_DIALOG_TITLE,
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();  // for debugging
            JOptionPane.showMessageDialog(
                    this,
                    "Unexpected error: " + ex.getMessage(),
                    UIConfig.ERROR_DIALOG_TITLE,
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /*------------------------------------------------------------------*/
    /* Helpers                                                          */
    /*------------------------------------------------------------------*/
    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(UITheme.BUTTON_FONT);
        btn.setForeground(UITheme.BACKGROUND_COLOR);
        btn.setBackground(text.equalsIgnoreCase(UIConfig.SAVE_BUTTON_TEXT)
                ? UITheme.POSITIVE_COLOR
                : UITheme.ACCENT_LIGHT_COLOR);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        UITheme.applyHoverEffect(btn);
        return btn;
    }
}