package physicianconnect.persistence.sqlite;

import physicianconnect.objects.Payment;
import physicianconnect.persistence.interfaces.PaymentPersistence;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class PaymentDB implements PaymentPersistence {
    private final Connection connection;

    public PaymentDB(Connection connection) {
        this.connection = connection;
        createTable();
    }

    private void createTable() {
        // Table is created in SchemaInitializer
    }

    @Override
    public void addPayment(Payment payment) {
        String sql = "INSERT INTO payments (id, invoice_id, amount, method, paid_at) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, payment.getId());
            stmt.setString(2, payment.getInvoiceId());
            stmt.setDouble(3, payment.getAmount());
            stmt.setString(4, payment.getMethod());
            stmt.setString(5, payment.getPaidAt().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add payment", e);
        }
    }

    @Override
    public List<Payment> getPaymentsByInvoice(String invoiceId) {
        List<Payment> result = new ArrayList<>();
        String sql = "SELECT * FROM payments WHERE invoice_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, invoiceId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) result.add(fromResultSet(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch payments by invoice", e);
        }
        return result;
    }

    @Override
    public List<Payment> getPaymentsByMonth(int year, int month) {
        List<Payment> result = new ArrayList<>();
        String sql = "SELECT * FROM payments";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Payment p = fromResultSet(rs);
                if (p.getPaidAt().getYear() == year && p.getPaidAt().getMonthValue() == month)
                    result.add(p);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch payments by month", e);
        }
        return result;
    }

    private Payment fromResultSet(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String invoiceId = rs.getString("invoice_id");
        double amount = rs.getDouble("amount");
        String method = rs.getString("method");
        LocalDateTime paidAt = LocalDateTime.parse(rs.getString("paid_at"));
        return new Payment(id, invoiceId, amount, method);
    }
}