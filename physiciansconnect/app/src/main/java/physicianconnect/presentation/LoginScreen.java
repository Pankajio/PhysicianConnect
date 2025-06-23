package physicianconnect.presentation;

import physicianconnect.AppController;
import physicianconnect.objects.Physician;
import physicianconnect.objects.Receptionist;
import physicianconnect.logic.controller.PhysicianController;
import physicianconnect.logic.controller.ReceptionistController;
import physicianconnect.logic.exceptions.InvalidCredentialException;
import physicianconnect.logic.manager.AppointmentManager;
import physicianconnect.logic.manager.PhysicianManager;
import physicianconnect.logic.manager.ReceptionistManager;
import physicianconnect.logic.validation.CredentialVerification;
import physicianconnect.presentation.config.UIConfig;
import physicianconnect.presentation.config.UITheme;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class LoginScreen extends JFrame {
    private final AppController controller;
    public JTextField emailField;
    public JPasswordField passField;
    public JButton loginBtn;
    public JButton createBtn;
    public JLabel errorLabel;

    public LoginScreen(PhysicianManager physicianManager, AppointmentManager appointmentManager,
            ReceptionistManager receptionistManager, AppController controller) {
        this.controller = controller;

        setTitle(UIConfig.LOGIN_DIALOG_TITLE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BACKGROUND_COLOR);

        // ───── Left Panel: Image ─────
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(UITheme.BACKGROUND_COLOR);
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/picture_assets/login_image.png"));
            Image scaled = icon.getImage().getScaledInstance(500, 600, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(scaled));
            imagePanel.add(imageLabel, BorderLayout.CENTER);
        } catch (Exception e) {
            imagePanel.add(new JLabel("Image failed to load"), BorderLayout.CENTER);
        }

        // ───── Right Panel: Login Form ─────
        JPanel rightPanel = new JPanel(new BorderLayout(20, 20));
        rightPanel.setBackground(UITheme.BACKGROUND_COLOR);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Welcome Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(UITheme.BACKGROUND_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        JLabel welcomeLabel = new JLabel(UIConfig.WELCOME_MESSAGE);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        welcomeLabel.setForeground(UITheme.TEXT_COLOR);
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel appLabel = new JLabel(UIConfig.APP_NAME);
        appLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        appLabel.setForeground(UITheme.PRIMARY_COLOR);
        appLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        headerPanel.add(welcomeLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        headerPanel.add(appLabel);

        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(UITheme.BACKGROUND_COLOR);

        // Email Field
        JPanel emailPanel = new JPanel(new BorderLayout(5, 0));
        emailPanel.setBackground(UITheme.BACKGROUND_COLOR);
        JLabel emailLabel = new JLabel(UIConfig.USER_EMAIL_LABEL);
        emailLabel.setFont(UITheme.LABEL_FONT);
        emailLabel.setForeground(UITheme.TEXT_COLOR);
        emailField = new JTextField(20);
        emailField.setFont(UITheme.TEXTFIELD_FONT);
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.ACCENT_LIGHT_COLOR),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        emailField.setName(UIConfig.EMAIL_FIELD_NAME);
        emailPanel.add(emailLabel, BorderLayout.NORTH);
        emailPanel.add(emailField, BorderLayout.CENTER);

        // Password Field
        JPanel passPanel = new JPanel(new BorderLayout(5, 0));
        passPanel.setBackground(UITheme.BACKGROUND_COLOR);
        JLabel passLabel = new JLabel(UIConfig.USER_PASSWORD_LABEL);
        passLabel.setFont(UITheme.LABEL_FONT);
        passLabel.setForeground(UITheme.TEXT_COLOR);
        passField = new JPasswordField(20);
        passField.setFont(UITheme.TEXTFIELD_FONT);
        passField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.ACCENT_LIGHT_COLOR),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        passField.setName(UIConfig.PASSWORD_FIELD_NAME);
        passPanel.add(passLabel, BorderLayout.NORTH);
        passPanel.add(passField, BorderLayout.CENTER);

        // Error Label
        errorLabel = new JLabel(" ");
        errorLabel.setFont(UITheme.LABEL_FONT.deriveFont(Font.ITALIC));
        errorLabel.setForeground(new Color(220, 53, 69));
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(UITheme.BACKGROUND_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        loginBtn = new JButton(UIConfig.LOGIN_BUTTON_TEXT);
        loginBtn.setFont(UITheme.BUTTON_FONT);
        loginBtn.setBackground(UITheme.PRIMARY_COLOR);
        loginBtn.setForeground(UITheme.BACKGROUND_COLOR);
        loginBtn.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginBtn.setOpaque(true);
        loginBtn.setBorderPainted(false);
        UITheme.applyHoverEffect(loginBtn);
        loginBtn.setName(UIConfig.LOGIN_BUTTON_NAME);

        createBtn = new JButton(UIConfig.CREAT_ACCOUNT_BUTTON_TEXT);
        createBtn.setFont(UITheme.BUTTON_FONT);
        createBtn.setBackground(UITheme.SUCCESS_BUTTON_COLOR);
        createBtn.setForeground(UITheme.BACKGROUND_COLOR);
        createBtn.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        createBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        createBtn.setOpaque(true);
        createBtn.setBorderPainted(false);
        UITheme.applyHoverEffect(createBtn);
        createBtn.setName(UIConfig.CREATE_ACCOUNT_BUTTON_NAME);

        buttonPanel.add(loginBtn);
        buttonPanel.add(createBtn);

        // Test Account Info
        JPanel testInfoPanel = new JPanel();
        testInfoPanel.setLayout(new BoxLayout(testInfoPanel, BoxLayout.Y_AXIS));
        testInfoPanel.setBackground(UITheme.BACKGROUND_COLOR);
        testInfoPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JLabel physicianHeader = new JLabel(UIConfig.PHYSICIAN_LOGIN_HEADER);
        physicianHeader.setFont(UITheme.LABEL_FONT.deriveFont(Font.BOLD));
        physicianHeader.setForeground(UITheme.TEXT_COLOR);

        JLabel physicianCreds = new JLabel(UIConfig.PHYSICIAN_LOGIN_INFO);
        physicianCreds.setFont(UITheme.LABEL_FONT.deriveFont(Font.ITALIC));
        physicianCreds.setForeground(Color.GRAY);

        JLabel receptionistHeader = new JLabel(UIConfig.RECEPTIONIST_LOGIN_HEADER);
        receptionistHeader.setFont(UITheme.LABEL_FONT.deriveFont(Font.BOLD));
        receptionistHeader.setForeground(UITheme.TEXT_COLOR);

        JLabel receptionistCreds = new JLabel(UIConfig.RECEPTIONIST_LOGIN_INFO);
        receptionistCreds.setFont(UITheme.LABEL_FONT.deriveFont(Font.ITALIC));
        receptionistCreds.setForeground(Color.GRAY);

        testInfoPanel.add(physicianHeader);
        testInfoPanel.add(physicianCreds);
        testInfoPanel.add(Box.createVerticalStrut(8));
        testInfoPanel.add(receptionistHeader);
        testInfoPanel.add(receptionistCreds);

        // Add components to form panel
        formPanel.add(emailPanel);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(passPanel);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(errorLabel);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(buttonPanel);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(testInfoPanel);

        // Add panels to right panel
        rightPanel.add(headerPanel, BorderLayout.NORTH);
        rightPanel.add(formPanel, BorderLayout.CENTER);

        // Add panels to frame
        add(imagePanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        // Add keyboard listeners
        setupKeyboardListeners();

        // Add button listeners
        setupButtonListeners(physicianManager, receptionistManager);

        setVisible(true);
    }

    private void setupKeyboardListeners() {
        // Email field enter key
        emailField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    passField.requestFocus();
                }
            }
        });

        // Password field enter key
        passField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    loginBtn.doClick();
                }
            }
        });

        // Add focus listeners for visual feedback
        addFocusListener(emailField);
        addFocusListener(passField);
    }

    private void addFocusListener(JComponent component) {
        component.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (component instanceof JTextField || component instanceof JPasswordField) {
                    component.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UITheme.PRIMARY_COLOR, 2),
                        BorderFactory.createEmptyBorder(8, 10, 8, 10)
                    ));
                }
            }

            public void focusLost(FocusEvent e) {
                if (component instanceof JTextField || component instanceof JPasswordField) {
                    component.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UITheme.ACCENT_LIGHT_COLOR),
                        BorderFactory.createEmptyBorder(8, 10, 8, 10)
                    ));
                }
            }
        });
    }

    private void setupButtonListeners(PhysicianManager physicianManager, ReceptionistManager receptionistManager) {
        loginBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String pass = new String(passField.getPassword());

            if (email.isEmpty() || pass.isEmpty()) {
                errorLabel.setText("Please enter both email and password");
                return;
            }

            PhysicianController physicianController = new PhysicianController(physicianManager);
            ReceptionistController receptionistController = new ReceptionistController(receptionistManager);

            try {
                Physician user = physicianController.login(email, pass);
                dispose();
                controller.showPhysicianApp(user);
                return;
            } catch (InvalidCredentialException ex) {
                try {
                    Receptionist receptionist = receptionistController.login(email, pass);
                    dispose();
                    controller.showReceptionistApp(receptionist);
                    return;
                } catch (InvalidCredentialException rex) {
                    errorLabel.setText(rex.getMessage());
                }
            }
        });

        createBtn.addActionListener(e -> showCreateAccountDialog(physicianManager, receptionistManager));
    }

    private void showCreateAccountDialog(PhysicianManager physicianManager, ReceptionistManager receptionistManager) {
        JDialog dialog = new JDialog(this, UIConfig.CREATE_ACCOUNT_DIALOG_TITLE, true);
        dialog.setLayout(new BorderLayout(20, 20));
        dialog.getContentPane().setBackground(UITheme.BACKGROUND_COLOR);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(UITheme.BACKGROUND_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] userTypes = { "Physician", "Receptionist" };
        JComboBox<String> userTypeCombo = new JComboBox<>(userTypes);
        userTypeCombo.setFont(UITheme.LABEL_FONT);
        userTypeCombo.setBackground(UITheme.BACKGROUND_COLOR);
        userTypeCombo.setForeground(UITheme.TEXT_COLOR);

        JTextField nameField = createStyledTextField();
        JTextField regEmailField = createStyledTextField();
        JPasswordField passwordField = createStyledPasswordField();
        JPasswordField confirmPasswordField = createStyledPasswordField();

        JLabel errorLabel = new JLabel(" ");
        errorLabel.setFont(UITheme.LABEL_FONT.deriveFont(Font.ITALIC));
        errorLabel.setForeground(new Color(220, 53, 69));

        JButton registerBtn = new JButton(UIConfig.REGISTER_BUTTON_TEXT);
        registerBtn.setFont(UITheme.BUTTON_FONT);
        registerBtn.setBackground(UITheme.SUCCESS_BUTTON_COLOR);
        registerBtn.setForeground(UITheme.BACKGROUND_COLOR);
        registerBtn.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerBtn.setOpaque(true);
        registerBtn.setBorderPainted(false);
        UITheme.applyHoverEffect(registerBtn);

        // Add form fields
        addFormField(formPanel, UIConfig.ACCOUNT_TYPE_LABEL, userTypeCombo);
        addFormField(formPanel, UIConfig.NAME_LABEL, nameField);
        addFormField(formPanel, UIConfig.USER_EMAIL_LABEL, regEmailField);
        addFormField(formPanel, UIConfig.USER_PASSWORD_LABEL, passwordField);
        addFormField(formPanel, UIConfig.CONFIRM_PASSWORD_LABEL, confirmPasswordField);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(errorLabel);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(registerBtn);

        // Add keyboard listeners
        setupRegistrationKeyboardListeners(nameField, regEmailField, passwordField, confirmPasswordField, registerBtn);

        // Add focus listeners
        addFocusListener(nameField);
        addFocusListener(regEmailField);
        addFocusListener(passwordField);
        addFocusListener(confirmPasswordField);

        // Add registration action
        registerBtn.addActionListener(ev -> {
            String userType = (String) userTypeCombo.getSelectedItem();
            String name = nameField.getText().trim();
            String email = regEmailField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            CredentialVerification verification = new CredentialVerification(physicianManager, receptionistManager, dialog);
            if (!verification.verifySignUpData(name, email, password, confirmPassword)) {
                errorLabel.setText("Please check all fields and try again");
                return;
            }

            PhysicianController physicianController = new PhysicianController(physicianManager);
            ReceptionistController receptionistController = new ReceptionistController(receptionistManager);

            try {
                if ("Physician".equals(userType)) {
                    Physician newPhysician = physicianController.register(name, email, password, confirmPassword);
                    JOptionPane.showMessageDialog(dialog, UIConfig.SUCCESS_ACCOUNT_CREATED,
                            UIConfig.SUCCESS_DIALOG_TITLE, JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    SwingUtilities.invokeLater(() -> controller.showPhysicianApp(newPhysician));
                } else {
                    Receptionist newReceptionist = receptionistController.register(name, email, password, confirmPassword);
                    JOptionPane.showMessageDialog(dialog, UIConfig.SUCCESS_ACCOUNT_CREATED,
                            UIConfig.SUCCESS_DIALOG_TITLE, JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    SwingUtilities.invokeLater(() -> controller.showReceptionistApp(newReceptionist));
                }
            } catch (InvalidCredentialException ex) {
                errorLabel.setText(ex.getMessage());
            }
        });

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setFont(UITheme.TEXTFIELD_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.ACCENT_LIGHT_COLOR),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setFont(UITheme.TEXTFIELD_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.ACCENT_LIGHT_COLOR),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    private void addFormField(JPanel panel, String labelText, JComponent field) {
        JPanel fieldPanel = new JPanel(new BorderLayout(5, 0));
        fieldPanel.setBackground(UITheme.BACKGROUND_COLOR);
        JLabel label = new JLabel(labelText);
        label.setFont(UITheme.LABEL_FONT);
        label.setForeground(UITheme.TEXT_COLOR);
        fieldPanel.add(label, BorderLayout.NORTH);
        fieldPanel.add(field, BorderLayout.CENTER);
        panel.add(fieldPanel);
        panel.add(Box.createVerticalStrut(15));
    }

    private void setupRegistrationKeyboardListeners(JTextField nameField, JTextField emailField,
            JPasswordField passwordField, JPasswordField confirmPasswordField, JButton registerBtn) {
        nameField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    emailField.requestFocus();
                }
            }
        });

        emailField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    passwordField.requestFocus();
                }
            }
        });

        passwordField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    confirmPasswordField.requestFocus();
                }
            }
        });

        confirmPasswordField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    registerBtn.doClick();
                }
            }
        });
    }
}