package physicianconnect.persistence.sqlite;

import org.junit.jupiter.api.*;
import physicianconnect.objects.Payment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PaymentDBTest {
    private Connection conn;
    private PaymentDB db;

    @BeforeEach
    void setUp() throws Exception {
        conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        SchemaInitializer.initializeSchema(conn);
        db = new PaymentDB(conn);

        // Insert the physician first for foreign key constraints
        insertPhysician("doc1");
        // Then insert parent appointment and invoice
        insertAppointment("1");
        insertInvoice("inv1", "1");
    }

    @AfterEach
    void tearDown() throws Exception {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    // Helper to insert the physician
    private void insertPhysician(String id) throws Exception {
        String sqlPhys = "INSERT OR IGNORE INTO physicians (id, name, email, password) VALUES (?, ?, ?, ?)";
        try (var stmt = conn.prepareStatement(sqlPhys)) {
            stmt.setString(1, id);
            stmt.setString(2, "Dr. Test");
            stmt.setString(3, "test@doc.com");
            stmt.setString(4, "pw");
            stmt.executeUpdate();
        }
    }

    // Helper to insert an appointment
    private void insertAppointment(String id) throws Exception {
        String sql = "INSERT INTO appointments (id, physician_id, patient_name, datetime, notes) VALUES (?, ?, ?, ?, ?)";
        try (var stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(id));
            stmt.setString(2, "doc1");
            stmt.setString(3, "Test Patient");
            stmt.setString(4, LocalDateTime.now().toString());
            stmt.setString(5, "");
            stmt.executeUpdate();
        }
    }

    // Helper to insert an invoice
    private void insertInvoice(String id, String appointmentId) throws Exception {
        String sql = "INSERT INTO invoices (id, appointment_id, patient_name, services, insurance_adjustment, total_amount, balance, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.setInt(2, Integer.parseInt(appointmentId));
            stmt.setString(3, "Test Patient");
            stmt.setString(4, "");
            stmt.setDouble(5, 0.0);
            stmt.setDouble(6, 0.0);
            stmt.setDouble(7, 0.0);
            stmt.setString(8, "Unpaid");
            stmt.setString(9, LocalDateTime.now().toString());
            stmt.executeUpdate();
        }
    }

    @Test
    void testAddAndFetchPayment() {
        Payment p = new Payment("pid1", "inv1", 50.0, "Cash");
        db.addPayment(p);
        List<Payment> payments = db.getPaymentsByInvoice("inv1");
        assertEquals(1, payments.size());
        assertEquals(50.0, payments.get(0).getAmount());
    }

    @Test
    void testGetPaymentsByMonth() {
        Payment p = new Payment("pid1", "inv1", 50.0, "Cash");
        db.addPayment(p);
        List<Payment> payments = db.getPaymentsByMonth(LocalDateTime.now().getYear(), LocalDateTime.now().getMonthValue());
        assertFalse(payments.isEmpty());
    }

    // --- Catch/exception coverage ---

    @Test
    void testAddPaymentCatchesSQLException() throws Exception {
        conn.close();
        Payment p = new Payment("pid1", "inv1", 50.0, "Cash");
        Exception ex = assertThrows(RuntimeException.class, () -> db.addPayment(p));
        assertTrue(ex.getMessage().contains("Failed to add payment"));
    }

    @Test
    void testGetPaymentsByInvoiceCatchesSQLException() throws Exception {
        Payment p = new Payment("pid1", "inv1", 50.0, "Cash");
        db.addPayment(p);
        conn.close();
        Exception ex = assertThrows(RuntimeException.class, () -> db.getPaymentsByInvoice("inv1"));
        assertTrue(ex.getMessage().contains("Failed to fetch payments by invoice"));
    }

    @Test
    void testGetPaymentsByMonthCatchesSQLException() throws Exception {
        Payment p = new Payment("pid1", "inv1", 50.0, "Cash");
        db.addPayment(p);
        conn.close();
        Exception ex = assertThrows(RuntimeException.class, () -> db.getPaymentsByMonth(LocalDateTime.now().getYear(), LocalDateTime.now().getMonthValue()));
        assertTrue(ex.getMessage().contains("Failed to fetch payments by month"));
    }
}