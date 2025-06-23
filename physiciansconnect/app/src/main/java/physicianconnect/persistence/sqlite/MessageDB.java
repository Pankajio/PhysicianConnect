package physicianconnect.persistence.sqlite;

import physicianconnect.objects.Message;
import physicianconnect.persistence.interfaces.MessageRepository;
import java.sql.*;
import java.util.*;
import java.time.LocalDateTime;

public class MessageDB implements MessageRepository {
    private final Connection connection;

    public MessageDB(Connection connection) {
        this.connection = connection;
        createTable();
    }

    private void createTable() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS messages (
                    message_id TEXT PRIMARY KEY,
                    sender_id TEXT NOT NULL,
                    sender_type TEXT NOT NULL,
                    receiver_id TEXT NOT NULL,
                    receiver_type TEXT NOT NULL,
                    content TEXT NOT NULL,
                    timestamp TEXT NOT NULL,
                    is_read BOOLEAN NOT NULL
                )
            """);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create messages table: " + e.getMessage(), e);
        }
    }

    @Override
    public Message save(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        if (message.getMessageId() == null) {
            throw new IllegalArgumentException("Message ID cannot be null");
        }
        if (message.getSenderId() == null || message.getSenderId().trim().isEmpty()) {
            throw new IllegalArgumentException("Sender ID cannot be null or empty");
        }
        if (message.getSenderType() == null || message.getSenderType().trim().isEmpty()) {
            throw new IllegalArgumentException("Sender type cannot be null or empty");
        }
        if (message.getReceiverId() == null || message.getReceiverId().trim().isEmpty()) {
            throw new IllegalArgumentException("Receiver ID cannot be null or empty");
        }
        if (message.getReceiverType() == null || message.getReceiverType().trim().isEmpty()) {
            throw new IllegalArgumentException("Receiver type cannot be null or empty");
        }
        if (message.getContent() == null) {
            throw new IllegalArgumentException("Content cannot be null");
        }
        if (message.getTimestamp() == null) {
            throw new IllegalArgumentException("Timestamp cannot be null");
        }

        String sql = "INSERT OR REPLACE INTO messages (message_id, sender_id, sender_type, receiver_id, receiver_type, content, timestamp, is_read) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, message.getMessageId().toString());
            pstmt.setString(2, message.getSenderId());
            pstmt.setString(3, message.getSenderType());
            pstmt.setString(4, message.getReceiverId());
            pstmt.setString(5, message.getReceiverType());
            pstmt.setString(6, message.getContent());
            pstmt.setString(7, message.getTimestamp().toString());
            pstmt.setBoolean(8, message.isRead());
            pstmt.executeUpdate();
            return message;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save message: " + e.getMessage() +
                    " (Message ID: " + message.getMessageId() + ")", e);
        }
    }

    @Override
    public List<Message> findByReceiverId(String receiverId, String receiverType) {
        if (receiverId == null || receiverId.trim().isEmpty()) {
            throw new IllegalArgumentException("Receiver ID cannot be null or empty");
        }
        if (receiverType == null || receiverType.trim().isEmpty()) {
            throw new IllegalArgumentException("Receiver type cannot be null or empty");
        }
        String sql = "SELECT * FROM messages WHERE receiver_id = ? AND receiver_type = ? ORDER BY timestamp";
        return queryMessages(sql, receiverId, receiverType, "Failed to find messages by receiver");
    }

    @Override
    public List<Message> findBySenderId(String senderId, String senderType) {
        if (senderId == null || senderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Sender ID cannot be null or empty");
        }
        if (senderType == null || senderType.trim().isEmpty()) {
            throw new IllegalArgumentException("Sender type cannot be null or empty");
        }
        String sql = "SELECT * FROM messages WHERE sender_id = ? AND sender_type = ? ORDER BY timestamp";
        return queryMessages(sql, senderId, senderType, "Failed to find messages by sender");
    }

    @Override
    public List<Message> findUnreadByReceiverId(String receiverId, String receiverType) {
        if (receiverId == null || receiverId.trim().isEmpty()) {
            throw new IllegalArgumentException("Receiver ID cannot be null or empty");
        }
        if (receiverType == null || receiverType.trim().isEmpty()) {
            throw new IllegalArgumentException("Receiver type cannot be null or empty");
        }
        String sql = "SELECT * FROM messages WHERE receiver_id = ? AND receiver_type = ? AND is_read = 0 ORDER BY timestamp";
        return queryMessages(sql, receiverId, receiverType, "Failed to find unread messages by receiver");
    }

    @Override
    public void markAsRead(UUID messageId) {
        if (messageId == null) {
            throw new IllegalArgumentException("Message ID cannot be null");
        }
        String sql = "UPDATE messages SET is_read = 1 WHERE message_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, messageId.toString());
            int updated = pstmt.executeUpdate();
            if (updated == 0) {
                throw new RuntimeException("No message found with ID: " + messageId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to mark message as read: " + e.getMessage() +
                    " (Message ID: " + messageId + ")", e);
        }
    }

    @Override
    public int countUnreadMessages(String receiverId, String receiverType) {
        if (receiverId == null || receiverId.trim().isEmpty()) {
            throw new IllegalArgumentException("Receiver ID cannot be null or empty");
        }
        if (receiverType == null || receiverType.trim().isEmpty()) {
            throw new IllegalArgumentException("Receiver type cannot be null or empty");
        }
        String sql = "SELECT COUNT(*) FROM messages WHERE receiver_id = ? AND receiver_type = ? AND is_read = 0";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, receiverId);
            pstmt.setString(2, receiverType);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to count unread messages: " + e.getMessage(), e);
        }
    }

    // Helper for queries with two parameters (id, type)
    private List<Message> queryMessages(String sql, String id, String type, String errorMessage) {
        List<Message> messages = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, type);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Message message = new Message(
                        rs.getString("sender_id"),
                        rs.getString("sender_type"),
                        rs.getString("receiver_id"),
                        rs.getString("receiver_type"),
                        rs.getString("content"));
                message.setMessageId(UUID.fromString(rs.getString("message_id")));
                message.setTimestamp(LocalDateTime.parse(rs.getString("timestamp")));
                message.setRead(rs.getBoolean("is_read"));
                messages.add(message);
            }
            return messages;
        } catch (SQLException e) {
            throw new RuntimeException(errorMessage + ": " + e.getMessage(), e);
        }
    }
}