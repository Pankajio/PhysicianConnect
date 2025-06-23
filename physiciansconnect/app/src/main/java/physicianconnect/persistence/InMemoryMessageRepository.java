package physicianconnect.persistence;

import physicianconnect.objects.Message;
import physicianconnect.persistence.interfaces.MessageRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryMessageRepository implements MessageRepository {
    private final Map<UUID, Message> messages = new ConcurrentHashMap<>();

    @Override
    public Message save(Message message) {
        messages.put(message.getMessageId(), message);
        return message;
    }

    // Updated: now requires both receiverId and receiverType
    public List<Message> findByReceiverId(String receiverId, String receiverType) {
        return messages.values().stream()
                .filter(message -> message.getReceiverId().equals(receiverId)
                        && message.getReceiverType().equals(receiverType))
                .sorted(Comparator.comparing(Message::getTimestamp))
                .collect(Collectors.toList());
    }

    // Updated: now requires both senderId and senderType
    public List<Message> findBySenderId(String senderId, String senderType) {
        return messages.values().stream()
                .filter(message -> message.getSenderId().equals(senderId)
                        && message.getSenderType().equals(senderType))
                .sorted(Comparator.comparing(Message::getTimestamp))
                .collect(Collectors.toList());
    }

    // Updated: now requires both receiverId and receiverType
    public List<Message> findUnreadByReceiverId(String receiverId, String receiverType) {
        return messages.values().stream()
                .filter(message -> message.getReceiverId().equals(receiverId)
                        && message.getReceiverType().equals(receiverType)
                        && !message.isRead())
                .sorted(Comparator.comparing(Message::getTimestamp))
                .collect(Collectors.toList());
    }

    @Override
    public void markAsRead(UUID messageId) {
        Message message = messages.get(messageId);
        if (message != null) {
            message.setRead(true);
            messages.put(messageId, message);
        }
    }

    // Updated: now requires both receiverId and receiverType
    public int countUnreadMessages(String receiverId, String receiverType) {
        return (int) messages.values().stream()
                .filter(message -> message.getReceiverId().equals(receiverId)
                        && message.getReceiverType().equals(receiverType)
                        && !message.isRead())
                .count();
    }
}
