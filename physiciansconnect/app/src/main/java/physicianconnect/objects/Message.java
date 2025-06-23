package physicianconnect.objects;

import java.time.LocalDateTime;
import java.util.UUID;

public class Message {
    private UUID messageId;
    private String senderId;
    private String receiverId;
    private String senderType;
    private String receiverType;
    private String content;
    private LocalDateTime timestamp;
    private boolean isRead;

    public Message(String senderId, String senderType, String receiverId, String receiverType, String content) {
        this.messageId = UUID.randomUUID();
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.isRead = false;
        this.senderType = senderType;
        this.receiverType = receiverType;
    }

    // Getters and Setters
    public UUID getMessageId() { return messageId; }
    public String getSenderId() { return senderId; }
    public String getReceiverId() { return receiverId; }
    public String getContent() { return content; }
    public String getSenderType() { return senderType; }
    public String getReceiverType() { return receiverType; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public void setMessageId(UUID messageId) { this.messageId = messageId; }
} 