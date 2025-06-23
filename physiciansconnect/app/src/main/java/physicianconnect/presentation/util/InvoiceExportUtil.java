package physicianconnect.presentation.util;

import physicianconnect.objects.Invoice;
import physicianconnect.objects.Payment;
import physicianconnect.objects.ServiceItem;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Destination;
import javax.swing.*;
import java.awt.*;
import java.awt.print.*;
import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class InvoiceExportUtil {

public static void exportInvoice(Component parent, Invoice invoice, String apptDateTime, List<Payment> payments) {
    String[] choices = { "Save as TXT", "Export to PDF", "Print to Printer" };
    int which = JOptionPane.showOptionDialog(parent,
        "What would you like to do with the invoice?",
        "Export Options",
        JOptionPane.DEFAULT_OPTION,
        JOptionPane.QUESTION_MESSAGE,
        null, choices, choices[0]);
    if (which < 0) return;  // user cancelled

    String base = invoice.getPatientName().replaceAll("[^a-zA-Z0-9_\\-]", "_") + "_invoice";
    try {
        if (which == 0) {
            // TXT branch
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File(base + ".txt"));
            if (chooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) return;
            File txtFile = chooser.getSelectedFile();

            try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(
                    new FileOutputStream(txtFile), "UTF-8"), true)) {
                writeInvoiceLines(pw, invoice, apptDateTime, payments, true);
            }
            showOpenDialog(parent, txtFile, "TXT Export Successful");

        } else if (which == 1) {
            // PDF branch (no print dialog, just export to PDF)
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File(base + ".pdf"));
            if (chooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) return;
            File pdfFile = chooser.getSelectedFile();

            // Render lines into memory
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (PrintWriter pw = new PrintWriter(
                    new OutputStreamWriter(baos, "UTF-8"), true)) {
                writeInvoiceLines(pw, invoice, apptDateTime, payments, false);
            }
            String[] lines = baos.toString("UTF-8").split("\n");

            Printable printable = (g, pageFormat, pageIndex) -> {
                FontMetrics fm = g.getFontMetrics();
                int lineHt = fm.getHeight();
                int linesPerPage = (int)(pageFormat.getImageableHeight() / lineHt);
                int start = pageIndex * linesPerPage;
                if (start >= lines.length) return Printable.NO_SUCH_PAGE;
                Graphics2D g2 = (Graphics2D) g;
                g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                for (int i = 0; i < linesPerPage && start + i < lines.length; i++) {
                    g2.drawString(lines[start + i], 0, (i+1)*lineHt);
                }
                return Printable.PAGE_EXISTS;
            };

            PrinterJob job = PrinterJob.getPrinterJob();
            job.setJobName(pdfFile.getName());
            job.setPrintable(printable);

            PrintRequestAttributeSet attrs = new HashPrintRequestAttributeSet();
            attrs.add(new Destination(pdfFile.toURI()));

            // Print directly to PDF file, no dialog
            job.print(attrs);

            showOpenDialog(parent, pdfFile, "PDF Export Successful");

        } else {
            // Direct Print branch (show print dialog)
            // Render lines into memory
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (PrintWriter pw = new PrintWriter(
                    new OutputStreamWriter(baos, "UTF-8"), true)) {
                writeInvoiceLines(pw, invoice, apptDateTime, payments, false);
            }
            String[] lines = baos.toString("UTF-8").split("\n");

            Printable printable = (g, pageFormat, pageIndex) -> {
                FontMetrics fm = g.getFontMetrics();
                int lineHt = fm.getHeight();
                int linesPerPage = (int)(pageFormat.getImageableHeight() / lineHt);
                int start = pageIndex * linesPerPage;
                if (start >= lines.length) return Printable.NO_SUCH_PAGE;
                Graphics2D g2 = (Graphics2D) g;
                g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                for (int i = 0; i < linesPerPage && start + i < lines.length; i++) {
                    g2.drawString(lines[start + i], 0, (i+1)*lineHt);
                }
                return Printable.PAGE_EXISTS;
            };

            PrinterJob job = PrinterJob.getPrinterJob();
            job.setJobName(base);
            job.setPrintable(printable);
            if (job.printDialog()) {
                job.print();
                JOptionPane.showMessageDialog(parent,
                    "Invoice has been sent to printer.",
                    "Print Successful",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(parent,
            "Failed to export/print invoice:\n" + ex.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    // Helper to write all invoice lines to any PrintWriter
    private static void writeInvoiceLines(PrintWriter pw,
            Invoice invoice, String apptDateTime, List<Payment> payments, boolean windowsLineEndings) {
        String ln = windowsLineEndings ? "\r\n" : "\n";
        pw.print("==================================================" + ln);
        pw.print("              PHYSICIANSCONNECT INVOICE           " + ln);
        pw.print("==================================================" + ln);
        pw.printf("Patient Name: %s%s", invoice.getPatientName(), ln);
        pw.printf("Appointment:  %s%s", apptDateTime == null ? "-" : apptDateTime, ln);
        pw.print("--------------------------------------------------" + ln);
        pw.print("Services:" + ln);
        for (ServiceItem s : invoice.getServices()) {
            pw.printf("  * %-18s $%8.2f%s", s.getName(), s.getCost(), ln);
        }
        pw.print("--------------------------------------------------" + ln);
        pw.print("Payments:" + ln);
        if (payments != null && !payments.isEmpty()) {
            for (Payment p : payments) {
                pw.printf("  * %s: $%8.2f via %s%s",
                    p.getPaidAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    p.getAmount(),
                    p.getMethod(),
                    ln);
            }
        } else {
            pw.print("  (No payments recorded)" + ln);
        }
        pw.print("--------------------------------------------------" + ln);
        pw.printf("%-22s $%8.2f%s", "Insurance Adjustment:", invoice.getInsuranceAdjustment(), ln);
        pw.printf("%-22s $%8.2f%s", "Total:", invoice.getTotalAmount(), ln);
        pw.printf("%-22s $%8.2f%s", "Balance:", invoice.getBalance(), ln);
        pw.printf("%-22s %s%s", "Status:", invoice.getStatus(), ln);
        pw.printf("%-22s %s%s", "Created:",
            invoice.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), ln);
        pw.print("==================================================" + ln);
        pw.print("Thank you for your business!" + ln);
        pw.print("Exported from PhysiciansConnect" + ln);
    }

    // Helper to show “Open File” button dialog
    private static void showOpenDialog(Component parent, File file, String title) {
        JPanel panel = new JPanel(new BorderLayout(10,10));
        panel.add(new JLabel("<html>Invoice saved to:<br>" +
                file.getAbsolutePath() + "</html>"),
            BorderLayout.CENTER);
        JButton open = new JButton("Open File");
        open.addActionListener(ev -> {
            try { Desktop.getDesktop().open(file); }
            catch (Exception e) {
                JOptionPane.showMessageDialog(parent,
                    "Could not open file:\n" + e.getMessage(),
                    "Open Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        JPanel bp = new JPanel();
        bp.add(open);
        panel.add(bp, BorderLayout.SOUTH);

        JOptionPane.showMessageDialog(parent,
            panel, title, JOptionPane.INFORMATION_MESSAGE);
    }
}