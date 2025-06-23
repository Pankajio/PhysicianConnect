package physicianconnect.logic;

import physicianconnect.objects.Message;
import physicianconnect.persistence.interfaces.MessageRepository;

import java.util.List;
import java.util.UUID;

public class MessageService {
    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Message sendMessage(String senderId, String senderType, String receiverId, String receiverType,
            String content) {
        Message message = new Message(senderId, senderType, receiverId, receiverType, content);
        return messageRepository.save(message);
    }

    public List<Message> getMessagesForUser(String userId, String userType) {
        List<Message> receivedMessages = messageRepository.findByReceiverId(userId, userType);
        List<Message> sentMessages = messageRepository.findBySenderId(userId, userType);
        List<Message> allMessages = new java.util.ArrayList<>();
        allMessages.addAll(receivedMessages);
        allMessages.addAll(sentMessages);
        allMessages.sort((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()));
        return allMessages;
    }

    public List<Message> getUnreadMessagesForUser(String userId, String userType) {
        return messageRepository.findUnreadByReceiverId(userId, userType);
    }

    public int getUnreadMessageCount(String userId, String userType) {
        return messageRepository.countUnreadMessages(userId, userType);
    }

    public void markMessageAsRead(UUID messageId) {
        messageRepository.markAsRead(messageId);
    }

}