package physicianconnect.presentation.receptionist;

import physicianconnect.logic.controller.BillingController;
import physicianconnect.logic.manager.InvoiceNotificationManager;
import physicianconnect.logic.controller.AppointmentController;
import physicianconnect.logic.validation.BillingValidator;
import physicianconnect.objects.Appointment;
import physicianconnect.objects.Invoice;
import physicianconnect.objects.ServiceItem;
import physicianconnect.objects.Payment;
import physicianconnect.presentation.config.UIConfig;
import physicianconnect.presentation.config.UITheme;
import physicianconnect.presentation.util.InvoiceExportUtil;
import physicianconnect.presentation.util.RevenueSummaryUtil;
import physicianconnect.presentation.NotificationPanel;
import physicianconnect.persistence.interfaces.NotificationPersistence;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;

public class BillingPanel extends JPanel {
    private static final String[] SERVICE_TYPES = { "Consult", "Lab", "XRay", "ECG", "Ultrasound" };
    private static final double[] SERVICE_DEFAULTS = { 100, 50, 75, 60, 120 };
    private static final String[] INSURANCE_TYPES = { "No Insurance", "Blue Cross", "Sun Life", "GMS Health" };
    private static final String[] PAYMENT_METHODS = { "Cash", "Card", "Cheque", "e-Transfer" };

    private final BillingController billingController;
    private final AppointmentController appointmentController;
    private final DefaultTableModel model;
    private final JTable invoiceTable;
    private final TableRowSorter<DefaultTableModel> sorter;
    private final JTextField searchField;
    private final InvoiceNotificationManager notificationManager;

    // For keeping the invoice dialog open and refreshing content
    private JDialog invoiceDialog;
    private JPanel invoiceContentPanel;

