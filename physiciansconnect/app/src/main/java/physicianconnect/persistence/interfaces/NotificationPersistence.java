package physicianconnect.persistence.interfaces;

import java.util.List;
import physicianconnect.objects.Notification;

public interface NotificationPersistence {
    void addNotification(Notification notification);
    List<Notification> getNotificationsForUser(String userId, String userType);
    void clearNotificationsForUser(String userId, String userType);
} 