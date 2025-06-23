package physicianconnect.presentation.util;

import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import physicianconnect.objects.Invoice;
import physicianconnect.objects.Payment;
import physicianconnect.objects.ServiceItem;

import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.*;
import java.awt.*;
import java.awt.print.PrinterJob;
import java.io.*;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class InvoiceExportUtilTest {

    Invoice invoice;
    List<ServiceItem> services;
    List<Payment> payments;

    @BeforeEach
    void setup() {
        services = List.of(new ServiceItem("Consult", 100.0), new ServiceItem("Lab", 50.0));
        invoice = mock(Invoice.class);
        when(invoice.getPatientName()).thenReturn("John Doe");
        when(invoice.getServices()).thenReturn(services);
        when(invoice.getInsuranceAdjustment()).thenReturn(10.0);
        when(invoice.getTotalAmount()).thenReturn(150.0);
        when(invoice.getBalance()).thenReturn(40.0);
        when(invoice.getStatus()).thenReturn("Unpaid");
        when(invoice.getCreatedAt()).thenReturn(LocalDateTime.of(2024, 1, 1, 10, 0));
        payments = List.of(
                new Payment("1", "1", 50.0, "Cash"),
                new Payment("2", "1", 60.0, "Card")
        );
    }


    @Test
    void testExportInvoice_printBranch_and_cancel() throws Exception {
        // Print branch, approve and cancel
        try (MockedStatic<JOptionPane> paneMock = mockStatic(JOptionPane.class, CALLS_REAL_METHODS);
             MockedStatic<PrinterJob> printerJobMock = mockStatic(PrinterJob.class, CALLS_REAL_METHODS)) {

            paneMock.when(() -> JOptionPane.showOptionDialog(any(), any(), any(), anyInt(), anyInt(), any(), any(), any()))
                    .thenReturn(2);

            PrinterJob job = mock(PrinterJob.class);
            printerJobMock.when(PrinterJob::getPrinterJob).thenReturn(job);

            doNothing().when(job).setPrintable(any());
            doNothing().when(job).setJobName(anyString());
            when(job.printDialog()).thenReturn(true);
            doNothing().when(job).print();

            paneMock.when(() -> JOptionPane.showMessageDialog(any(), contains("Invoice has been sent to printer."), any(), eq(JOptionPane.INFORMATION_MESSAGE)))
                    .then(inv -> null);

            InvoiceExportUtil.exportInvoice(null, invoice, "2024-01-01 10:00", payments);

            // Cancel print dialog
            when(job.printDialog()).thenReturn(false);
            InvoiceExportUtil.exportInvoice(null, invoice, "2024-01-01 10:00", payments);
        }
    }

    // Helper to access package-private/private methods for full coverage
    static class InvoiceExportUtilTestHelper extends InvoiceExportUtil {
        public static void showOpenDialog(Component parent, File file, String title) {
            try {
                var m = InvoiceExportUtil.class.getDeclaredMethod("showOpenDialog", Component.class, File.class, String.class);
                m.setAccessible(true);
                m.invoke(null, parent, file, title);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}