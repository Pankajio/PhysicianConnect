package physicianconnect.persistence.sqlite;

import physicianconnect.objects.Invoice;
import physicianconnect.objects.ServiceItem;
import physicianconnect.persistence.interfaces.InvoicePersistence;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class InvoiceDB implements InvoicePersistence {
    private final Connection connection;

    public InvoiceDB(Connection connection) {
        this.connection = connection;
        createTable();
    }

    private void createTable() {
        // Table is created in SchemaInitializer
    }


@Override
public void addInvoice(Invoice invoice) {
    String sql = "INSERT INTO invoices (id, appointment_id, patient_name, services, insurance_adjustment, total_amount, balance, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setString(1, invoice.getId());
        stmt.setInt(2, Integer.parseInt(invoice.getAppointmentId())); // <-- FIXED
        stmt.setString(3, invoice.getPatientName());
        stmt.setString(4, serializeServices(invoice.getServices()));
        stmt.setDouble(5, invoice.getInsuranceAdjustment());
        stmt.setDouble(6, invoice.getTotalAmount());
        stmt.setDouble(7, invoice.getBalance());
        stmt.setString(8, invoice.getStatus());
        stmt.setString(9, invoice.getCreatedAt().toString());
        stmt.executeUpdate();
    } catch (SQLException e) {
        throw new RuntimeException("Failed to add invoice", e);
    }
}

    @Override
    public Invoice getInvoiceById(String id) {
        String sql = "SELECT * FROM invoices WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return fromResultSet(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch invoice", e);
        }
        return null;
    }

    @Override
    public List<Invoice> getInvoicesByMonth(int year, int month) {
        List<Invoice> result = new ArrayList<>();
        String sql = "SELECT * FROM invoices";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Invoice inv = fromResultSet(rs);
                if (inv.getCreatedAt().getYear() == year && inv.getCreatedAt().getMonthValue() == month)
                    result.add(inv);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch invoices by month", e);
        }
        return result;
    }

    @Override
    public List<Invoice> getAllInvoices() {
        List<Invoice> result = new ArrayList<>();
        String sql = "SELECT * FROM invoices";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) result.add(fromResultSet(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch all invoices", e);
        }
        return result;
    }

    @Override
    public void updateInvoice(Invoice invoice) {
        String sql = "UPDATE invoices SET balance = ?, status = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, invoice.getBalance());
            stmt.setString(2, invoice.getStatus());
            stmt.setString(3, invoice.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update invoice", e);
        }
    }

private Invoice fromResultSet(ResultSet rs) throws SQLException {
    String id = rs.getString("id");
    String appointmentId = Integer.toString(rs.getInt("appointment_id")); // <-- FIXED
    String patientName = rs.getString("patient_name");
    List<ServiceItem> services = deserializeServices(rs.getString("services"));
    double insuranceAdjustment = rs.getDouble("insurance_adjustment");
    double totalAmount = rs.getDouble("total_amount");
    double balance = rs.getDouble("balance");
    String status = rs.getString("status");
    LocalDateTime createdAt = LocalDateTime.parse(rs.getString("created_at"));
    Invoice inv = new Invoice(id, appointmentId, patientName, services, insuranceAdjustment);
    inv.setBalance(balance);
    inv.setStatus(status);
    return inv;
}

    private String serializeServices(List<ServiceItem> services) {
        StringBuilder sb = new StringBuilder();
        for (ServiceItem s : services) {
            if (sb.length() > 0) sb.append(";");
            sb.append(s.getName()).append(":").append(s.getCost());
        }
        return sb.toString();
    }

    private List<ServiceItem> deserializeServices(String str) {
        List<ServiceItem> list = new ArrayList<>();
        if (str == null || str.isEmpty()) return list;
        for (String part : str.split(";")) {
            String[] arr = part.split(":");
            if (arr.length == 2) list.add(new ServiceItem(arr[0], Double.parseDouble(arr[1])));
        }
        return list;
    }

    @Override
public void deleteInvoiceById(String id) {
    String sql = "DELETE FROM invoices WHERE id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setString(1, id);
        stmt.executeUpdate();
    } catch (SQLException e) {
        throw new RuntimeException("Failed to delete invoice", e);
    }
}
}