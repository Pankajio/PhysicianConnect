package physicianconnect.persistence.sqlite;

import org.junit.jupiter.api.*;
import physicianconnect.objects.Invoice;
import physicianconnect.objects.ServiceItem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InvoiceDBTest {
    private Connection conn;
    private InvoiceDB db;

    @BeforeEach
    void setUp() throws Exception {
        conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        SchemaInitializer.initializeSchema(conn);
        db = new InvoiceDB(conn);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    // Helper to insert a matching physician for foreign key constraint
    private void insertPhysician(String id) throws Exception {
        String sql = "INSERT INTO physicians (id, name, email, password) VALUES (?, ?, ?, ?)";
        try (var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.setString(2, "Test Physician");
            stmt.setString(3, "test@doc.com");
            stmt.setString(4, "pw");
            stmt.executeUpdate();
        }
    }

    // Helper to insert a matching appointment for foreign key constraint
    private void insertAppointment(String appointmentId) throws Exception {
        String physicianId = "doc" + appointmentId;
        insertPhysician(physicianId);
        String sql = "INSERT INTO appointments (id, physician_id, patient_name, datetime, notes) VALUES (?, ?, ?, ?, ?)";
        try (var stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(appointmentId));
            stmt.setString(2, physicianId);
            stmt.setString(3, "Test Patient");
            stmt.setString(4, LocalDateTime.now().toString());
            stmt.setString(5, "");
            stmt.executeUpdate();
        }
    }

    @Test
    void testAddAndFetchInvoice() throws Exception {
        insertAppointment("1");
        ServiceItem s = new ServiceItem("Consult", 100);
        Invoice inv = new Invoice("inv1", "1", "Alice", List.of(s), 0);
        db.addInvoice(inv);
        Invoice fetched = db.getInvoiceById("inv1");
        assertNotNull(fetched);
        assertEquals("Alice", fetched.getPatientName());
        assertEquals(100, fetched.getTotalAmount());
    }

    @Test
    void testGetInvoiceByIdNotFound() {
        assertNull(db.getInvoiceById("notfound"));
    }

    @Test
    void testGetAllInvoices() throws Exception {
        insertAppointment("1");
        insertAppointment("2");
        db.addInvoice(new Invoice("inv1", "1", "Alice", List.of(new ServiceItem("Consult", 100)), 0));
        db.addInvoice(new Invoice("inv2", "2", "Bob", List.of(new ServiceItem("Lab", 50)), 0));
        List<Invoice> all = db.getAllInvoices();
        assertEquals(2, all.size());
    }

    @Test
    void testGetInvoicesByMonth() throws Exception {
        insertAppointment("1");
        Invoice inv = new Invoice("inv1", "1", "Alice", List.of(new ServiceItem("Consult", 100)), 0);
        db.addInvoice(inv);
        LocalDateTime created = inv.getCreatedAt();
        List<Invoice> result = db.getInvoicesByMonth(created.getYear(), created.getMonthValue());
        assertEquals(1, result.size());
    }

    @Test
    void testGetInvoicesByMonthNotFound() throws Exception {
        insertAppointment("1");
        Invoice inv = new Invoice("inv1", "1", "Alice", List.of(new ServiceItem("Consult", 100)), 0);
        db.addInvoice(inv);
        List<Invoice> result = db.getInvoicesByMonth(1999, 1);
        assertTrue(result.isEmpty());
    }

    @Test
    void testUpdateInvoice() throws Exception {
        insertAppointment("1");
        Invoice inv = new Invoice("inv1", "1", "Alice", List.of(new ServiceItem("Consult", 100)), 0);
        db.addInvoice(inv);
        inv.setBalance(50.0);
        inv.setStatus("Paid");
        db.updateInvoice(inv);
        Invoice updated = db.getInvoiceById("inv1");
        assertEquals(50.0, updated.getBalance());
        assertEquals("Paid", updated.getStatus());
    }

    @Test
    void testDeleteInvoice() throws Exception {
        insertAppointment("1");
        db.addInvoice(new Invoice("inv1", "1", "Alice", List.of(new ServiceItem("Consult", 100)), 0));
        db.deleteInvoiceById("inv1");
        assertNull(db.getInvoiceById("inv1"));
    }

    // Exception/catch coverage

    @Test
    void testAddInvoiceCatchesSQLException() throws Exception {
        conn.close();
        Invoice inv = new Invoice("inv1", "1", "Alice", List.of(new ServiceItem("Consult", 100)), 0);
        assertThrows(RuntimeException.class, () -> db.addInvoice(inv));
    }

    @Test
    void testGetInvoiceByIdCatchesSQLException() throws Exception {
        conn.close();
        assertThrows(RuntimeException.class, () -> db.getInvoiceById("inv1"));
    }

    @Test
    void testGetInvoicesByMonthCatchesSQLException() throws Exception {
        conn.close();
        assertThrows(RuntimeException.class, () -> db.getInvoicesByMonth(2025, 6));
    }

    @Test
    void testGetAllInvoicesCatchesSQLException() throws Exception {
        conn.close();
        assertThrows(RuntimeException.class, () -> db.getAllInvoices());
    }

    @Test
    void testUpdateInvoiceCatchesSQLException() throws Exception {
        conn.close();
        Invoice inv = new Invoice("inv1", "1", "Alice", List.of(new ServiceItem("Consult", 100)), 0);
        assertThrows(RuntimeException.class, () -> db.updateInvoice(inv));
    }

    @Test
    void testDeleteInvoiceByIdCatchesSQLException() throws Exception {
        conn.close();
        assertThrows(RuntimeException.class, () -> db.deleteInvoiceById("inv1"));
    }

    @Test
    void testDeleteInvoiceByIdActuallyDeletes() throws Exception {
        insertAppointment("1");
        Invoice inv = new Invoice("invDel", "1", "Del", List.of(new ServiceItem("X", 1)), 0);
        db.addInvoice(inv);
        assertNotNull(db.getInvoiceById("invDel"));
        db.deleteInvoiceById("invDel");
        assertNull(db.getInvoiceById("invDel"));
    }

    @Test
    void testUpdateInvoiceActuallyUpdates() throws Exception {
        insertAppointment("1");
        Invoice inv = new Invoice("invU", "1", "U", List.of(new ServiceItem("Y", 2)), 0);
        db.addInvoice(inv);
        inv.setBalance(123.45);
        inv.setStatus("Paid");
        db.updateInvoice(inv);
        Invoice updated = db.getInvoiceById("invU");
        assertEquals(123.45, updated.getBalance());
        assertEquals("Paid", updated.getStatus());
    }

    @Test
    void testGetAllInvoicesCoversWhileLoop() throws Exception {
        insertAppointment("1");
        insertAppointment("2");
        db.addInvoice(new Invoice("invA", "1", "A", List.of(new ServiceItem("A", 10)), 0));
        db.addInvoice(new Invoice("invB", "2", "B", List.of(new ServiceItem("B", 20)), 0));
        List<Invoice> all = db.getAllInvoices();
        assertTrue(all.stream().anyMatch(i -> i.getId().equals("invA")));
        assertTrue(all.stream().anyMatch(i -> i.getId().equals("invB")));
    }

    @Test
    void testGetInvoicesByMonthCoversWhileAndIf() throws Exception {
        insertAppointment("1");
        Invoice inv = new Invoice("invM", "1", "M", List.of(new ServiceItem("M", 30)), 0);
        db.addInvoice(inv);
        LocalDateTime created = inv.getCreatedAt();
        List<Invoice> result = db.getInvoicesByMonth(created.getYear(), created.getMonthValue());
        assertEquals(1, result.size());
        // Negative case: wrong month
        assertTrue(db.getInvoicesByMonth(1999, 1).isEmpty());
    }

    @Test
    void testGetInvoiceByIdCoversIfNext() throws Exception {
        insertAppointment("1");
        Invoice inv = new Invoice("invN", "1", "N", List.of(new ServiceItem("N", 40)), 0);
        db.addInvoice(inv);
        Invoice fetched = db.getInvoiceById("invN");
        assertNotNull(fetched);
        assertEquals("N", fetched.getPatientName());
    }

    @Test
    void testSerializeServicesAndDeserializeServices() throws Exception {
        List<ServiceItem> services = List.of(
            new ServiceItem("A", 1.1),
            new ServiceItem("B", 2.2)
        );
        // serialize with multiple items (covers sb.length() > 0)
        var serializeMethod = db.getClass().getDeclaredMethod("serializeServices", List.class);
        serializeMethod.setAccessible(true);
        String serialized = serializeMethod.invoke(db, services).toString();
        assertTrue(serialized.contains(";"));
        // deserialize with multiple items (covers arr.length == 2)
        var deserializeMethod = db.getClass().getDeclaredMethod("deserializeServices", String.class);
        deserializeMethod.setAccessible(true);
        List<ServiceItem> deserialized = (List<ServiceItem>) deserializeMethod.invoke(db, serialized);
        assertEquals(2, deserialized.size());
        assertEquals("A", deserialized.get(0).getName());
        assertEquals(2.2, deserialized.get(1).getCost());
        // deserialize null/empty (covers str == null || str.isEmpty())
        List<ServiceItem> empty = (List<ServiceItem>) deserializeMethod.invoke(db, "");
        assertTrue(empty.isEmpty());
    }

    @Test
    void testSerializeServicesSingleItem() throws Exception {
        List<ServiceItem> services = List.of(new ServiceItem("Single", 9.99));
        var serializeMethod = db.getClass().getDeclaredMethod("serializeServices", List.class);
        serializeMethod.setAccessible(true);
        String serialized = serializeMethod.invoke(db, services).toString();
        assertEquals("Single:9.99", serialized);
    }

    @Test
    void testDeserializeServicesMalformedString() throws Exception {
        var deserializeMethod = db.getClass().getDeclaredMethod("deserializeServices", String.class);
        deserializeMethod.setAccessible(true);
        // Malformed: missing cost
        List<ServiceItem> result = (List<ServiceItem>) deserializeMethod.invoke(db, "BadData");
        assertTrue(result.isEmpty());
        // Malformed: extra colon
        result = (List<ServiceItem>) deserializeMethod.invoke(db, "A:1.0:extra");
        assertTrue(result.isEmpty());
    }

    @Test
    void testAddInvoiceWithEmptyServices() throws Exception {
        insertAppointment("3");
        Invoice inv = new Invoice("invEmpty", "3", "NoServices", List.of(), 0);
        db.addInvoice(inv);
        Invoice fetched = db.getInvoiceById("invEmpty");
        assertNotNull(fetched);
        assertTrue(fetched.getServices().isEmpty());
    }

    @Test
    void testUpdateInvoiceWithNullStatus() throws Exception {
        insertAppointment("4");
        Invoice inv = new Invoice("invNullStatus", "4", "NullStatus", List.of(new ServiceItem("Test", 1)), 0);
        db.addInvoice(inv);
        inv.setStatus(null);
        db.updateInvoice(inv);
        Invoice updated = db.getInvoiceById("invNullStatus");
        assertNull(updated.getStatus());
    }

    @Test
    void testDeleteInvoiceNonExistent() {
        // Should not throw
        assertDoesNotThrow(() -> db.deleteInvoiceById("doesNotExist"));
    }

    @Test
    void testGetAllInvoicesEmpty() {
        List<Invoice> all = db.getAllInvoices();
        assertTrue(all.isEmpty());
    }

    @Test
    void testFromResultSetCoversAllFields() throws Exception {
        insertAppointment("1");
        Invoice inv = new Invoice("invF", "1", "F", List.of(new ServiceItem("F", 99)), 5.5);
        inv.setBalance(10.1);
        inv.setStatus("Paid");
        db.addInvoice(inv);
        Invoice fetched = db.getInvoiceById("invF");
        assertEquals("invF", fetched.getId());
        assertEquals("1", fetched.getAppointmentId());
        assertEquals("F", fetched.getPatientName());
        assertEquals(5.5, fetched.getInsuranceAdjustment());
        assertEquals(10.1, fetched.getBalance());
        assertEquals("Paid", fetched.getStatus());
        assertFalse(fetched.getServices().isEmpty());
        assertNotNull(fetched.getCreatedAt());
    }

    @Test
    void testFromResultSetWithNullStatus() throws Exception {
        insertAppointment("5");
        // Insert invoice directly with status NULL
        String sql = "INSERT INTO invoices (id, appointment_id, patient_name, services, insurance_adjustment, total_amount, balance, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "invNull");
            stmt.setInt(2, 5);
            stmt.setString(3, "NullStatus");
            stmt.setString(4, ""); // empty services
            stmt.setDouble(5, 0.0);
            stmt.setDouble(6, 0.0);
            stmt.setDouble(7, 0.0);
            stmt.setNull(8, java.sql.Types.VARCHAR); // status is NULL
            stmt.setString(9, LocalDateTime.now().toString());
            stmt.executeUpdate();
        }
        Invoice fetched = db.getInvoiceById("invNull");
        assertNotNull(fetched);
        assertNull(fetched.getStatus());
    }
}