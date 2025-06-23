package physicianconnect.presentation.util;

import physicianconnect.objects.Invoice;
import physicianconnect.presentation.config.UIConfig;
import physicianconnect.presentation.config.UITheme;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class RevenueSummaryUtil {
        public interface RevenueSummaryListener {
        void onRevenueSummaryChanged();
    }

    private static final List<RevenueSummaryListener> listeners = new CopyOnWriteArrayList<>();

    public static void addListener(RevenueSummaryListener l) {
        listeners.add(l);
    }

    public static void removeListener(RevenueSummaryListener l) {
        listeners.remove(l);
    }

    public static void fireRevenueSummaryChanged() {
        for (RevenueSummaryListener l : listeners) {
            l.onRevenueSummaryChanged();
        }
    }

    public static void showRevenueSummary(Component parent, List<Invoice> invoices) {
        double totalBilled = invoices.stream().mapToDouble(Invoice::getTotalAmount).sum();
        double totalPaid = invoices.stream().mapToDouble(inv -> inv.getTotalAmount() - inv.getBalance()).sum();
        double outstanding = invoices.stream().mapToDouble(Invoice::getBalance).sum();

        // Main panel with border and title
        JPanel mainPanel = new JPanel(new BorderLayout(16, 16));
        mainPanel.setBackground(UITheme.BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Summary panel with grid layout
        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new GridLayout(3, 2, 16, 16));
        summaryPanel.setBackground(UITheme.BACKGROUND_COLOR);
        summaryPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UITheme.ACCENT_LIGHT_COLOR, 2, true),
                UIConfig.REVENUE_SUMMARY_TITLE,
                0, 0, UITheme.HEADER_FONT, UITheme.ACCENT_LIGHT_COLOR));

        // Create styled labels for each metric
        JLabel billedLabel = createMetricLabel(UIConfig.TOTAL_BILLED_LABEL + ":");
        JLabel billedValue = makeSummaryValue(totalBilled, UITheme.PRIMARY_COLOR);

        JLabel paidLabel = createMetricLabel(UIConfig.TOTAL_PAID_LABEL + ":");
        JLabel paidValue = makeSummaryValue(totalPaid, totalPaid < 0 ? Color.RED : new Color(0, 128, 0));

        JLabel outstandingLabel = createMetricLabel(UIConfig.OUTSTANDING_LABEL + ":");
        JLabel outstandingValue = makeSummaryValue(outstanding,
                outstanding > 0 ? Color.RED : UITheme.ACCENT_LIGHT_COLOR);

        summaryPanel.add(billedLabel);
        summaryPanel.add(billedValue);
        summaryPanel.add(paidLabel);
        summaryPanel.add(paidValue);
        summaryPanel.add(outstandingLabel);
        summaryPanel.add(outstandingValue);

        // Bar chart panel with improved styling
        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = getWidth() - 40;
                int height = getHeight() - 80; // Increased space for labels
                int x = 20, y = 20;
                int barWidth = width / 3 - 20;
                double max = Math.max(Math.abs(totalBilled), Math.max(Math.abs(totalPaid), Math.abs(outstanding)));
                int zeroY = y + height / 2;

                // Draw baseline with improved styling
                g2d.setColor(UITheme.ACCENT_LIGHT_COLOR);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawLine(x - 10, zeroY, x + width, zeroY);

                // Drawing bars and labels
                double[] values = {totalBilled, totalPaid, outstanding };
                Color[] colors = {UITheme.PRIMARY_COLOR, new Color(0, 128, 0), Color.RED };
                String[] labels = {UIConfig.TOTAL_BILLED_LABEL, UIConfig.TOTAL_PAID_LABEL,
                        UIConfig.OUTSTANDING_LABEL };

                // Draw bars and labels
                for (int i = 0; i < 3; i++) {
                    int barHeight = (int) ((height / 2) * (Math.abs(values[i]) / (max == 0 ? 1 : max)));
                    int barX = x + i * (barWidth + 20);

                    // Drawing bar with rounded corners (for better visualization!!!!!)
                    int barY = zeroY;
                    if (values[i] >= 0) {
                        barY = zeroY - barHeight;
                    } 

                    // Draw bar with rounded corners
                    g2d.setColor(colors[i]);
                    g2d.fillRoundRect(barX, barY, barWidth, barHeight, 12, 12);
                    g2d.setColor(colors[i].darker());
                    g2d.drawRoundRect(barX, barY, barWidth, barHeight, 12, 12);

                    // Draw value label
                    g2d.setColor(Color.BLACK);
                    g2d.setFont(UITheme.LABEL_FONT);

                    // Draw value label (with exactly 2 decimal places)
                    String valueText = String.format("$%.2f", Math.abs(values[i]));
                    
                    // Getting font metrics to calculate string width
                    FontMetrics fm = g2d.getFontMetrics();
                    int valueWidth = fm.stringWidth(valueText);

                    // Drawing value label in the center of the bar
                    g2d.drawString(valueText, barX + (barWidth - valueWidth) / 2, 
                            values[i] >= 0 ? barY - 5 : barY + barHeight + 15);

                    // Draw category label
                    g2d.drawString(labels[i], barX + (barWidth - fm.stringWidth(labels[i])) / 2, 
                            zeroY + height / 2 + 20);
                }
            }
        };

        chartPanel.setPreferredSize(new Dimension(500, 200));
        chartPanel.setBackground(UITheme.BACKGROUND_COLOR);

        // Add panels to main panel
        mainPanel.add(summaryPanel, BorderLayout.NORTH);
        mainPanel.add(chartPanel, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(parent, mainPanel, UIConfig.REVENUE_SUMMARY_DIALOG_TITLE, 
                JOptionPane.PLAIN_MESSAGE);
    }

    private static JLabel createMetricLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UITheme.LABEL_FONT.deriveFont(Font.BOLD));
        label.setHorizontalAlignment(SwingConstants.LEFT);
        
        return label;
    }

    private static JLabel makeSummaryValue(double value, Color color) {
        JLabel label = new JLabel((value < 0 ? "-$" : "$") + 
                        String.format("%.2f", Math.abs(value)));

        label.setFont(UITheme.HEADER_FONT);
        label.setForeground(color);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        
        return label;
    }

    // Add this method to RevenueSummaryUtil
    public static JPanel createSummaryPanel(List<Invoice> invoices) {
        double totalBilled = invoices.stream().mapToDouble(Invoice::getTotalAmount).sum();
        double totalPaid = invoices.stream().mapToDouble(inv -> inv.getTotalAmount() - inv.getBalance()).sum();
        double outstanding = invoices.stream().mapToDouble(Invoice::getBalance).sum();

        JPanel summaryPanel = new JPanel(new GridLayout(3, 2, 8, 4));
        summaryPanel.setBackground(UITheme.BACKGROUND_COLOR);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        summaryPanel.add(createMetricLabel(UIConfig.TOTAL_BILLED_LABEL + ":"));
        summaryPanel.add(makeSummaryValue(totalBilled, UITheme.PRIMARY_COLOR));
        summaryPanel.add(createMetricLabel(UIConfig.TOTAL_PAID_LABEL + ":"));
        summaryPanel.add(makeSummaryValue(totalPaid, totalPaid < 0 ? Color.RED : new Color(0, 128, 0)));
        summaryPanel.add(createMetricLabel(UIConfig.OUTSTANDING_LABEL + ":"));
        summaryPanel.add(makeSummaryValue(outstanding, outstanding > 0 ? Color.RED : UITheme.ACCENT_LIGHT_COLOR));

        return summaryPanel;
    }
}