package physicianconnect.presentation.receptionist;

import physicianconnect.logic.AvailabilityService;
import physicianconnect.logic.MessageService;
import physicianconnect.logic.controller.AppointmentController;
import physicianconnect.logic.controller.BillingController;
import physicianconnect.logic.controller.MessageController;
import physicianconnect.logic.controller.ReceptionistController;
import physicianconnect.logic.manager.AppointmentManager;
import physicianconnect.logic.manager.InvoiceManager;
import physicianconnect.logic.manager.PaymentManager;
import physicianconnect.logic.manager.PhysicianManager;
import physicianconnect.logic.manager.ReceptionistManager;
import physicianconnect.objects.Appointment;
import physicianconnect.objects.Invoice;
import physicianconnect.objects.Payment;
import physicianconnect.objects.Physician;
import physicianconnect.objects.Receptionist;
import physicianconnect.persistence.PersistenceFactory;
import physicianconnect.presentation.AddAppointmentPanel;
import physicianconnect.presentation.AllPhysiciansDailyPanel;
import physicianconnect.presentation.receptionist.BillingPanel;
import physicianconnect.presentation.DailyAvailabilityPanel;
import physicianconnect.presentation.MessageButton;
import physicianconnect.presentation.MessagePanel;
import physicianconnect.presentation.NotificationBanner;
import physicianconnect.presentation.NotificationButton;
import physicianconnect.presentation.NotificationPanel;
import physicianconnect.presentation.ViewAppointmentPanel;
import physicianconnect.presentation.WeeklyAvailabilityPanel;
import physicianconnect.presentation.config.UIConfig;
import physicianconnect.presentation.config.UITheme;
import physicianconnect.presentation.util.ProfileImageUtil;
import physicianconnect.presentation.util.RevenueSummaryUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ReceptionistApp {
    private final Receptionist loggedIn;
    private final PhysicianManager physicianManager;
    private final AppointmentManager appointmentManager;
    private final ReceptionistManager receptionistManager;
    private final InvoiceManager invoiceManager;
    private final PaymentManager paymentManager;
    private final Runnable logoutCallback;
    private final MessageService messageService;
    private final MessageController messageController;
    private final AppointmentController appointmentController;
    private final ReceptionistController receptionistController;
    private final BillingController billingController;
    private final AvailabilityService availabilityService;

    private JFrame frame;
    private JComboBox<Object> physicianCombo;
    private DefaultTableModel appointmentTableModel;
    private JTable appointmentTable;
    private TableRowSorter<DefaultTableModel> appointmentTableSorter;
    private JTextField appointmentSearchField;
    private DailyAvailabilityPanel dailyPanel;
    private WeeklyAvailabilityPanel weeklyPanel;
    private AllPhysiciansDailyPanel allPhysiciansDailyPanel;
    private LocalDate selectedDate;
    private LocalDate weekStart;
    private JLabel dayLabel;
    private JLabel weekLabel;
    private JPanel dailyContainer;
    private JPanel weeklyContainer;
    private JPanel dayNav;
    private JPanel weekNav;
    private MessageButton messageButton;
    private Timer dateTimeTimer;
    private JButton profilePicButton;

    // Revenue summary fields
    private JPanel revenueSummaryPanel;
    private JPanel revenueSummaryContent;
    private boolean revenueSummaryCollapsed = false;

    private Timer messageRefreshTimer;
    private NotificationPanel notificationPanel;
    private NotificationBanner notificationBanner;
    private JDialog notificationDialog;
    private NotificationButton notificationButton;
    private Timer notificationRefreshTimer;
    private int lastNotifiedUnreadMessageCount = 0;

    public ReceptionistApp(Receptionist loggedIn, PhysicianManager physicianManager,
                           AppointmentManager appointmentManager, ReceptionistManager receptionistManager, 
                           AppointmentController appointmentController, Runnable logoutCallback) {
        this.loggedIn = loggedIn;
        this.physicianManager = physicianManager;
        this.appointmentManager = appointmentManager;
        this.receptionistManager = receptionistManager;
        this.logoutCallback = () -> {
            frame.dispose(); // Dispose the UI window
            if (logoutCallback != null)
                logoutCallback.run();
        };
        this.receptionistController = new ReceptionistController(receptionistManager);
        this.messageService = new MessageService(PersistenceFactory.getMessageRepository());
        this.messageController = new MessageController(messageService);
        // FIX: Use the passed-in appointmentController, not a new one!
        this.appointmentController = appointmentController;
        this.invoiceManager = new InvoiceManager(PersistenceFactory.getInvoicePersistence());
        this.paymentManager = new PaymentManager(PersistenceFactory.getPaymentPersistence());
        this.billingController = new BillingController(invoiceManager, paymentManager);
        this.availabilityService = new AvailabilityService(
                (physicianconnect.persistence.sqlite.AppointmentDB) PersistenceFactory.getAppointmentPersistence());
        
        // Initialize notification panel
        this.notificationPanel = new NotificationPanel(
            PersistenceFactory.getNotificationPersistence(),
            loggedIn.getId(),
            "receptionist"
        );
        this.notificationDialog = new JDialog(frame, "Notifications", false);
        this.notificationDialog.setContentPane(notificationPanel);
        this.notificationDialog.pack();
        this.notificationDialog.setLocationRelativeTo(frame);
        
        // Register appointment callbacks
        appointmentController.setOnAppointmentCreated(this::onAppointmentCreated);
        appointmentController.setOnAppointmentUpdated(this::onAppointmentUpdated);
        appointmentController.setOnAppointmentDeleted(this::onAppointmentDeleted);
        
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame(UIConfig.RECEPTIONIST_DASHBOARD_TITLE + " - " + loggedIn.getName());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout(10, 10));

        // Register for revenue summary updates
        RevenueSummaryUtil.addListener(this::updateRevenueSummary);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent e) {
                RevenueSummaryUtil.removeListener(ReceptionistApp.this::updateRevenueSummary);
            }
        });

        // Top Panel
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(UITheme.BACKGROUND_COLOR);
        topPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel welcome = new JLabel(UIConfig.WELCOME_PREFIX + loggedIn.getName());
        welcome.setFont(UITheme.HEADER_FONT);
        welcome.setForeground(UITheme.TEXT_COLOR);
        topPanel.add(welcome, BorderLayout.WEST);

        // Profile photo button (optional, if you want to add for receptionists)
        ImageIcon profileIcon = ProfileImageUtil.getProfileIcon(loggedIn.getId(), false);
        profilePicButton = new JButton(profileIcon);
        profilePicButton.setToolTipText(UIConfig.PROFILE_BUTTON_TEXT);
        profilePicButton.setPreferredSize(new Dimension(40, 40));
        profilePicButton.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
        profilePicButton.setContentAreaFilled(false);
        profilePicButton.setFocusPainted(false);
        profilePicButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        profilePicButton.addActionListener(e -> openProfileDialog());

        // Physician dropdown
        List<Physician> physicians = physicianManager.getAllPhysicians();
        physicianCombo = new JComboBox<>();
        physicianCombo.addItem(UIConfig.ALL_PHYSICIANS_LABEL);
        for (Physician p : physicians)
            physicianCombo.addItem(p);

        // Date/Time Label
        JLabel dateTimeLabel = new JLabel();
        dateTimeLabel.setFont(UITheme.LABEL_FONT);
        dateTimeLabel.setForeground(UITheme.TEXT_COLOR);
        dateTimeLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        dateTimeTimer = new Timer(1000, e -> {
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            dateTimeLabel.setText(now);
        });
        dateTimeTimer.start();

        // Message Button
        messageButton = new MessageButton();
        messageButton.setOnAction(e -> showMessageDialog());

        // Add notification button
        notificationButton = new NotificationButton();
        notificationButton.setOnAction(e -> showNotificationPanel());

        // Initialize notification refresh timer
        notificationRefreshTimer = new Timer(5000, e -> refreshNotificationCount());
        notificationRefreshTimer.start();

        // Initialize message refresh timer
        messageRefreshTimer = new Timer(5000, e -> refreshMessageCount());
        messageRefreshTimer.start();

        // Right-aligned panel for physician dropdown, date/time, and message button
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(new JLabel(UIConfig.PHYSICIAN_LABEL));
        rightPanel.add(physicianCombo);
        rightPanel.add(dateTimeLabel);
        rightPanel.add(notificationButton);
        rightPanel.add(messageButton);
        rightPanel.add(profilePicButton);

        topPanel.add(rightPanel, BorderLayout.EAST);

        frame.add(topPanel, BorderLayout.NORTH);

        // Appointments Panel
        JPanel appointmentsPanel = new JPanel(new BorderLayout(10, 10));
        appointmentsPanel.setBackground(UITheme.BACKGROUND_COLOR);
        appointmentsPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        JLabel appointmentsTitle = new JLabel(UIConfig.APPOINTMENTS_TITLE);
        appointmentsTitle.setFont(UITheme.HEADER_FONT);
        appointmentsTitle.setForeground(UITheme.TEXT_COLOR);
        appointmentsPanel.add(appointmentsTitle, BorderLayout.NORTH);

        // Add search bar under the title
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.setBackground(UITheme.BACKGROUND_COLOR);
        JLabel searchLabel = new JLabel(UIConfig.SEARCH_PATIENT_LABEL);
        searchLabel.setFont(UITheme.LABEL_FONT);
        appointmentSearchField = new JTextField();
        appointmentSearchField.setFont(UITheme.LABEL_FONT);
        appointmentSearchField.putClientProperty("JTextField.placeholderText", UIConfig.SEARCH_PATIENT_PLACEHOLDER);
        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(appointmentSearchField, BorderLayout.CENTER);
        appointmentsPanel.add(searchPanel, BorderLayout.BEFORE_FIRST_LINE);

        // Table for appointments
        String[] columns = { UIConfig.PATIENT_LABEL, UIConfig.PHYSICIAN_LABEL, UIConfig.DATE_LABEL,
                UIConfig.TIME_LABEL };
        appointmentTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        appointmentTable = new JTable(appointmentTableModel);
        appointmentTable.setFont(UITheme.LABEL_FONT);
        appointmentTable.setRowHeight(28);
        appointmentTable.getTableHeader().setFont(UITheme.HEADER_FONT);
        appointmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appointmentTable.setAutoCreateRowSorter(true);

        appointmentTableSorter = new TableRowSorter<>(appointmentTableModel);
        appointmentTable.setRowSorter(appointmentTableSorter);

        appointmentSearchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filterAppointments();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filterAppointments();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filterAppointments();
            }
        });

        JScrollPane appointmentScroll = new JScrollPane(appointmentTable);
        appointmentScroll.setPreferredSize(new Dimension(600, 300));
        appointmentScroll.setBorder(BorderFactory.createLineBorder(UITheme.PRIMARY_COLOR, 1));
        appointmentsPanel.add(appointmentScroll, BorderLayout.CENTER);

        // --- Revenue Summary Panel (collapsible) ---
        revenueSummaryPanel = new JPanel(new BorderLayout());
        revenueSummaryPanel.setBackground(UITheme.BACKGROUND_COLOR);
        revenueSummaryPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        JButton revenueHeader = new JButton(UIConfig.REVENUE_SUMMARY_HEADER + " " + UIConfig.REVENUE_SUMMARY_EXPANDED);
        revenueHeader.setFont(UITheme.LABEL_FONT);
        revenueHeader.setFocusPainted(false);
        revenueHeader.setContentAreaFilled(false);
        revenueHeader.setBorderPainted(false);
        revenueHeader.setHorizontalAlignment(SwingConstants.LEFT);

        revenueSummaryContent = new JPanel(new BorderLayout());
        revenueSummaryContent.setBackground(UITheme.BACKGROUND_COLOR);

        updateRevenueSummary();

        revenueHeader.addActionListener(e -> {
            revenueSummaryCollapsed = !revenueSummaryCollapsed;
            revenueSummaryContent.setVisible(!revenueSummaryCollapsed);
            revenueHeader.setText(UIConfig.REVENUE_SUMMARY_HEADER + " " +
                (revenueSummaryCollapsed ? UIConfig.REVENUE_SUMMARY_COLLAPSED : UIConfig.REVENUE_SUMMARY_EXPANDED));
            revenueSummaryPanel.revalidate();
        });

        revenueSummaryPanel.add(revenueHeader, BorderLayout.NORTH);
        revenueSummaryPanel.add(revenueSummaryContent, BorderLayout.CENTER);

        appointmentsPanel.add(revenueSummaryPanel, BorderLayout.SOUTH);

        // Calendar Panels
        selectedDate = LocalDate.now();
        weekStart = selectedDate.with(java.time.DayOfWeek.MONDAY);

        dailyPanel = new DailyAvailabilityPanel(
                null, // will be set in updateCalendarPanels
                availabilityService,
                appointmentController,
                selectedDate,
                () -> weeklyPanel.loadWeek(selectedDate.with(java.time.DayOfWeek.MONDAY)));
        weeklyPanel = new WeeklyAvailabilityPanel(
                null, // will be set in updateCalendarPanels
                availabilityService,
                appointmentController,
                weekStart,
                () -> dailyPanel.loadSlotsForDate(dailyPanel.getCurrentDate()));

        // Navigation for calendar
        JButton prevDayBtn = new JButton(UIConfig.PREV_DAY_BUTTON_TEXT);
        JButton nextDayBtn = new JButton(UIConfig.NEXT_DAY_BUTTON_TEXT);
        dayLabel = new JLabel(UIConfig.LABEL_SHOW_DATE + selectedDate);
        dayLabel.setFont(UITheme.LABEL_FONT);
        dayLabel.setForeground(UITheme.TEXT_COLOR);

        prevDayBtn.addActionListener(e -> {
            selectedDate = selectedDate.minusDays(1);
            dailyPanel.loadSlotsForDate(selectedDate);
            dayLabel.setText(UIConfig.LABEL_SHOW_DATE + selectedDate);
        });
        nextDayBtn.addActionListener(e -> {
            selectedDate = selectedDate.plusDays(1);
            dailyPanel.loadSlotsForDate(selectedDate);
            dayLabel.setText(UIConfig.LABEL_SHOW_DATE + selectedDate);
        });

        dayNav = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        dayNav.setBackground(UITheme.BACKGROUND_COLOR);
        dayNav.add(prevDayBtn);
        dayNav.add(dayLabel);
        dayNav.add(nextDayBtn);

        dailyContainer = new JPanel(new BorderLayout());
        dailyContainer.setBackground(UITheme.BACKGROUND_COLOR);
        dailyContainer.add(dayNav, BorderLayout.NORTH);
        dailyContainer.add(new JScrollPane(dailyPanel), BorderLayout.CENTER);

        JButton prevWeekBtn = new JButton(UIConfig.PREV_WEEK_BUTTON_TEXT);
        JButton nextWeekBtn = new JButton(UIConfig.NEXT_WEEK_BUTTON_TEXT);
        weekLabel = new JLabel(UIConfig.LABEL_WEEK_OF + weekStart);
        weekLabel.setFont(UITheme.LABEL_FONT);
        weekLabel.setForeground(UITheme.TEXT_COLOR);

        prevWeekBtn.addActionListener(e -> {
            weekStart = weekStart.minusWeeks(1);
            weeklyPanel.loadWeek(weekStart);
            weekLabel.setText(UIConfig.LABEL_WEEK_OF + weekStart);
        });
        nextWeekBtn.addActionListener(e -> {
            weekStart = weekStart.plusWeeks(1);
            weeklyPanel.loadWeek(weekStart);
            weekLabel.setText(UIConfig.LABEL_WEEK_OF + weekStart);
        });

        weekNav = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        weekNav.setBackground(UITheme.BACKGROUND_COLOR);
        weekNav.add(prevWeekBtn);
        weekNav.add(weekLabel);
        weekNav.add(nextWeekBtn);

        weeklyContainer = new JPanel(new BorderLayout());
        weeklyContainer.setBackground(UITheme.BACKGROUND_COLOR);
        weeklyContainer.add(weekNav, BorderLayout.NORTH);
        weeklyContainer.add(new JScrollPane(weeklyPanel), BorderLayout.CENTER);

        JTabbedPane calendarTabs = new JTabbedPane();
        calendarTabs.setFont(UITheme.LABEL_FONT);
        calendarTabs.addTab(UIConfig.TAB_DAILY_VIEW, dailyContainer);
        calendarTabs.addTab(UIConfig.TAB_WEEKLY_VIEW, weeklyContainer);
        calendarTabs.setPreferredSize(new Dimension(600, 500));

        JSplitPane centerSplit = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                appointmentsPanel,
                calendarTabs);
        centerSplit.setOneTouchExpandable(false);
        frame.add(centerSplit, BorderLayout.CENTER);
        centerSplit.setDividerLocation(600);

        // --- Bottom Button Panel ---
        JPanel buttonPanel = new JPanel(new GridLayout(1, 0, 10, 10));
        buttonPanel.setBackground(UITheme.BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        JButton addAppointmentButton = createStyledButton(UIConfig.ADD_APPOINTMENT_BUTTON_TEXT);
        JButton viewAppointmentButton = createStyledButton(UIConfig.VIEW_APPOINTMENTS_BUTTON_TEXT);
        JButton signOutButton = createStyledButton(UIConfig.LOGOUT_BUTTON_TEXT);
        JButton billingBtn = createStyledButton(UIConfig.BILLING_BUTTON_TEXT);

        buttonPanel.add(addAppointmentButton);
        buttonPanel.add(viewAppointmentButton);
        buttonPanel.add(billingBtn);
        buttonPanel.add(signOutButton);

        // Add listeners for buttons
        addAppointmentButton.addActionListener(e -> {
            Object selected = physicianCombo.getSelectedItem();
            Physician selectedPhysician = (selected instanceof Physician) ? (Physician) selected : null;
            if (selectedPhysician == null) {
                JOptionPane.showMessageDialog(frame, UIConfig.ERROR_NO_PHYSICIAN_SELECTED,
                        UIConfig.ERROR_DIALOG_TITLE, JOptionPane.WARNING_MESSAGE);
                return;
            }
            AddAppointmentPanel dlg = new AddAppointmentPanel(
                    frame,
                    appointmentController,
                    selectedPhysician.getId(),
                    this::updateAppointments);
            dlg.setVisible(true);
        });

        viewAppointmentButton.addActionListener(e -> {
            int selectedRow = appointmentTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(frame, UIConfig.ERROR_NO_APPOINTMENT_SELECTED,
                        UIConfig.ERROR_DIALOG_TITLE, JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            int modelRow = appointmentTable.convertRowIndexToModel(selectedRow);
            String patientName = (String) appointmentTableModel.getValueAt(modelRow, 0);
            String physicianName = (String) appointmentTableModel.getValueAt(modelRow, 1);
            String dateStr = (String) appointmentTableModel.getValueAt(modelRow, 2);
            String timeStr = (String) appointmentTableModel.getValueAt(modelRow, 3);

            // Find the matching Appointment object
            List<Appointment> allAppointments = physicianManager.getAllPhysicians().stream()
                    .flatMap(p -> appointmentManager.getAppointmentsForPhysician(p.getId()).stream())
                    .collect(Collectors.toList());
            Appointment selectedAppt = null;
            for (Appointment a : allAppointments) {
                Physician p = physicianManager.getPhysicianById(a.getPhysicianId());
                String pName = (p != null) ? p.getName() : UIConfig.UNKNOWN_PHYSICIAN_LABEL;
                String date = a.getDateTime().format(DateTimeFormatter.ofPattern(UIConfig.DATE_FORMAT));
                String time = a.getDateTime().format(DateTimeFormatter.ofPattern(UIConfig.TIME_FORMAT));
                if (a.getPatientName().equals(patientName) && pName.equals(physicianName)
                        && date.equals(dateStr) && time.equals(timeStr)) {
                    selectedAppt = a;
                    break;
                }
            }
            if (selectedAppt == null) {
                JOptionPane.showMessageDialog(frame, UIConfig.ERROR_APPOINTMENT_NOT_FOUND,
                        UIConfig.ERROR_DIALOG_TITLE, JOptionPane.ERROR_MESSAGE);
                return;
            }
            ViewAppointmentPanel viewDlg = new ViewAppointmentPanel(
                    frame,
                    appointmentController,
                    selectedAppt,
                    this::updateAppointments);
            viewDlg.setVisible(true);
        });

        billingBtn.addActionListener(e -> {
            BillingPanel billingPanel = new BillingPanel(
                billingController,
                appointmentController,
                notificationPanel,
                PersistenceFactory.getNotificationPersistence()
            );
            JDialog billingDialog = new JDialog(frame, UIConfig.BILLING_DIALOG_TITLE, true);
            billingDialog.setContentPane(billingPanel);
            billingDialog.setSize(800, 600);
            billingDialog.setLocationRelativeTo(frame);
            billingDialog.setVisible(true);
            // Revenue summary will auto-update via listener
        });

        signOutButton.addActionListener(e -> {
            frame.dispose();
            if (logoutCallback != null)
                logoutCallback.run();
        });

        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Listeners
        physicianCombo.addActionListener(e -> {
            updateAppointments();
            updateCalendarPanels();
        });

        // Initial load
        updateAppointments();
        updateCalendarPanels();

        appointmentManager.addChangeListener(this::updateAppointments);

        frame.setVisible(true);
    }

private void updateAppointments() {
    Object selected = physicianCombo.getSelectedItem();
    Physician selectedPhysician = (selected instanceof Physician) ? (Physician) selected : null;
    List<Appointment> allAppointments;
    if (selectedPhysician == null) {
        allAppointments = physicianManager.getAllPhysicians().stream()
                .flatMap(p -> appointmentManager.getAppointmentsForPhysician(p.getId()).stream())
                .collect(Collectors.toList());
    } else {
        allAppointments = appointmentManager.getAppointmentsForPhysician(selectedPhysician.getId());
    }
    appointmentTableModel.setRowCount(0);
    List<Appointment> filtered = allAppointments.stream()
            .sorted((a, b) -> a.getDateTime().compareTo(b.getDateTime()))
            .collect(Collectors.toList());
    for (Appointment a : filtered) {
        Physician p = physicianManager.getPhysicianById(a.getPhysicianId());
        String physicianName = (p != null) ? p.getName() : UIConfig.UNKNOWN_PHYSICIAN_LABEL;
        String date = a.getDateTime().format(DateTimeFormatter.ofPattern(UIConfig.DATE_FORMAT));
        String time = a.getDateTime().format(DateTimeFormatter.ofPattern(UIConfig.TIME_FORMAT));
        appointmentTableModel.addRow(new Object[] {
                a.getPatientName(),
                physicianName,
                date,
                time
        });
    }
}

private void filterAppointments() {
    String text = appointmentSearchField.getText();
    if (text.trim().length() == 0) {
        appointmentTableSorter.setRowFilter(null);
    } else {
        appointmentTableSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 0)); // 0 = Patient Name column
    }
}

