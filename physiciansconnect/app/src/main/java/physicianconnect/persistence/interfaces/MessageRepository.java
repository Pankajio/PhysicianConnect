package physicianconnect.persistence.interfaces;

import physicianconnect.objects.Message;
import java.util.List;
import java.util.UUID;

public interface MessageRepository {
    Message save(Message message);

    List<Message> findByReceiverId(String receiverId, String receiverType);
    List<Message> findBySenderId(String senderId, String senderType);
    List<Message> findUnreadByReceiverId(String receiverId, String receiverType);

    void markAsRead(UUID messageId);

    int countUnreadMessages(String receiverId, String receiverType);
}