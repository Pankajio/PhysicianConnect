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
import java.util.List;
import java.time.format.DateTimeFormatter;


/**
 * Shows a single day’s 16 half-hour slots (08:00–16:30) in one column,
 * with a dedicated left-hand column for the time labels.
 *
 * Now uses AppointmentController instead of AppointmentManager directly.
 */
public class DailyAvailabilityPanel extends JPanel {
    private final String physicianId;
    private final AvailabilityService availabilityService;
    private final AppointmentController appointmentController;
    private final Runnable onDayChanged;

    private LocalDate currentDate;
    private List<TimeSlot> currentSlots;

    // Constants (layout dimensions)
    private static final int SLOT_COUNT       = 18;   // 16 half-hour slots (08:00–16:30)
    private static final int PIXEL_PER_SLOT   = 30;   // each row is 30px tall
    private static final int TIME_LABEL_WIDTH = 80;   // width of the left-hand time column
    private static final int SLOT_COLUMN_WIDTH = 200; // width of the slot column

    /**
     * @param physicianId          the physician’s integer ID
     * @param svc                  the AvailabilityService
     * @param apptController       the AppointmentController to use for create/update/delete/fetch
     * @param date                 the initially displayed date
     * @param onDayChanged         callback to run whenever the day’s data changes
     */
    public DailyAvailabilityPanel(String physicianId,
                                  AvailabilityService svc,
                                  AppointmentController apptController,
                                  LocalDate date,
                                  Runnable onDayChanged) {
        this.physicianId          = physicianId;
        this.availabilityService  = svc;
        this.appointmentController = apptController;
        this.currentDate          = date;
        this.onDayChanged         = onDayChanged;

        int totalWidth  = TIME_LABEL_WIDTH + SLOT_COLUMN_WIDTH;
        int totalHeight = SLOT_COUNT * PIXEL_PER_SLOT;
        setPreferredSize(new Dimension(totalWidth + 1, totalHeight + 1));
        setBackground(UITheme.BACKGROUND_COLOR);

        loadSlotsForDate(date);

        // When user clicks on a slot, either book a new appointment or view/edit an existing one:
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                // (1) If clicked in the time-label column, ignore
                if (x < TIME_LABEL_WIDTH) {
                    return;
                }

                // (2) Compute which slot row (0..15) was clicked
                int slotIndex = y / PIXEL_PER_SLOT;
                if (slotIndex < 0 || slotIndex >= SLOT_COUNT) {
                    return;
                }

                TimeSlot ts = currentSlots.get(slotIndex);
                LocalDateTime slotTime = ts.getStart();

                if (!ts.isBooked()) {
                    // → FREE slot: confirm and open AddAppointmentDialog
                    int choice = JOptionPane.showConfirmDialog(
                            DailyAvailabilityPanel.this,
                            UIConfig.FREE_SLOT_MESSAGE
                                    .replace("{date}", slotTime.toLocalDate().toString())
                                    .replace("{time}", slotTime.toLocalTime().toString()),
                            UIConfig.ADD_APPOINTMENT_CONFIRM_TITLE,
                            JOptionPane.YES_NO_OPTION
                    );

                    if (choice == JOptionPane.YES_OPTION) {
                        AddAppointmentPanel addDlg = new AddAppointmentPanel(
                                (JFrame) SwingUtilities.getWindowAncestor(DailyAvailabilityPanel.this),
                                appointmentController,
                                String.valueOf(physicianId),
                                () -> {
                                    // Reload slots and notify week view (if any) of change:
                                    loadSlotsForDate(currentDate);
                                    onDayChanged.run();
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
                    // → BOOKED slot: find matching Appointment by exact date/time via controller
                    Appointment existingAppt = appointmentController
                            .getAppointmentsForPhysician(String.valueOf(physicianId))
                            .stream()
                            .filter(a -> a.getDateTime().equals(slotTime))
                            .findFirst()
                            .orElse(null);

                    if (existingAppt != null) {
                        ViewAppointmentPanel viewDlg = new ViewAppointmentPanel(
                                (JFrame) SwingUtilities.getWindowAncestor(DailyAvailabilityPanel.this),
                                appointmentController,
                                existingAppt,
                                () -> {
                                    // Reload slots and notify week view of change:
                                    loadSlotsForDate(currentDate);
                                    onDayChanged.run();
                                }
                        );
                        viewDlg.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(
                                DailyAvailabilityPanel.this,
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
     * Loads 16 half-hour slots for the given date.
     * On SQLException, falls back to “all free” using TimeSlot.generateDailySlots().
     */
    public void loadSlotsForDate(LocalDate date) {
        this.currentDate = date;
        try {
            this.currentSlots = availabilityService.getDailyAvailability(
                    String.valueOf(physicianId),
                    date
            );
        } catch (Exception ex) {
            ex.printStackTrace();
            this.currentSlots = TimeSlot.generateDailySlots(date);
        }
        repaint();
    }

    /**
     * Return the currently displayed date (so the callback can figure out which week it belongs to).
     */
    public LocalDate getCurrentDate() {
        return currentDate;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int height = SLOT_COUNT * PIXEL_PER_SLOT;

        // ─── (1) Draw the left-hand time labels ───
        g.setColor(UITheme.ACCENT_LIGHT_COLOR);
        g.fillRect(0, 0, TIME_LABEL_WIDTH, height);
        g.setColor(UITheme.TEXT_COLOR);
        g.drawRect(0, 0, TIME_LABEL_WIDTH, height);

        LocalTime t = LocalTime.of(8, 0);
        for (int i = 0; i < SLOT_COUNT; i++) {
            int y = i * PIXEL_PER_SLOT;
            g.drawRect(0, y, TIME_LABEL_WIDTH, PIXEL_PER_SLOT);
            g.drawString(t.toString(), 10, y + 20);
            t = t.plusMinutes(30);
        }

        // ─── (2) Draw each slot rectangle and patient name if booked ───
        for (int i = 0; i < SLOT_COUNT; i++) {
            int y = i * PIXEL_PER_SLOT;
            TimeSlot ts = currentSlots.get(i);

            Color fill = ts.isBooked()
                    ? UITheme.ACCENT_LIGHT_COLOR.darker()
                    : UITheme.BACKGROUND_COLOR;
            g.setColor(fill);
            g.fillRect(TIME_LABEL_WIDTH, y, SLOT_COLUMN_WIDTH, PIXEL_PER_SLOT);

            g.setColor(UITheme.TEXT_COLOR);
            g.drawRect(TIME_LABEL_WIDTH, y, SLOT_COLUMN_WIDTH, PIXEL_PER_SLOT);

            if (ts.isBooked()) {
                String patient = ts.getPatientName();
                String display = (patient.length() > 18)
                        ? patient.substring(0, 15) + "…"
                        : patient;
                g.drawString(display, TIME_LABEL_WIDTH + 5, y + 18);
            }
        }
    }
}
