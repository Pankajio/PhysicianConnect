package physicianconnect.presentation;

import physicianconnect.logic.AvailabilityService;
import physicianconnect.logic.controller.AppointmentController;
import physicianconnect.objects.TimeSlot;
import physicianconnect.objects.Appointment;
import physicianconnect.presentation.config.UIConfig;
import physicianconnect.presentation.config.UITheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.time.format.DateTimeFormatter;


/**
 * Shows a weekly grid: a left-hand time column (08:00–16:30)
 * plus seven day columns (Mon–Sun), each with 16 slots.
 *
 * Now uses AppointmentController instead of AppointmentManager directly.
 */
public class WeeklyAvailabilityPanel extends JPanel {
    private final String physicianId;
    private final AvailabilityService availabilityService;
    private final AppointmentController appointmentController;
    private final Runnable onWeekChanged;
    private LocalDate weekStart;
    private Map<LocalDate, List<TimeSlot>> weekData;

    // Constants
    private static final int DAYS_IN_WEEK     = 7;
    private static final int SLOT_COUNT       = 18;   // 08:00–16:30
    private static final int PIXEL_PER_SLOT   = 30;   // each row is 30px tall
    private static final int TIME_LABEL_WIDTH = 80;   // width of left-hand time column
    private static final int DAY_COLUMN_WIDTH = 100;  // width of each day-of-week column
    private static final int HEADER_HEIGHT    = 30;   // height of the day-header row

    /**
     * @param physicianId      the physician’s integer ID
     * @param svc              the AvailabilityService
     * @param apptController   the AppointmentController to use for create/update/delete/fetch
     * @param monday           the LocalDate representing the Monday of the week to display
     * @param onWeekChanged    callback to run after any appointment add/update/delete inside weekly view
     */
    public WeeklyAvailabilityPanel(String physicianId,
                                   AvailabilityService svc,
                                   AppointmentController apptController,
                                   LocalDate monday,
                                   Runnable onWeekChanged) {
        this.physicianId           = physicianId;
        this.availabilityService   = svc;
        this.appointmentController = apptController;
        this.onWeekChanged         = onWeekChanged;
        this.weekStart             = monday;

        int totalW = TIME_LABEL_WIDTH + (DAYS_IN_WEEK * DAY_COLUMN_WIDTH);
        int totalH = HEADER_HEIGHT + (SLOT_COUNT * PIXEL_PER_SLOT);
        setPreferredSize(new Dimension(totalW + 1, totalH + 1));
        setBackground(UITheme.BACKGROUND_COLOR);

        loadWeek(monday);

        // When user clicks in the weekly grid, either book a new appointment or open existing one:
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                // (1) If click is in the time-label column or header row, ignore
                if (x < TIME_LABEL_WIDTH || y < HEADER_HEIGHT) {
                    return;
                }

                // (2) Determine which day column (0..6) and which slot row (0..15)
                int dayIndex = (x - TIME_LABEL_WIDTH) / DAY_COLUMN_WIDTH;
                if (dayIndex < 0 || dayIndex >= DAYS_IN_WEEK) {
                    return;
                }
                int slotIndex = (y - HEADER_HEIGHT) / PIXEL_PER_SLOT;
                if (slotIndex < 0 || slotIndex >= SLOT_COUNT) {
                    return;
                }

                LocalDate clickedDate = weekStart.plusDays(dayIndex);
                TimeSlot ts = weekData.get(clickedDate).get(slotIndex);
                LocalDateTime slotTime = ts.getStart();

                if (!ts.isBooked()) {
                    // → FREE slot: confirm & open AddAppointmentDialog
                    int choice = JOptionPane.showConfirmDialog(
                            WeeklyAvailabilityPanel.this,
                            UIConfig.FREE_SLOT_MESSAGE
                                    .replace("{date}", clickedDate.toString())
                                    .replace("{time}", slotTime.toLocalTime().toString()),
                            UIConfig.ADD_APPOINTMENT_CONFIRM_TITLE,
                            JOptionPane.YES_NO_OPTION
                    );
                    if (choice == JOptionPane.YES_OPTION) {
                        AddAppointmentPanel addDlg = new AddAppointmentPanel(
                                (JFrame) SwingUtilities.getWindowAncestor(WeeklyAvailabilityPanel.this),
                                appointmentController,
                                String.valueOf(physicianId),
                                () -> {
                                    // Reload week slots and notify day view (if any) of change:
                                    loadWeek(weekStart);
                                    onWeekChanged.run();
                                }
                        );
                        // Pre-fill date/time spinners:
                        // 1) set the date
                        java.util.Date prefillDate = java.util.Date.from(
                                slotTime.atZone(ZoneId.systemDefault()).toInstant()
                        );
                        addDlg.dateSpinner.setValue(prefillDate);

                        // 2) format the time as “HH:mm”
                        String prefillString = slotTime
                                .toLocalTime()
                                .format(DateTimeFormatter.ofPattern("HH:mm"));

                        // 3) set the combo
                        addDlg.timeCombo.setSelectedItem(prefillString);

                        addDlg.setVisible(true);
                    }
                } else {
                    // → BOOKED slot: find the matching Appointment by date/time via controller
                    Appointment existingAppt = appointmentController
                            .getAppointmentsForPhysician(String.valueOf(physicianId))
                            .stream()
                            .filter(a -> a.getDateTime().equals(slotTime))
                            .findFirst()
                            .orElse(null);

                    if (existingAppt != null) {
                        ViewAppointmentPanel viewDlg = new ViewAppointmentPanel(
                                (JFrame) SwingUtilities.getWindowAncestor(WeeklyAvailabilityPanel.this),
                                appointmentController,
                                existingAppt,
                                () -> {
                                    // Reload week slots and notify day view of change:
                                    loadWeek(weekStart);
                                    onWeekChanged.run();
                                }
                        );
                        viewDlg.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(
                                WeeklyAvailabilityPanel.this,
                                UIConfig.ERROR_APPOINTMENT_NOT_FOUND,
                                UIConfig.ERROR_DIALOG_TITLE,
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            }
        });
    }