private void updateCalendarPanels() {
    Object selected = physicianCombo.getSelectedItem();
    boolean allPhysiciansSelected = selected instanceof String && UIConfig.ALL_PHYSICIANS_LABEL.equals(selected);
    Physician selectedPhysician = (selected instanceof Physician) ? (Physician) selected : null;
    String currentPhysicianId = (selectedPhysician != null) ? selectedPhysician.getId() : null;

    // Remove old panels
    dailyContainer.removeAll();
    weeklyContainer.removeAll();

    // Remove all tabs and add only the relevant ones
    JTabbedPane calendarTabs = null;
    for (Component comp : frame.getContentPane().getComponents()) {
        if (comp instanceof JSplitPane split) {
            Component right = split.getRightComponent();
            if (right instanceof JTabbedPane tabs) {
                calendarTabs = tabs;
                tabs.removeAll();
                break;
            }
        }
    }

    if (allPhysiciansSelected) {
        // Show only daily view for all physicians
        allPhysiciansDailyPanel = new AllPhysiciansDailyPanel(
                physicianManager,
                appointmentController,
                availabilityService,
                selectedDate,
                newDate -> {
                    selectedDate = newDate;
                    updateCalendarPanels();
                });
        if (calendarTabs != null) {
            calendarTabs.addTab(UIConfig.TAB_DAILY_VIEW, allPhysiciansDailyPanel);
        }
    } else {
        // Show daily and weekly for a specific physician
        dailyPanel = new DailyAvailabilityPanel(
                currentPhysicianId,
                availabilityService,
                appointmentController,
                selectedDate,
                () -> weeklyPanel.loadWeek(selectedDate.with(java.time.DayOfWeek.MONDAY)));
        weeklyPanel = new WeeklyAvailabilityPanel(
                currentPhysicianId,
                availabilityService,
                appointmentController,
                weekStart,
                () -> dailyPanel.loadSlotsForDate(dailyPanel.getCurrentDate()));

        dailyContainer.add(dayNav, BorderLayout.NORTH);
        dailyContainer.add(new JScrollPane(dailyPanel), BorderLayout.CENTER);

        weeklyContainer.add(weekNav, BorderLayout.NORTH);
        weeklyContainer.add(new JScrollPane(weeklyPanel), BorderLayout.CENTER);

        if (calendarTabs != null) {
            calendarTabs.addTab(UIConfig.TAB_DAILY_VIEW, dailyContainer);
            calendarTabs.addTab(UIConfig.TAB_WEEKLY_VIEW, weeklyContainer);
        }
    }

    // Update labels
    dayLabel.setText(UIConfig.LABEL_SHOW_DATE + selectedDate);
    weekLabel.setText(UIConfig.LABEL_WEEK_OF + weekStart);

    // Refresh UI
    if (calendarTabs != null) {
        calendarTabs.revalidate();
        calendarTabs.repaint();
    }
}

    private void showNotificationPanel() {
        if (notificationDialog == null) {
            notificationDialog = new JDialog(frame, "Notifications", false);
            notificationPanel = new NotificationPanel(
                PersistenceFactory.getNotificationPersistence(),
                loggedIn.getId(),
                "receptionist"
            );
            notificationDialog.setContentPane(notificationPanel);
            notificationDialog.pack();
            notificationDialog.setLocationRelativeTo(frame);
        }
        notificationDialog.setVisible(true);
        // Mark all notifications as read when panel is opened
        notificationPanel.showNotificationPanel();
        notificationButton.updateNotificationCount(0);
        lastNotifiedUnreadMessageCount = 0;
    }

    private void showNotificationBanner(String message, java.awt.event.ActionListener onClick) {
        // Only show banner if the frame is visible (user is logged in)
        if (frame != null && frame.isVisible()) {
            if (notificationBanner == null) {
                notificationBanner = new NotificationBanner(frame);
            }
            notificationBanner.show(message, onClick);
        }
    }

    private void updateRevenueSummary() {
        revenueSummaryContent.removeAll();
        List<Invoice> invoices = billingController.getAllInvoices();
        JPanel summary = RevenueSummaryUtil.createSummaryPanel(invoices);
        revenueSummaryContent.add(summary, BorderLayout.CENTER);
        revenueSummaryContent.setVisible(!revenueSummaryCollapsed);
        revenueSummaryContent.revalidate();
        revenueSummaryContent.repaint();
    }

    private void showMessageDialog() {
        JDialog dialog = new JDialog(frame, UIConfig.MESSAGES_DIALOG_TITLE, true);

        // Combine physicians and receptionists into one list
        List<Object> allUsers = new java.util.ArrayList<>();
        allUsers.addAll(physicianManager.getAllPhysicians());
        allUsers.addAll(receptionistManager.getAllReceptionists());

        MessagePanel messagePanel = new MessagePanel(messageController, loggedIn.getId(), "receptionist", allUsers);
        dialog.setContentPane(messagePanel);
        dialog.pack();
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
        refreshMessageCount();
    }

    private void refreshMessageCount() {
        int unreadCount = messageService.getUnreadMessageCount(loggedIn.getId(), "receptionist");
        messageButton.updateNotificationCount(unreadCount);
        
        // Only show banner for new unread messages
        if (unreadCount > lastNotifiedUnreadMessageCount) {
            // Find the latest unread message
            List<physicianconnect.objects.Message> unreadMessages = messageService.getUnreadMessagesForUser(loggedIn.getId(), "receptionist");
            if (!unreadMessages.isEmpty()) {
                physicianconnect.objects.Message latest = unreadMessages.get(unreadMessages.size() - 1);
                String senderType = latest.getSenderType();
                String senderName = "";
                
                if (senderType.equals("physician")) {
                    senderName = physicianManager.getPhysicianById(latest.getSenderId()).getName();
                } else if (senderType.equals("receptionist")) {
                    senderName = receptionistManager.getReceptionistById(latest.getSenderId()).getName();
                }
                
                String notificationMsg = "New message received from " + senderName + " (" + senderType + ")";
                showNotificationBanner(notificationMsg, e -> showMessageDialog());
                if (notificationPanel != null) {
                    notificationPanel.addNotification(notificationMsg, "Message");
                }
            }
        }
        lastNotifiedUnreadMessageCount = unreadCount;
    }

    private void refreshNotificationCount() {
        if (notificationPanel != null) {
            int count = notificationPanel.getUnreadNotificationCount();
            if (count != lastNotifiedUnreadMessageCount) {
                notificationButton.updateNotificationCount(count);
                lastNotifiedUnreadMessageCount = count;
            }
        }
    }

    private void notifyAppointmentChange(String message, String type) {
        // Always add to notification panel for persistence
        if (notificationPanel == null) {
            notificationPanel = new NotificationPanel(
                PersistenceFactory.getNotificationPersistence(),
                loggedIn.getId(),
                "receptionist"
            );
        }
        notificationPanel.addNotification(message, type);
        // Update notification count immediately
        notificationButton.updateNotificationCount(notificationPanel.getUnreadNotificationCount());

        // Only show banner if user is logged in
        if (frame != null && frame.isVisible()) {
            showNotificationBanner(message, e -> {
                // Refresh the calendar views
                if (dailyPanel != null) {
                    dailyPanel.revalidate();
                    dailyPanel.repaint();
                }
                if (weeklyPanel != null) {
                    weeklyPanel.revalidate();
                    weeklyPanel.repaint();
                }
                updateAppointments();
            });
        }
    }

    // Add this method to handle appointment updates from the controller
    public void onAppointmentUpdated(Appointment appointment) {
        String physicianName = physicianManager.getPhysicianById(appointment.getPhysicianId()).getName();
        String message = String.format("Appointment notes for %s and %s has been updated.", 
            physicianName,
            appointment.getPatientName());
        notifyAppointmentChange(message, "Appointment Update!");
        
        // Notify the physician about the update
        Physician physician = physicianManager.getPhysicianById(appointment.getPhysicianId());
        if (physician != null) {
            String physicianMessage = String.format("Appointment with %s has been updated.", 
                appointment.getPatientName());
            
            // Create a new notification panel for the physician to store the notification
            NotificationPanel physicianNotificationPanel = new NotificationPanel(
                PersistenceFactory.getNotificationPersistence(),
                physician.getId(),
                "physician"
            );
            physicianNotificationPanel.addNotification(physicianMessage, "Appointment Update!");
        }
    }

    // Add this method to handle appointment deletions from the controller
    public void onAppointmentDeleted(Appointment appointment) {
        String physicianName = physicianManager.getPhysicianById(appointment.getPhysicianId()).getName();
        String message = String.format("Appointment for %s and %s has been deleted.", 
            physicianName,
            appointment.getPatientName());
        notifyAppointmentChange(message, "Appointment Cancellation!");
        
        // Notify the physician about the deletion
        Physician physician = physicianManager.getPhysicianById(appointment.getPhysicianId());
        if (physician != null) {
            String physicianMessage = String.format("Appointment with %s has been cancelled.", 
                appointment.getPatientName());
            
            // Create a new notification panel for the physician to store the notification
            NotificationPanel physicianNotificationPanel = new NotificationPanel(
                PersistenceFactory.getNotificationPersistence(),
                physician.getId(),
                "physician"
            );
            physicianNotificationPanel.addNotification(physicianMessage, "Appointment Cancellation!");
        }
    }

    // Add this method to handle new appointments from the controller
    public void onAppointmentCreated(Appointment appointment) {
        String physicianName = physicianManager.getPhysicianById(appointment.getPhysicianId()).getName();
        String message = String.format("New appointment set for %s and %s.", 
            physicianName,
            appointment.getPatientName());
        notifyAppointmentChange(message, "New Appointment!");
        
        // Notify the physician about the new appointment
        Physician physician = physicianManager.getPhysicianById(appointment.getPhysicianId());
        if (physician != null) {
            String physicianMessage = String.format("New appointment scheduled with %s.", 
                appointment.getPatientName());
            
            // Create a new notification panel for the physician to store the notification
            NotificationPanel physicianNotificationPanel = new NotificationPanel(
                PersistenceFactory.getNotificationPersistence(),
                physician.getId(),
                "physician"
            );
            physicianNotificationPanel.addNotification(physicianMessage, "New Appointment!");
        }
    }

    private JButton createStyledButton(String txt) {
        JButton b = new JButton(txt);
        b.setFont(UITheme.BUTTON_FONT);
        b.setForeground(UITheme.BACKGROUND_COLOR);
        b.setBackground(UITheme.PRIMARY_COLOR);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        UITheme.applyHoverEffect(b);
        return b;
    }

    private void openProfileDialog() {
        JDialog dlg = new JDialog(frame, UIConfig.PROFILE_DIALOG_TITLE, true);

        ReceptionistProfilePanel panel = new ReceptionistProfilePanel(
                loggedIn,
                receptionistManager,
                () -> {
                    dlg.dispose(); // Close dialog on logout
                    if (logoutCallback != null)
                        logoutCallback.run();
                },
                () -> {
                    // Fetch updated receptionist info
                    Receptionist refreshed = receptionistManager.getReceptionistById(loggedIn.getId());
                    if (refreshed != null) {
                        // Update frame title and welcome text
                        frame.setTitle(UIConfig.RECEPTIONIST_DASHBOARD_TITLE + " - " + refreshed.getName());
                        for (Component comp : frame.getContentPane().getComponents()) {
                            if (comp instanceof JPanel topPanel) {
                                for (Component c : topPanel.getComponents()) {
                                    if (c instanceof JLabel label
                                            && label.getText().startsWith(UIConfig.WELCOME_PREFIX)) {
                                        label.setText(UIConfig.WELCOME_PREFIX + refreshed.getName());
                                    }
                                }
                            }
                        }

                        // Update profile icon
                        ImageIcon updatedIcon = ProfileImageUtil.getProfileIcon(refreshed.getId(), false);
                        profilePicButton.setIcon(updatedIcon);
                    }
                });

        dlg.setContentPane(panel);
        dlg.pack();
        dlg.setLocationRelativeTo(frame);
        dlg.setVisible(true);
    }
}