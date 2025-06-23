package physicianconnect.objects;

import java.time.LocalDateTime;

public class Notification {
    private final String message;
    private final String type;
    private final LocalDateTime timestamp;
    private final String userId;
    private final String userType;
    private boolean read;

    public Notification(String message, String type, LocalDateTime timestamp, String userId, String userType) {
        this.message = message;
        this.type = type;
        this.timestamp = timestamp;
        this.userId = userId;
        this.userType = userType;
        this.read = false;
    }

    public String getMessage() {
        return message;
    }
    public String getType() {
        return type;
    }
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public String getUserId() {
        return userId;
    }
    public String getUserType() {
        return userType;
    }
    public boolean isRead() {
        return read;
    }
    public void markAsRead() {
        this.read = true;
    }
} 