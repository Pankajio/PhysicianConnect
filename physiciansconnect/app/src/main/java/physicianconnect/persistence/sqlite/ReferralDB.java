package physicianconnect.persistence.sqlite;

import physicianconnect.objects.Referral;
import physicianconnect.persistence.interfaces.ReferralPersistence;

import java.sql.*;
import java.util.*;

public class ReferralDB implements ReferralPersistence {
    private final Connection connection;

    public ReferralDB(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void addReferral(Referral referral) {
        String sql = "INSERT INTO referrals (physician_id, patient_name, referral_type, details, date_created) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, referral.getPhysicianId());
            stmt.setString(2, referral.getPatientName());
            stmt.setString(3, referral.getReferralType());
            stmt.setString(4, referral.getDetails());
            stmt.setString(5, referral.getDateCreated());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add referral", e);
        }
    }

    @Override
    public List<Referral> getReferralsForPhysician(String physicianId) {
        List<Referral> list = new ArrayList<>();
        String sql = "SELECT * FROM referrals WHERE physician_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, physicianId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new Referral(
                    rs.getInt("id"),
                    rs.getString("physician_id"),
                    rs.getString("patient_name"),
                    rs.getString("referral_type"),
                    rs.getString("details"),
                    rs.getString("date_created")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch referrals", e);
        }
        return list;
    }

    @Override
    public List<Referral> getReferralsForPatient(String patientName) {
        List<Referral> list = new ArrayList<>();
        String sql = "SELECT * FROM referrals WHERE patient_name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, patientName);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new Referral(
                    rs.getInt("id"),
                    rs.getString("physician_id"),
                    rs.getString("patient_name"),
                    rs.getString("referral_type"),
                    rs.getString("details"),
                    rs.getString("date_created")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch referrals", e);
        }
        return list;
    }

    @Override
    public void deleteReferralById(int id) {
        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM referrals WHERE id = ?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete referral", e);
        }
    }

    @Override
    public void deleteAllReferrals() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM referrals");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete all referrals", e);
        }
    }
}