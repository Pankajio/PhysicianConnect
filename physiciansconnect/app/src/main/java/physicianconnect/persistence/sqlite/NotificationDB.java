package physicianconnect.persistence.sqlite;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import physicianconnect.objects.Notification;
import physicianconnect.persistence.interfaces.NotificationPersistence;
import physicianconnect.persistence.interfaces.ReceptionistPersistence;

public class NotificationDB implements NotificationPersistence {
    private final Connection conn;
    private final ReceptionistPersistence receptionistPersistence;

    public NotificationDB(Connection conn, ReceptionistPersistence receptionistPersistence) {
        this.conn = conn;
        this.receptionistPersistence = receptionistPersistence;
        createTable();
    }

    private void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS notifications (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id TEXT NOT NULL,
                user_type TEXT NOT NULL,
                message TEXT NOT NULL,
                type TEXT NOT NULL,
                timestamp TEXT NOT NULL,
                is_read INTEGER DEFAULT 0
            )
        """;

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            
            // Check if is_read column exists
            try {
                stmt.executeQuery("SELECT is_read FROM notifications LIMIT 1");
            } catch (SQLException e) {
                // Column doesn't exist, add it
                stmt.execute("ALTER TABLE notifications ADD COLUMN is_read INTEGER DEFAULT 0");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addNotification(Notification notification) {
        String sql = "INSERT INTO notifications (user_id, user_type, message, type, timestamp, is_read) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, notification.getUserId());
            pstmt.setString(2, notification.getUserType());
            pstmt.setString(3, notification.getMessage());
            pstmt.setString(4, notification.getType());
            pstmt.setString(5, notification.getTimestamp().toString());
            pstmt.setInt(6, notification.isRead() ? 1 : 0);
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Notification> getNotificationsForUser(String userId, String userType) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? AND user_type = ? ORDER BY timestamp DESC LIMIT 10";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, userType);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Notification notification = new Notification(
                        rs.getString("message"),
                        rs.getString("type"),
                        LocalDateTime.parse(rs.getString("timestamp")),
                        rs.getString("user_id"),
                        rs.getString("user_type")
                    );
                    if (rs.getInt("is_read") == 1) {
                        notification.markAsRead();
                    }
                    notifications.add(notification);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return notifications;
    }

    @Override
    public void clearNotificationsForUser(String userId, String userType) {
        String sql = "DELETE FROM notifications WHERE user_id = ? AND user_type = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, userType);
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void markNotificationAsRead(Notification notification) {
        String sql = "UPDATE notifications SET is_read = 1 WHERE user_id = ? AND user_type = ? AND message = ? AND type = ? AND timestamp = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, notification.getUserId());
            pstmt.setString(2, notification.getUserType());
            pstmt.setString(3, notification.getMessage());
            pstmt.setString(4, notification.getType());
            pstmt.setString(5, notification.getTimestamp().toString());
            
            pstmt.executeUpdate();
            notification.markAsRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void broadcastToReceptionists(String message, String type) {
        // Get all receptionists
        List<String> receptionistIds = receptionistPersistence.getAllReceptionistIds();
        
        // Create a notification for each receptionist
        for (String receptionistId : receptionistIds) {
            // Create notification with the recipient's ID as userId
            Notification notification = new Notification(
                message,
                type,
                LocalDateTime.now(),
                receptionistId,  // This is now the recipient's ID
                "receptionist"
            );
            addNotification(notification);
        }
    }
} 