    /**
     * Loads a map of seven days → sixteen slots each. On exception, falls back to “all free.”
     */
    public void loadWeek(LocalDate monday) {
        this.weekStart = monday;
        try {
            this.weekData = availabilityService.getWeeklyAvailability(
                    String.valueOf(physicianId),
                    monday
            );
        } catch (Exception ex) {
            ex.printStackTrace();
            Map<LocalDate, List<TimeSlot>> fallback = new LinkedHashMap<>();
            for (int i = 0; i < DAYS_IN_WEEK; i++) {
                LocalDate day = monday.plusDays(i);
                List<TimeSlot> slots = TimeSlot.generateDailySlots(day);
                fallback.put(day, slots);
            }
            this.weekData = fallback;
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int totalHeight = HEADER_HEIGHT + (SLOT_COUNT * PIXEL_PER_SLOT);

        // ─── (1) Draw left-hand time column ───
        g.setColor(UITheme.ACCENT_LIGHT_COLOR);
        g.fillRect(0, 0, TIME_LABEL_WIDTH, totalHeight);
        g.setColor(UITheme.TEXT_COLOR);
        g.drawRect(0, 0, TIME_LABEL_WIDTH, totalHeight);
        g.drawRect(0, 0, TIME_LABEL_WIDTH, HEADER_HEIGHT);

        LocalTime t = LocalTime.of(8, 0);
        for (int i = 0; i < SLOT_COUNT; i++) {
            int y = HEADER_HEIGHT + (i * PIXEL_PER_SLOT);
            g.drawRect(0, y, TIME_LABEL_WIDTH, PIXEL_PER_SLOT);
            g.drawString(t.toString(), 10, y + 20);
            t = t.plusMinutes(30);
        }

        // ─── (2) Draw each day-column header & slots ───
        for (int day = 0; day < DAYS_IN_WEEK; day++) {
            int x = TIME_LABEL_WIDTH + (day * DAY_COLUMN_WIDTH);
            LocalDate date = weekStart.plusDays(day);

            // 2a) header background + border + weekday label
            g.setColor(UITheme.BACKGROUND_COLOR.darker());
            g.fillRect(x, 0, DAY_COLUMN_WIDTH, HEADER_HEIGHT);
            g.setColor(UITheme.TEXT_COLOR);
            g.drawRect(x, 0, DAY_COLUMN_WIDTH, HEADER_HEIGHT);
            String header = date.getDayOfWeek().toString().substring(0, 3)
                    + " " + date.getMonthValue() + "/" + date.getDayOfMonth();
            g.drawString(header, x + 5, 20);

            // 2b) sixteen half-hour slots
            List<TimeSlot> slots = weekData.get(date);
            for (int i = 0; i < SLOT_COUNT; i++) {
                int y = HEADER_HEIGHT + (i * PIXEL_PER_SLOT);
                TimeSlot ts = slots.get(i);

                Color fill = ts.isBooked()
                        ? UITheme.ACCENT_LIGHT_COLOR.darker()
                        : UITheme.BACKGROUND_COLOR;
                g.setColor(fill);
                g.fillRect(x, y, DAY_COLUMN_WIDTH, PIXEL_PER_SLOT);

                g.setColor(UITheme.TEXT_COLOR);
                g.drawRect(x, y, DAY_COLUMN_WIDTH, PIXEL_PER_SLOT);

                if (ts.isBooked()) {
                    String patient = ts.getPatientName();
                    String display = (patient.length() > 12)
                            ? patient.substring(0, 9) + "…"
                            : patient;
                    g.drawString(display, x + 5, y + 18);
                }
            }
        }
    }
}