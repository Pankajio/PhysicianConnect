package physicianconnect.persistence.sqlite;

import physicianconnect.objects.Receptionist;
import physicianconnect.persistence.interfaces.ReceptionistPersistence;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReceptionistDB implements ReceptionistPersistence {
    private final Connection connection;

    public ReceptionistDB(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Receptionist getReceptionistById(String id) {
        String sql = "SELECT * FROM receptionists WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Receptionist(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("password")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Receptionist getReceptionistByEmail(String email) {
        String sql = "SELECT * FROM receptionists WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Receptionist(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getBoolean("notifyAppointment"),
                        rs.getBoolean("notifyBilling"),
                        rs.getBoolean("notifyMessages"));

            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find receptionist by email", e);
        }
        return null;
    }

    @Override
    public void addReceptionist(Receptionist receptionist) {
        String sql = "INSERT OR IGNORE INTO receptionists (id, name, email, password, notifyAppointment, notifyBilling, notifyMessages) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, receptionist.getId());
            stmt.setString(2, receptionist.getName());
            stmt.setString(3, receptionist.getEmail());
            stmt.setString(4, receptionist.getPassword());
            stmt.setBoolean(5, receptionist.isNotifyAppointment());
            stmt.setBoolean(6, receptionist.isNotifyBilling());
            stmt.setBoolean(7, receptionist.isNotifyMessages());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add receptionist", e);
        }
    }

    @Override
    public List<Receptionist> getAllReceptionists() {
        List<Receptionist> list = new ArrayList<>();
        String sql = "SELECT * FROM receptionists";
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Receptionist(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getBoolean("notifyAppointment"),
                        rs.getBoolean("notifyBilling"),
                        rs.getBoolean("notifyMessages")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch receptionists", e);
        }
        return list;
    }

    @Override
    public void updateReceptionist(Receptionist receptionist) {
        String sql = """
                    UPDATE receptionists
                    SET name = ?, email = ?, password = ?,
                        notifyAppointment = ?, notifyBilling = ?, notifyMessages = ?
                    WHERE id = ?
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, receptionist.getName());
            stmt.setString(2, receptionist.getEmail());
            stmt.setString(3, receptionist.getPassword());
            stmt.setBoolean(4, receptionist.isNotifyAppointment());
            stmt.setBoolean(5, receptionist.isNotifyBilling());
            stmt.setBoolean(6, receptionist.isNotifyMessages());
            stmt.setString(7, receptionist.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update receptionist", e);
        }
    }

    @Override
    public List<String> getAllReceptionistIds() {
        List<String> ids = new ArrayList<>();
        String sql = "SELECT id FROM receptionists";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ids.add(rs.getString("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return ids;
    }

    @Override
    public void deleteReceptionist(String id) {
        String sql = "DELETE FROM receptionists WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}