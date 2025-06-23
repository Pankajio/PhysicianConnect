package physicianconnect.persistence.sqlite;

import physicianconnect.objects.Medication;
import physicianconnect.persistence.interfaces.MedicationPersistence;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicationDB implements MedicationPersistence {

    private final Connection connection;

    public MedicationDB(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void addMedication(Medication medication) {
        String sql = "INSERT INTO medications (name, dosage, default_frequency, default_notes) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, medication.getName());
            stmt.setString(2, medication.getDosage());
            stmt.setString(3, medication.getDefaultFrequency());
            stmt.setString(4, medication.getDefaultNotes());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add medication", e);
        }
    }

    @Override
    public List<Medication> getAllMedications() {
        List<Medication> meds = new ArrayList<>();
        String sql = "SELECT name, dosage, default_frequency, default_notes FROM medications";
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                meds.add(new Medication(
                        rs.getString("name"),
                        rs.getString("dosage"),
                        rs.getString("default_frequency"),
                        rs.getString("default_notes")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load medications", e);
        }
        return meds;
    }

    @Override
    public void deleteMedication(Medication medication) {
        String sql = "DELETE FROM medications WHERE name = ? AND dosage = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, medication.getName());
            stmt.setString(2, medication.getDosage());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete medication", e);
        }
    }

    @Override
    public void deleteAllMedications() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM medications");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete all medications", e);
        }
    }
}
