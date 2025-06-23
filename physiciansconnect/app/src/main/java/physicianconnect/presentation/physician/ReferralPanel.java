package physicianconnect.presentation.physician;

import physicianconnect.logic.manager.ReferralManager;
import physicianconnect.objects.Referral;
import physicianconnect.presentation.NotificationBanner;
import physicianconnect.presentation.NotificationPanel;
import physicianconnect.presentation.config.UIConfig;
import physicianconnect.presentation.config.UITheme;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * ReferralPanel allows a physician to create a new referral for a patient
 * and view existing referrals for that patient.
 */
public class ReferralPanel extends JPanel {
    private final ReferralManager referralManager;
    private final String physicianId;
    private final List<String> patientNames;
    private final NotificationPanel notificationPanel;
    private final NotificationBanner notificationBanner;
    private final JFrame parentFrame;

    private JComboBox<String> patientCombo;
    private JTextField typeField;
    private JTextArea detailsArea;
    private JTextArea referralListArea;
    private JButton createButton;

    public ReferralPanel(ReferralManager referralManager, String physicianId, List<String> patientNames,
                        NotificationPanel notificationPanel, NotificationBanner notificationBanner, JFrame parentFrame) {
        this.referralManager = referralManager;
        this.physicianId = physicianId;
        this.patientNames = patientNames;
        this.notificationPanel = notificationPanel;
        this.notificationBanner = notificationBanner;
        this.parentFrame = parentFrame;

        setLayout(new BorderLayout(10, 10));
        setBackground(UITheme.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ─────────── Top: Create Referral ───────────
        JPanel createPanel = new JPanel(new GridBagLayout());
        createPanel.setBackground(UITheme.BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        patientCombo = new JComboBox<>(patientNames.toArray(new String[0]));
        patientCombo.setName("patientCombo");
        patientCombo.setFont(UITheme.LABEL_FONT);
        patientCombo.setBackground(UITheme.BACKGROUND_COLOR);
        patientCombo.setForeground(UITheme.TEXT_COLOR);

        typeField = new JTextField(15);
        typeField.setName("typeField");
        typeField.setFont(UITheme.TEXTFIELD_FONT);
        typeField.setBackground(UITheme.BACKGROUND_COLOR);
        typeField.setForeground(UITheme.TEXT_COLOR);

        detailsArea = new JTextArea(3, 20);
        detailsArea.setName("detailsArea");
        detailsArea.setFont(UITheme.TEXTFIELD_FONT);
        detailsArea.setBackground(UITheme.BACKGROUND_COLOR);
        detailsArea.setForeground(UITheme.TEXT_COLOR);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        JScrollPane detailsScroll = new JScrollPane(detailsArea);

        createButton = new JButton(UIConfig.CREATE_REFERRAL_BUTTON_TEXT);
        createButton.setName("createButton");
        createButton.setFont(UITheme.BUTTON_FONT);
        createButton.setBackground(UITheme.PRIMARY_COLOR);
        createButton.setForeground(UITheme.BACKGROUND_COLOR);
        createButton.setFocusPainted(false);
        createButton.setBorderPainted(false);
        createButton.setOpaque(true);
        UITheme.applyHoverEffect(createButton);

        // Row 0: Patient label + combo
        gbc.gridx = 0; gbc.gridy = 0;
        createPanel.add(new JLabel(UIConfig.PATIENT_LABEL), gbc);
        gbc.gridx = 1;
        createPanel.add(patientCombo, gbc);

        // Row 1: Type label + text field
        gbc.gridx = 0; gbc.gridy = 1;
        createPanel.add(new JLabel(UIConfig.TYPE_LABEL), gbc);
        gbc.gridx = 1;
        createPanel.add(typeField, gbc);

        // Row 2: Details label + text area
        gbc.gridx = 0; gbc.gridy = 2;
        createPanel.add(new JLabel(UIConfig.DETAILS_LABEL), gbc);
        gbc.gridx = 1;
        createPanel.add(detailsScroll, gbc);

        // Row 3: Create button
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        createPanel.add(createButton, gbc);

        // ─────────── Center: List of Referrals ───────────
        referralListArea = new JTextArea(10, 40);
        referralListArea.setEditable(false);
        referralListArea.setName("referralListArea");
        referralListArea.setFont(UITheme.TEXTFIELD_FONT);
        referralListArea.setBackground(UITheme.BACKGROUND_COLOR);
        referralListArea.setForeground(UITheme.TEXT_COLOR);
        JScrollPane listScroll = new JScrollPane(referralListArea);
        listScroll.setBorder(BorderFactory.createTitledBorder(UIConfig.REFERRALS_LIST_TITLE));

        add(createPanel, BorderLayout.NORTH);
        add(listScroll, BorderLayout.CENTER);

        // ─────────── Action Listeners ───────────
        createButton.addActionListener(e -> createReferral());
        patientCombo.addActionListener(e -> updateReferralList());

        if (patientCombo.getItemCount() > 0) {
            patientCombo.setSelectedIndex(0);
            updateReferralList();
        }
    }

    private void createReferral() {
        String patient = (String) patientCombo.getSelectedItem();
        String type    = typeField.getText().trim();
        String details = detailsArea.getText().trim();
        String date    = LocalDate.now().toString();

        if (patient == null || type.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    UIConfig.ERROR_REQUIRED_FIELD_REFERRAL,
                    UIConfig.ERROR_DIALOG_TITLE,
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        Referral referral = new Referral(
                0,
                physicianId,
                patient,
                type,
                details,
                date
        );
        referralManager.addReferral(referral);

        String message = String.format("New referral created for %s: %s", patient, type);
        
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
                updateReferralList();
                // Close the dialog
                Window window = SwingUtilities.getWindowAncestor(this);
                if (window != null) {
                    window.dispose();
                }
            });
        }
        
        // Add to notification panel
        if (notificationPanel != null) {
            notificationPanel.addNotification(message, "New Referral!");
        }

        // Close the dialog
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.dispose();
        }

        typeField.setText("");
        detailsArea.setText("");
        updateReferralList();
    }

    private void updateReferralList() {
        String patient = (String) patientCombo.getSelectedItem();
        if (patient == null) {
            referralListArea.setText("");
            return;
        }

        // “getReferralsForPatient” returns all referrals for this patient
        List<Referral> referrals = referralManager.getReferralsForPatient(patient);

        StringBuilder sb = new StringBuilder();
        sb.append(UIConfig.REFERRALS_HEADER).append(" ").append(patient).append(":\n");
        for (Referral r : referrals) {
            sb.append("  [")
                    .append(r.getDateCreated())
                    .append("] ")
                    .append(r.getReferralType())
                    .append(": ")
                    .append(r.getDetails())
                    .append("\n");
        }
        referralListArea.setText(sb.toString());
    }
}