package physicianconnect.persistence.sqlite;

import physicianconnect.objects.Prescription;
import physicianconnect.persistence.interfaces.PrescriptionPersistence;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionDB implements PrescriptionPersistence {
    private final Connection connection;

    public PrescriptionDB(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void addPrescription(Prescription p) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO prescriptions (physician_id, patient_name, medication_name, default_dosage, dosage, frequency, notes, date_prescribed) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
            stmt.setString(1, p.getPhysicianId());
            stmt.setString(2, p.getPatientName());
            stmt.setString(3, p.getMedicationName());
            stmt.setString(4, p.getDefaultDosage());
            stmt.setString(5, p.getDosage());
            stmt.setString(6, p.getFrequency());
            stmt.setString(7, p.getNotes());
            stmt.setString(8, p.getDatePrescribed());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Prescription> getPrescriptionsForPatient(String patientName) {
        List<Prescription> list = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM prescriptions WHERE patient_name = ? ORDER BY date_prescribed DESC")) {
            stmt.setString(1, patientName);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new Prescription(
                        rs.getInt("id"),
                        rs.getString("physician_id"),
                        rs.getString("patient_name"),
                        rs.getString("medication_name"),
                        rs.getString("default_dosage"),
                        rs.getString("dosage"),
                        rs.getString("frequency"),
                        rs.getString("notes"),
                        rs.getString("date_prescribed")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public List<Prescription> getAllPrescriptions() {
        List<Prescription> list = new ArrayList<>();
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM prescriptions");
            while (rs.next()) {
                list.add(new Prescription(
                        rs.getInt("id"),
                        rs.getString("physician_id"),
                        rs.getString("patient_name"),
                        rs.getString("medication_name"),
                        rs.getString("default_dosage"),
                        rs.getString("dosage"),
                        rs.getString("frequency"),
                        rs.getString("notes"),
                        rs.getString("date_prescribed")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get all prescriptions", e);
        }
        return list;
    }

    @Override
    public void deletePrescriptionById(int id) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM prescriptions WHERE id = ?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete prescription by id", e);
        }
    }

    @Override
    public void deleteAllPrescriptions() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM prescriptions");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete all prescriptions", e);
        }
    }
}