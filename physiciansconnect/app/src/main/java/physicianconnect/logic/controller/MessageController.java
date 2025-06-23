package physicianconnect.logic.controller;

import physicianconnect.logic.exceptions.InvalidMessageException;
import physicianconnect.logic.MessageService;
import physicianconnect.objects.Message;

import java.util.List;
import java.util.UUID;

/**
 * Controller for message‐related use cases.
 * Delegates to MessageService for persistence and business logic.
 */
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * Send a new message from sender → receiver.
     *
     * @param senderId     the ID of the sending user
     * @param senderType   the type of the sending user
     * @param receiverId   the ID of the recipient user
     * @param receiverType the type of the recipient user
     * @param content      the message text
     * @return the saved Message object (with ID/timestamp populated)
     * @throws InvalidMessageException if content is blank or IDs/types are invalid
     */
    public Message sendMessage(String senderId, String senderType, String receiverId, String receiverType, String content)
            throws InvalidMessageException {
        if (senderId == null || senderId.isBlank()) {
            throw new InvalidMessageException("Sender ID cannot be blank.");
        }
        if (senderType == null || senderType.isBlank()) {
            throw new InvalidMessageException("Sender type cannot be blank.");
        }
        if (receiverId == null || receiverId.isBlank()) {
            throw new InvalidMessageException("Receiver ID cannot be blank.");
        }
        if (receiverType == null || receiverType.isBlank()) {
            throw new InvalidMessageException("Receiver type cannot be blank.");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new InvalidMessageException("Message content cannot be empty.");
        }
        return messageService.sendMessage(senderId, senderType, receiverId, receiverType, content.trim());
    }

    /**
     * Retrieve all messages (sent & received) for a given user, sorted by timestamp.
     *
     * @param userId   the ID of the user
     * @param userType the type of the user
     * @return a List of Message objects
     */
    public List<Message> getAllMessagesForUser(String userId, String userType) {
        return messageService.getMessagesForUser(userId, userType);
    }

    /**
     * Retrieve only unread messages for a user.
     *
     * @param userId   the ID of the user
     * @param userType the type of the user
     * @return a List of unread Message objects
     */
    public List<Message> getUnreadMessagesForUser(String userId, String userType) {
        return messageService.getUnreadMessagesForUser(userId, userType);
    }

    /**
     * Mark a specific message as read.
     *
     * @param messageId the UUID of the message to mark as read
     */
    public void markMessageAsRead(UUID messageId) {
        messageService.markMessageAsRead(messageId);
    }

    /**
     * Count how many unread messages a user has.
     *
     * @param userId   the ID of the user
     * @param userType the type of the user
     * @return the number of unread messages
     */
    public int getUnreadMessageCount(String userId, String userType) {
        return messageService.getUnreadMessageCount(userId, userType);
    }
}