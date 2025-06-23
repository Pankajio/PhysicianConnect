package physicianconnect.presentation;

import physicianconnect.logic.controller.AppointmentController;
import physicianconnect.logic.manager.PhysicianManager;
import physicianconnect.logic.AvailabilityService;
import physicianconnect.objects.Physician;

import javax.swing.*;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class AllPhysiciansDailyPanel extends JPanel {
    private final JPanel panelsContainer;
    private final JTextField searchField;
    private final PhysicianManager physicianManager;
    private final AppointmentController appointmentController;
    private final AvailabilityService availabilityService;
    private final LocalDate date;
    private List<Physician> allPhysicians;

    public AllPhysiciansDailyPanel(
            PhysicianManager physicianManager,
            AppointmentController appointmentController,
            AvailabilityService availabilityService,
            LocalDate date,
            java.util.function.Consumer<LocalDate> onDateChange
    ) {
        this.physicianManager = physicianManager;
        this.appointmentController = appointmentController;
        this.availabilityService = availabilityService;
        this.date = date;

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 247, 250));

        // Navigation panel
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        navPanel.setBackground(new Color(245, 247, 250));
        JButton prevDayBtn = new JButton("← Prev Day");
        JButton nextDayBtn = new JButton("Next Day →");
        JLabel dayLabel = new JLabel("Show Date: " + date);
        dayLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dayLabel.setForeground(new Color(34, 40, 49));
        navPanel.add(prevDayBtn);
        navPanel.add(dayLabel);
        navPanel.add(nextDayBtn);

        prevDayBtn.addActionListener(e -> onDateChange.accept(date.minusDays(1)));
        nextDayBtn.addActionListener(e -> onDateChange.accept(date.plusDays(1)));

        add(navPanel, BorderLayout.SOUTH);
        // Search bar
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.setBackground(new Color(245, 247, 250));
        JLabel searchLabel = new JLabel("Search Physician: ");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.putClientProperty("JTextField.placeholderText", "Type physician name...");
        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        add(searchPanel, BorderLayout.NORTH);

        // Container for DailyAvailabilityPanels
        panelsContainer = new JPanel();
        panelsContainer.setLayout(new BoxLayout(panelsContainer, BoxLayout.X_AXIS));
        panelsContainer.setBackground(new Color(245, 247, 250));
        JScrollPane scrollPane = new JScrollPane(panelsContainer,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        // Load all physicians and display panels
        allPhysicians = physicianManager.getAllPhysicians();
        updatePhysicianPanels(allPhysicians);

        // Search filter
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            private void filter() {
                String text = searchField.getText().trim().toLowerCase();
                List<Physician> filtered = allPhysicians.stream()
                        .filter(p -> p.getName().toLowerCase().contains(text))
                        .collect(Collectors.toList());
                updatePhysicianPanels(filtered);
            }
        });
    }

    private void updatePhysicianPanels(List<Physician> physicians) {
        panelsContainer.removeAll();
        for (Physician p : physicians) {
            JPanel panelWithLabel = new JPanel(new BorderLayout());
            panelWithLabel.setBackground(new Color(245, 247, 250));
            JLabel nameLabel = new JLabel(p.getName(), SwingConstants.CENTER);
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            nameLabel.setOpaque(true);
            nameLabel.setBackground(new Color(220, 230, 250));
            panelWithLabel.add(nameLabel, BorderLayout.NORTH);

            DailyAvailabilityPanel dailyPanel = new DailyAvailabilityPanel(
                    p.getId(),
                    availabilityService,
                    appointmentController,
                    date,
                    () -> date.with(DayOfWeek.MONDAY));
            panelWithLabel.add(dailyPanel, BorderLayout.CENTER);
            panelsContainer.add(panelWithLabel);
        }
        panelsContainer.revalidate();
        panelsContainer.repaint();
    }
}