package physicianconnect.presentation.receptionist;

import physicianconnect.objects.Receptionist;
import physicianconnect.presentation.config.UIConfig;
import physicianconnect.logic.manager.ReceptionistManager;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.awt.image.BufferedImage;

public class ReceptionistProfilePanel extends JPanel {
    private final JLabel photoLabel;
    private final JButton changePhotoButton;
    private final JTextField nameField;
    private final JTextField emailField;
    private final JButton signOutButton;
    private final JButton editButton;
    private final JButton saveButton;
    private final JButton cancelButton;
    private final JCheckBox notifyAppointments;
    private final JCheckBox notifyBilling;
    private final JCheckBox notifyMessages;

    private final Runnable logoutCallback;
    private final Runnable onProfileUpdated;

    private final Receptionist receptionist;
    private final ReceptionistManager receptionistManager;

    private static final int MAX_PHOTO_SIZE = 200;

    public ReceptionistProfilePanel(Receptionist receptionist, ReceptionistManager receptionistManager,
            Runnable logoutCallback, Runnable onProfileUpdated) {
        this.receptionist = receptionist;
        this.receptionistManager = receptionistManager;
        this.logoutCallback = logoutCallback;
        this.onProfileUpdated = onProfileUpdated;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- Photo Panel (left)
        photoLabel = new JLabel();
        photoLabel.setPreferredSize(new Dimension(MAX_PHOTO_SIZE, MAX_PHOTO_SIZE));
        photoLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        photoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loadProfilePhoto(receptionist.getId());

        changePhotoButton = new JButton(UIConfig.CHANGE_PHOTO_BUTTON_TEXT);
        changePhotoButton.addActionListener(e -> {
            chooseAndUploadPhoto();
        });

        JPanel photoPanel = new JPanel(new BorderLayout(10, 10));
        photoPanel.setBackground(getBackground());
        photoPanel.add(photoLabel, BorderLayout.CENTER);
        photoPanel.add(changePhotoButton, BorderLayout.SOUTH);

        // --- Form Fields (center)
        nameField = new JTextField(receptionist.getName());
        emailField = new JTextField(receptionist.getEmail());
        emailField.setEditable(false);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(getBackground());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        JLabel nameLabel = new JLabel(UIConfig.NAME_LABEL);
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD));
        nameField.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        JLabel emailLabel = new JLabel(UIConfig.USER_EMAIL_LABEL);
        emailLabel.setFont(emailLabel.getFont().deriveFont(Font.BOLD));
        emailField.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(emailLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);

        JLabel notifyLabel = new JLabel(UIConfig.NOTIFICATION_PREFS_LABEL);
        notifyLabel.setFont(notifyLabel.getFont().deriveFont(Font.BOLD));
        notifyAppointments = new JCheckBox(UIConfig.NOTIFY_APPOINTMENTS, receptionist.isNotifyAppointment());
        notifyBilling = new JCheckBox(UIConfig.NOTIFY_BILLING, receptionist.isNotifyBilling());
        notifyMessages = new JCheckBox(UIConfig.MESSAGES_DIALOG_TITLE, receptionist.isNotifyMessages());

        JPanel checkboxPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        checkboxPanel.setBackground(getBackground());
        checkboxPanel.add(notifyAppointments);
        checkboxPanel.add(notifyBilling);
        checkboxPanel.add(notifyMessages);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(notifyLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(checkboxPanel, gbc);

        // Create photo directory if it doesn't exist
        createPhotoDirectory();

        JPanel paddedFormPanel = new JPanel(new BorderLayout(20, 0));
        paddedFormPanel.setBackground(getBackground());
        paddedFormPanel.add(formPanel, BorderLayout.CENTER);
        paddedFormPanel.add(photoPanel, BorderLayout.WEST);

        add(paddedFormPanel, BorderLayout.CENTER);

        // --- Button Panel (bottom)
        editButton = new JButton(UIConfig.EDIT_BUTTON_TEXT);
        saveButton = new JButton(UIConfig.SAVE_BUTTON_TEXT);
        cancelButton = new JButton(UIConfig.CANCEL_BUTTON_TEXT);
        signOutButton = new JButton(UIConfig.LOGOUT_BUTTON_TEXT);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(getBackground());
        buttonPanel.add(editButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(signOutButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        editButton.addActionListener(e -> setEditable(true));
        cancelButton.addActionListener(e -> {
            nameField.setText(receptionist.getName());
            setEditable(false);
            notifyAppointments.setSelected(receptionist.isNotifyAppointment());
            notifyBilling.setSelected(receptionist.isNotifyBilling());
            notifyMessages.setSelected(receptionist.isNotifyMessages());
        });
        saveButton.addActionListener(e -> saveProfile());
        signOutButton.addActionListener(e -> {
            Window topWindow = SwingUtilities.getWindowAncestor(this);
            if (topWindow != null) {
                topWindow.dispose();
            }
            if (logoutCallback != null) {
                logoutCallback.run();
            }
        });

        saveButton.setVisible(false);
        cancelButton.setVisible(false);
        setEditable(false);
    }

    private void saveProfile() {
        try {
            String newName = nameField.getText().trim();
            if (newName.isEmpty()) {
                throw new IllegalArgumentException(UIConfig.ERROR_NAME_EMPTY);
            }

            receptionistManager.validateAndUpdateReceptionist(
                    receptionist,
                    newName,
                    notifyAppointments.isSelected(),
                    notifyBilling.isSelected(),
                    notifyMessages.isSelected());

            if (onProfileUpdated != null) {
                onProfileUpdated.run();
            }

            JOptionPane.showMessageDialog(this, UIConfig.PROFILE_UPDATED_MESSAGE);
            setEditable(false);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), UIConfig.VALIDATION_ERROR_TITLE,
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setEditable(boolean editable) {
        nameField.setEditable(editable);
        changePhotoButton.setEnabled(editable);
        saveButton.setVisible(editable);
        cancelButton.setVisible(editable);
        editButton.setVisible(!editable);
        signOutButton.setVisible(!editable);
        notifyAppointments.setEnabled(editable);
        notifyBilling.setEnabled(editable);
        notifyMessages.setEnabled(editable);
    }

    private void createPhotoDirectory() {
        File photoDir = new File(UIConfig.PHOTO_DIR);
        if (!photoDir.exists()) {
            photoDir.mkdirs();
        }
    }

    private void chooseAndUploadPhoto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(UIConfig.PHOTO_DIR));
        chooser.setDialogTitle(UIConfig.SELECT_PROFILE_PHOTO_DIALOG_TITLE);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) return true;
                String name = f.getName().toLowerCase();
                return name.startsWith(UIConfig.PHOTO_PREFIX_RECEPTIONIST) && 
                       (name.endsWith(UIConfig.SUPPORTED_IMAGE_TYPES[0]) || 
                        name.endsWith(UIConfig.SUPPORTED_IMAGE_TYPES[1]) || 
                        name.endsWith(UIConfig.SUPPORTED_IMAGE_TYPES[2]));
            }

            @Override
            public String getDescription() {
                return UIConfig.RECEPTIONIST_PHOTO_FILTER_DESC;
            }
        });

        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            try {
                // Validate image
                BufferedImage img = ImageIO.read(selectedFile);
                if (img == null) {
                    throw new IOException(UIConfig.ERROR_INVALID_IMAGE_FILE);
                }

                // Resize image if needed
                if (img.getWidth() > MAX_PHOTO_SIZE || img.getHeight() > MAX_PHOTO_SIZE) {
                    img = resizeImage(img);
                }

                // Ensure directory exists
                File photoDir = new File(UIConfig.PHOTO_DIR);
                if (!photoDir.exists()) {
                    photoDir.mkdirs();
                }

                // Save resized image
                File outputFile = new File(photoDir, UIConfig.PHOTO_PREFIX_RECEPTIONIST + receptionist.getId() + UIConfig.PHOTO_EXTENSION);
                ImageIO.write(img, "png", outputFile);

                // Update profile photo
                FileInputStream fis = new FileInputStream(outputFile);
                receptionistManager.uploadProfilePhoto(receptionist.getId(), fis);
                fis.close();
                loadProfilePhoto(receptionist.getId());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        UIConfig.ERROR_PHOTO_UPLOAD + ex.getMessage(),
                        UIConfig.ERROR_DIALOG_TITLE,
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private BufferedImage resizeImage(BufferedImage originalImage) {
        double ratio = Math.min(
                (double) MAX_PHOTO_SIZE / originalImage.getWidth(),
                (double) MAX_PHOTO_SIZE / originalImage.getHeight());

        int newWidth = (int) (originalImage.getWidth() * ratio);
        int newHeight = (int) (originalImage.getHeight() * ratio);

        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g.dispose();

        return resizedImage;
    }

    private void loadProfilePhoto(String id) {
        File file = new File(UIConfig.PHOTO_DIR, UIConfig.PHOTO_PREFIX_RECEPTIONIST + id + UIConfig.PHOTO_EXTENSION);
        if (file.exists()) {
            try {
                BufferedImage img = ImageIO.read(file);
                if (img != null) {
                    Image scaledImg = img.getScaledInstance(MAX_PHOTO_SIZE, MAX_PHOTO_SIZE, Image.SCALE_SMOOTH);
                    photoLabel.setIcon(new ImageIcon(scaledImg));
                    return;
                }
            } catch (IOException ex) {
                // Fall through to placeholder if image loading fails
                System.out.println(UIConfig.ERROR_PHOTO_LOADING + ex.getMessage());
            }
        }

        // Create placeholder if no photo exists or loading failed
        BufferedImage placeholder = new BufferedImage(MAX_PHOTO_SIZE, MAX_PHOTO_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = placeholder.createGraphics();
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRect(0, 0, MAX_PHOTO_SIZE, MAX_PHOTO_SIZE);
        g2.setColor(Color.DARK_GRAY);
        g2.setFont(new Font("Arial", Font.PLAIN, 14));
        String text = UIConfig.NO_PHOTO_PLACEHOLDER_TEXT;
        FontMetrics fm = g2.getFontMetrics();
        int x = (MAX_PHOTO_SIZE - fm.stringWidth(text)) / 2;
        int y = (MAX_PHOTO_SIZE - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(text, x, y);
        g2.dispose();
        photoLabel.setIcon(new ImageIcon(placeholder));
    }
}