    public BillingPanel(BillingController billingController, AppointmentController appointmentController,
                       NotificationPanel notificationPanel, NotificationPersistence notificationPersistence) {
        this.billingController = billingController;
        this.appointmentController = appointmentController;
        this.notificationManager = new InvoiceNotificationManager(SwingUtilities.getWindowAncestor(this), 
                                                                notificationPanel, notificationPersistence);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(UITheme.BACKGROUND_COLOR);

        // Top bar with buttons and search
        JButton newInvoiceBtn = new JButton(UIConfig.NEW_INVOICE_BUTTON_TEXT);
        JButton revenueSummaryBtn = new JButton(UIConfig.REVENUE_SUMMARY_BUTTON_TEXT);
        styleButton(newInvoiceBtn);
        styleButton(revenueSummaryBtn);

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        topBar.setBackground(UITheme.BACKGROUND_COLOR);
        topBar.add(newInvoiceBtn);
        topBar.add(revenueSummaryBtn);

        searchField = new JTextField(20);
        searchField.setFont(UITheme.LABEL_FONT);
        searchField.setToolTipText(UIConfig.SEARCH_PATIENT_PLACEHOLDER);
        topBar.add(Box.createHorizontalStrut(30));
        topBar.add(new JLabel(UIConfig.SEARCH_PATIENT_LABEL));
        topBar.add(searchField);

        add(topBar, BorderLayout.NORTH);

        // Table setup
        model = new DefaultTableModel(new Object[] {
                UIConfig.PATIENT_NAME_LABEL,
                UIConfig.TOTAL_LABEL,
                UIConfig.BALANCE_LABEL,
                UIConfig.STATUS_LABEL
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        invoiceTable = new JTable(model);
        invoiceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        invoiceTable.setFont(UITheme.LABEL_FONT);
        invoiceTable.setRowHeight(32);
        invoiceTable.setShowGrid(false);
        invoiceTable.setIntercellSpacing(new Dimension(0, 0));
        invoiceTable.getTableHeader().setFont(UITheme.HEADER_FONT);
        invoiceTable.getTableHeader().setBackground(UITheme.PRIMARY_COLOR);
        invoiceTable.getTableHeader().setForeground(Color.WHITE);
        invoiceTable.setSelectionBackground(UITheme.ACCENT_LIGHT_COLOR);

        // Status color renderer
        invoiceTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = value.toString();
                if ("Paid".equalsIgnoreCase(status))
                    c.setBackground(new Color(200, 255, 200));
                else if ("Partial".equalsIgnoreCase(status))
                    c.setBackground(new Color(255, 255, 180));
                else
                    c.setBackground(new Color(255, 200, 200));
                if (isSelected)
                    c.setBackground(UITheme.PRIMARY_COLOR);
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });

        sorter = new TableRowSorter<>(model);
        invoiceTable.setRowSorter(sorter);

        refreshInvoices();

        JScrollPane tableScroll = new JScrollPane(invoiceTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(UITheme.ACCENT_LIGHT_COLOR, 2, true));
        add(tableScroll, BorderLayout.CENTER);

        // Listeners
        newInvoiceBtn.addActionListener(e -> showNewInvoiceDialog());
        revenueSummaryBtn.addActionListener(
                e -> RevenueSummaryUtil.showRevenueSummary(this, billingController.getAllInvoices()));

        invoiceTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && invoiceTable.getSelectedRow() != -1) {
                int viewRow = invoiceTable.getSelectedRow();
                int modelRow = invoiceTable.convertRowIndexToModel(viewRow);
                String patientName = (String) model.getValueAt(modelRow, 0);
                Invoice invoice = billingController.getAllInvoices().stream()
                        .filter(inv -> inv.getPatientName().equals(patientName))
                        .findFirst().orElse(null);
                if (invoice != null)
                    showInvoiceDetail(invoice, billingController.getPaymentsByInvoice(invoice.getId()));
            }
        });

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            private void filter() {
                String text = searchField.getText();
                if (text.trim().isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 0));
                }
            }
        });
    }

    private void styleButton(JButton btn) {
        btn.setFont(UITheme.BUTTON_FONT);
        btn.setBackground(UITheme.PRIMARY_COLOR);
        btn.setForeground(UITheme.BACKGROUND_COLOR);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
    }

    private void refreshInvoices() {
        model.setRowCount(0);
        List<Invoice> invoices = billingController.getAllInvoices();
        for (Invoice inv : invoices) {
            model.addRow(new Object[] {
                    inv.getPatientName(),
                    inv.getTotalAmount(),
                    inv.getBalance(),
                    inv.getStatus()
            });
        }
        invoiceTable.clearSelection();
    }

    private void showNewInvoiceDialog() {
        List<Appointment> appointments = appointmentController.getAllAppointments();
        JComboBox<Appointment> appointmentBox = new JComboBox<>(appointments.toArray(new Appointment[0]));
        appointmentBox.setFont(UITheme.LABEL_FONT);
        appointmentBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,
                        cellHasFocus);
                if (value instanceof Appointment) {
                    Appointment appt = (Appointment) value;
                    label.setText(appt.getPatientName() + " (" + appt.getDateTime().toLocalDate() + " "
                            + appt.getDateTime().toLocalTime().toString().substring(0, 5) + ")");
                }
                label.setFont(UITheme.LABEL_FONT);
                return label;
            }
        });

        JTextField patientNameField = new JTextField();
        patientNameField.setEditable(false);

        // Search for appointments by patient name (above dropdown)
        JTextField apptSearchField = new JTextField(15);
        apptSearchField.setFont(UITheme.LABEL_FONT);
        JPanel apptSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        apptSearchPanel.setBackground(UITheme.BACKGROUND_COLOR);
        apptSearchPanel.add(new JLabel(UIConfig.SEARCH_PATIENT_LABEL));
        apptSearchPanel.add(apptSearchField);

        apptSearchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            private void filter() {
                String text = apptSearchField.getText().toLowerCase();
                appointmentBox.removeAllItems();
                appointments.stream()
                        .filter(a -> a.getPatientName().toLowerCase().contains(text))
                        .forEach(appointmentBox::addItem);
            }
        });

        // Auto-fill patient name
        appointmentBox.addActionListener(e -> {
            Appointment selected = (Appointment) appointmentBox.getSelectedItem();
            if (selected != null)
                patientNameField.setText(selected.getPatientName());
        });
        if (!appointments.isEmpty())
            patientNameField.setText(appointments.get(0).getPatientName());

        // Service selection
        JButton selectServicesBtn = new JButton(UIConfig.SELECT_SERVICES_BUTTON_TEXT);
        styleButton(selectServicesBtn);
        JLabel selectedServicesLabel = new JLabel(UIConfig.NO_SERVICES_SELECTED_LABEL);
        selectedServicesLabel.setFont(UITheme.LABEL_FONT);
        List<ServiceItem>[] selectedServices = new List[] { null };
        selectServicesBtn.addActionListener(e -> {
            List<ServiceItem> services = showServiceSelectionDialog();
            if (services != null && !services.isEmpty()) {
                selectedServices[0] = services;
                selectedServicesLabel.setText(services.size() + " " + UIConfig.SERVICES_SELECTED_LABEL);
            }
        });

        // Insurance
        JComboBox<String> insuranceBox = new JComboBox<>(INSURANCE_TYPES);
        insuranceBox.setFont(UITheme.LABEL_FONT);
        JTextField insuranceAdjField = new JTextField("0");
        insuranceAdjField.setFont(UITheme.LABEL_FONT);
        insuranceBox.addActionListener(e -> {
            boolean noInsurance = "No Insurance".equals(insuranceBox.getSelectedItem());
            insuranceAdjField.setEditable(!noInsurance);
            if (noInsurance)
                insuranceAdjField.setText("0");
        });

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(UITheme.BACKGROUND_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        // Align all labels and fields left
        apptSearchPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        appointmentBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        patientNameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        selectServicesBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        selectedServicesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        insuranceBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        insuranceAdjField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel apptLabel = new JLabel(UIConfig.APPOINTMENT_LABEL);
        apptLabel.setFont(UITheme.LABEL_FONT);
        apptLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel patientLabel = new JLabel(UIConfig.PATIENT_NAME_LABEL);
        patientLabel.setFont(UITheme.LABEL_FONT);
        patientLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel insuranceTypeLabel = new JLabel(UIConfig.INSURANCE_TYPE_LABEL);
        insuranceTypeLabel.setFont(UITheme.LABEL_FONT);
        insuranceTypeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel insuranceAdjLabel = new JLabel(UIConfig.INSURANCE_ADJUSTMENT_LABEL);
        insuranceAdjLabel.setFont(UITheme.LABEL_FONT);
        insuranceAdjLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        formPanel.add(apptSearchPanel);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(apptLabel);
        formPanel.add(appointmentBox);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(patientLabel);
        formPanel.add(patientNameField);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(selectServicesBtn);
        formPanel.add(selectedServicesLabel);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(insuranceTypeLabel);
        formPanel.add(insuranceBox);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(insuranceAdjLabel);
        formPanel.add(insuranceAdjField);

        int result = JOptionPane.showConfirmDialog(this, formPanel, "Create New Invoice",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            Appointment selectedAppointment = (Appointment) appointmentBox.getSelectedItem();
            if (selectedAppointment != null) {
                if (selectedServices[0] == null || selectedServices[0].isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please select at least one service.",
                            UIConfig.ERROR_DIALOG_TITLE, JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    double insuranceAdj = Double.parseDouble(insuranceAdjField.getText().trim());
                    if (insuranceAdj < 0) {
                        throw new NumberFormatException();
                    }

                    String appointmentId = String.valueOf(selectedAppointment.getId());
                    String patientName = selectedAppointment.getPatientName();
                    
                    billingController.createInvoice(appointmentId, patientName, selectedServices[0], insuranceAdj);
                    notificationManager.notifyInvoiceCreated(patientName);
                    refreshInvoices();
                    RevenueSummaryUtil.fireRevenueSummaryChanged();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid insurance adjustment amount.",
                            UIConfig.ERROR_DIALOG_TITLE, JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private List<ServiceItem> showServiceSelectionDialog() {
        JPanel panel = new JPanel(new GridLayout(SERVICE_TYPES.length, 3, 5, 5));
        panel.setBackground(UITheme.BACKGROUND_COLOR);
        JCheckBox[] boxes = new JCheckBox[SERVICE_TYPES.length];
        JTextField[] fields = new JTextField[SERVICE_TYPES.length];
        for (int i = 0; i < SERVICE_TYPES.length; i++) {
            boxes[i] = new JCheckBox(SERVICE_TYPES[i]);
            fields[i] = new JTextField(String.valueOf(SERVICE_DEFAULTS[i]));
            fields[i].setFont(UITheme.LABEL_FONT);
            panel.add(boxes[i]);
            panel.add(new JLabel("$"));
            panel.add(fields[i]);
        }
        int result = JOptionPane.showConfirmDialog(this, panel, UIConfig.SELECT_SERVICES_DIALOG_TITLE,
                JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            List<ServiceItem> items = new ArrayList<>();
            for (int i = 0; i < SERVICE_TYPES.length; i++) {
                if (boxes[i].isSelected()) {
                    try {
                        double amt = Double.parseDouble(fields[i].getText().trim());
                        if (amt <= 0)
                            throw new NumberFormatException();
                        items.add(new ServiceItem(SERVICE_TYPES[i], amt));
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, UIConfig.ERROR_INVALID_AMOUNT + SERVICE_TYPES[i],
                                UIConfig.ERROR_DIALOG_TITLE, JOptionPane.ERROR_MESSAGE);
                        return null;
                    }
                }
            }
            return items;
        }
        return null;
    }

    // --- Dialog-based invoice detail with refreshable content ---
    private void showInvoiceDetail(Invoice invoice, List<Payment> payments) {
        if (invoiceDialog == null) {
            invoiceDialog = new JDialog(SwingUtilities.getWindowAncestor(this), UIConfig.INVOICE_DETAILS_DIALOG_TITLE,
                    Dialog.ModalityType.APPLICATION_MODAL);
            invoiceContentPanel = new JPanel(new BorderLayout());
            invoiceDialog.setContentPane(invoiceContentPanel);
            invoiceDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        }
        invoiceContentPanel.removeAll();

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(UITheme.BACKGROUND_COLOR);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(18, 28, 18, 28));

        // Patient name (header)
        JLabel patientLabel = new JLabel(invoice.getPatientName());
        patientLabel.setFont(UITheme.HEADER_FONT.deriveFont(Font.BOLD, 20f));
        patientLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(patientLabel);

        infoPanel.add(Box.createVerticalStrut(8));

        // Appointment date/time
        String apptDateTime = "";
        try {
            Appointment appt = appointmentController.getAllAppointments().stream()
                    .filter(a -> String.valueOf(a.getId()).equals(invoice.getAppointmentId()))
                    .findFirst().orElse(null);
            if (appt != null) {
                apptDateTime = appt.getDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            }
        } catch (Exception e) {
            System.out.println("Error getting appointment date: " + e.getMessage());
        }

        JLabel apptLabel = new JLabel(UIConfig.APPOINTMENT_LABEL + (apptDateTime.isEmpty() ? "-" : apptDateTime));
        apptLabel.setFont(UITheme.LABEL_FONT);
        apptLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(apptLabel);

        infoPanel.add(Box.createVerticalStrut(10));

        // Services
        JLabel servicesLabel = new JLabel(UIConfig.SERVICES_LABEL);
        servicesLabel.setFont(UITheme.LABEL_FONT.deriveFont(Font.BOLD));
        servicesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(servicesLabel);

        JPanel servicesPanel = new JPanel();
        servicesPanel.setLayout(new BoxLayout(servicesPanel, BoxLayout.Y_AXIS));
        servicesPanel.setBackground(UITheme.BACKGROUND_COLOR);
        
        for (ServiceItem s : invoice.getServices()) {
            JLabel serviceLine = new JLabel("   • " + s.getName() + ": $" + String.format("%.2f", s.getCost()));
            serviceLine.setFont(UITheme.LABEL_FONT);
            serviceLine.setAlignmentX(Component.LEFT_ALIGNMENT);
            servicesPanel.add(serviceLine);
        }

        servicesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(servicesPanel);

        // Payment History
        if (payments != null && !payments.isEmpty()) {
            JLabel paymentsLabel = new JLabel("Payments:");
            paymentsLabel.setFont(UITheme.LABEL_FONT.deriveFont(Font.BOLD));
            paymentsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            infoPanel.add(Box.createVerticalStrut(10));
            infoPanel.add(paymentsLabel);

            JPanel paymentsPanel = new JPanel();
            paymentsPanel.setLayout(new BoxLayout(paymentsPanel, BoxLayout.Y_AXIS));
            paymentsPanel.setBackground(UITheme.BACKGROUND_COLOR);

            for (Payment p : payments) {
                JLabel paymentLine = new JLabel(
                        String.format("   • %s: $%.2f via %s",
                                p.getPaidAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                                p.getAmount(),
                                p.getMethod()));
                paymentLine.setFont(UITheme.LABEL_FONT);
                paymentLine.setAlignmentX(Component.LEFT_ALIGNMENT);
                paymentsPanel.add(paymentLine);
            }
            paymentsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            infoPanel.add(paymentsPanel);
        }

        infoPanel.add(Box.createVerticalStrut(12));

        // Key-value grid for details
        JPanel grid = new JPanel(new GridLayout(0, 2, 8, 6));
        grid.setBackground(UITheme.BACKGROUND_COLOR);

        Font detailFont = UITheme.LABEL_FONT;

        JLabel insuranceLabel = new JLabel(UIConfig.INSURANCE_ADJUSTMENT_LABEL);
        insuranceLabel.setFont(detailFont);
        JLabel insuranceValue = new JLabel("$" + String.format("%.2f", invoice.getInsuranceAdjustment()));
        insuranceValue.setFont(detailFont);

        JLabel totalLabel = new JLabel(UIConfig.TOTAL_LABEL);
        totalLabel.setFont(detailFont);
        JLabel totalValue = new JLabel("$" + String.format("%.2f", invoice.getTotalAmount()));
        totalValue.setFont(detailFont);

        JLabel balanceLabel = new JLabel(UIConfig.BALANCE_LABEL);
        balanceLabel.setFont(detailFont);
        JLabel balanceValue = new JLabel("$" + String.format("%.2f", invoice.getBalance()));
        balanceValue.setFont(detailFont);

        if (invoice.getBalance() > 0)
            balanceValue.setForeground(Color.RED);

        JLabel statusLabel = new JLabel(UIConfig.STATUS_LABEL);
        statusLabel.setFont(detailFont);
        JLabel statusValue = new JLabel(invoice.getStatus());
        statusValue.setFont(detailFont.deriveFont(Font.BOLD));

        if ("Paid".equalsIgnoreCase(invoice.getStatus()))
            statusValue.setForeground(new Color(0, 128, 0));
        else if ("Partial".equalsIgnoreCase(invoice.getStatus()))
            statusValue.setForeground(new Color(255, 140, 0));
        else
            statusValue.setForeground(Color.RED);

        JLabel createdLabel = new JLabel(UIConfig.CREATED_LABEL);
        createdLabel.setFont(detailFont);
        JLabel createdValue = new JLabel(
                invoice.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        createdValue.setFont(detailFont);

        grid.add(insuranceLabel);
        grid.add(insuranceValue);
        grid.add(totalLabel);
        grid.add(totalValue);
        grid.add(balanceLabel);
        grid.add(balanceValue);
        grid.add(statusLabel);
        grid.add(statusValue);
        grid.add(createdLabel);
        grid.add(createdValue);

        grid.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(grid);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        btnPanel.setBackground(UITheme.BACKGROUND_COLOR);

        if (!"Paid".equals(invoice.getStatus())) {
            JButton payBtn = new JButton(UIConfig.RECORD_PAYMENT_BUTTON_TEXT);
            styleButton(payBtn);
            payBtn.setFont(UITheme.BUTTON_FONT);
            payBtn.addActionListener(e -> showPaymentDialog(invoice));
            btnPanel.add(payBtn);
        }

        JButton deleteBtn = new JButton(UIConfig.DELETE_INVOICE_BUTTON_TEXT);
        styleButton(deleteBtn);
        deleteBtn.setFont(UITheme.BUTTON_FONT);
        deleteBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, UIConfig.CONFIRM_DELETE_INVOICE,
                    UIConfig.CONFIRM_DIALOG_TITLE, JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                billingController.deleteInvoice(invoice.getId());
                notificationManager.notifyInvoiceDeleted(invoice.getPatientName());
                refreshInvoices();
                RevenueSummaryUtil.fireRevenueSummaryChanged();
                invoiceDialog.dispose();
            }
        });
        
        btnPanel.add(deleteBtn);

        // Export/Print Button
        JButton exportBtn = new JButton("Export");
        styleButton(exportBtn);
        exportBtn.setFont(UITheme.BUTTON_FONT);
        String finalApptDateTime = apptDateTime;
        exportBtn.addActionListener(e -> InvoiceExportUtil.exportInvoice(this, invoice, finalApptDateTime, payments));
        btnPanel.add(exportBtn);

        // Scroll pane to display the invoice details (with better styling!!!!)
        JScrollPane scrollPane = new JScrollPane(infoPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); 
        invoiceContentPanel.add(scrollPane, BorderLayout.CENTER);
        invoiceContentPanel.add(btnPanel, BorderLayout.SOUTH);

        // Revalidate and repaint the invoice content Panel
        invoiceContentPanel.revalidate();
        invoiceContentPanel.repaint();

        if (!invoiceDialog.isVisible()) {
            invoiceDialog.pack();
            invoiceDialog.setLocationRelativeTo(this);
            invoiceDialog.setVisible(true);
        }
        refreshInvoices();
    }

    private void showPaymentDialog(Invoice invoice) {

        // Format the balance with exactly 2 decimal places
        String formattedBalance = String.format("%.2f", Math.round(invoice.getBalance() * 100.0) / 100.0);
        JTextField amountField = new JTextField(formattedBalance);
        amountField.setFont(UITheme.LABEL_FONT);
        JComboBox<String> methodBox = new JComboBox<>(PAYMENT_METHODS);
        methodBox.setFont(UITheme.LABEL_FONT);

        // Create a panel with proper spacing and layout
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UITheme.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        // Add current balance information with exactly 2 decimal places
        JLabel balanceLabel = new JLabel(String.format("Current Balance: $%.2f", 
            Math.round(invoice.getBalance() * 100.0) / 100.0));
        balanceLabel.setFont(UITheme.LABEL_FONT.deriveFont(Font.BOLD));
        balanceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(balanceLabel);
        panel.add(Box.createVerticalStrut(16));

        // Amount field with label
        JLabel amountLabel = new JLabel(UIConfig.AMOUNT_LABEL);
        amountLabel.setFont(UITheme.LABEL_FONT);
        amountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(amountLabel);
        panel.add(Box.createVerticalStrut(4));
        amountField.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(amountField);
        panel.add(Box.createVerticalStrut(12));

        // Payment method with label
        JLabel methodLabel = new JLabel(UIConfig.PAYMENT_METHOD_LABEL);
        methodLabel.setFont(UITheme.LABEL_FONT);
        methodLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(methodLabel);
        panel.add(Box.createVerticalStrut(4));
        methodBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(methodBox);

        // Add input validation for amount field
        amountField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                validateAmount();
            }
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                validateAmount();
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                validateAmount();
            }

            private void validateAmount() {
                String text = amountField.getText();

                if (!text.matches("^\\d*\\.?\\d{0,2}$")) { // Regex usedd to check if the amount is valid
                    amountField.setBackground(new Color(255, 200, 200));
                } else {
                    amountField.setBackground(Color.WHITE);
                }
            }
        });

        int result = JOptionPane.showConfirmDialog(this, panel, UIConfig.RECORD_PAYMENT_DIALOG_TITLE,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String amountText = amountField.getText().trim();
                if (!amountText.matches("^\\d*\\.?\\d{0,2}$")) {
                    throw new NumberFormatException("Please enter a valid amount with up to 2 decimal places");
                }
                
                // Round to 2 decimal places to handle sub-cent values
                double amount = Math.round(Double.parseDouble(amountText) * 100.0) / 100.0;
                String method = (String) methodBox.getSelectedItem();
                BillingValidator.validatePaymentAmount(amount, invoice.getBalance());
                
                // Record the payment
                billingController.recordPayment(invoice.getId(), amount, method);
                
                // Get the updated invoice to check its new status
                Invoice updatedInvoice = billingController.getInvoiceById(invoice.getId());
                
                // Only show the paid notification if the invoice is now fully paid
                if ("Paid".equals(updatedInvoice.getStatus())) {
                    notificationManager.notifyInvoicePaid(updatedInvoice.getPatientName());
                }
                
                refreshInvoices();
                RevenueSummaryUtil.fireRevenueSummaryChanged();
                
                // Show updated details in the same dialog
                List<Payment> updatedPayments = billingController.getPaymentsByInvoice(invoice.getId());
                showInvoiceDetail(updatedInvoice, updatedPayments);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid amount with up to 2 decimal places.",
                        UIConfig.ERROR_DIALOG_TITLE, JOptionPane.ERROR_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), UIConfig.ERROR_DIALOG_TITLE,
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}