package physicianconnect.presentation;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import physicianconnect.presentation.config.UITheme;
import physicianconnect.presentation.config.UIConfig;
import physicianconnect.objects.Notification;
import physicianconnect.persistence.interfaces.NotificationPersistence;
import physicianconnect.persistence.sqlite.NotificationDB;

public class NotificationPanel extends JPanel {
    private final DefaultListModel<Notification> notificationListModel;
    private final JList<Notification> notificationList;
    private static final int MAX_NOTIFICATIONS = 10;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("MMM d, h:mm a");
    private final NotificationPersistence notificationPersistence;
    private final String userId;
    private final String userType;
    private final List<Notification> unreadNotifications;
    private LocalDateTime lastViewedTime;

    public NotificationPanel(NotificationPersistence notificationPersistence, String userId, String userType) {
        this.notificationPersistence = notificationPersistence;
        this.userId = userId;
        this.userType = userType;
        this.unreadNotifications = new ArrayList<>();
        this.lastViewedTime = LocalDateTime.MIN;
        
        setLayout(new BorderLayout(10, 10));
        setBackground(UITheme.BACKGROUND_COLOR);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Header
        JLabel titleLabel = new JLabel("Recent Notifications");
        titleLabel.setFont(UITheme.HEADER_FONT);
        titleLabel.setForeground(UITheme.TEXT_COLOR);
        add(titleLabel, BorderLayout.NORTH);

        // Notification list
        notificationListModel = new DefaultListModel<>();
        notificationList = new JList<>(notificationListModel);
        notificationList.setCellRenderer(new NotificationCellRenderer());
        notificationList.setBackground(UITheme.BACKGROUND_COLOR);
        notificationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        notificationList.setBorder(BorderFactory.createLineBorder(UITheme.PRIMARY_COLOR, 1));

        JScrollPane scrollPane = new JScrollPane(notificationList);
        scrollPane.setPreferredSize(new Dimension(300, 400));
        add(scrollPane, BorderLayout.CENTER);

        // Load existing notifications
        loadNotifications();
    }

    /**
     * Load notifications from persistence
     */
    public void loadNotifications() {
        notificationListModel.clear();
        unreadNotifications.clear();
        List<Notification> storedNotifications = notificationPersistence.getNotificationsForUser(userId, userType);
        
        // Sort notifications by timestamp, newest first
        storedNotifications.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
        
        for (Notification notification : storedNotifications) {
            notificationListModel.addElement(notification);
            // Add to unread if it's not marked as read
            if (!notification.isRead()) {
                unreadNotifications.add(notification);
            }
        }
        
        // Ensure the UI is updated
        revalidate();
        repaint();
    }

    public void addNotification(String message, String type) {
        // Check if a similar notification already exists in the last few seconds
        LocalDateTime now = LocalDateTime.now();
        boolean duplicateExists = false;
        
        for (int i = 0; i < notificationListModel.size(); i++) {
            Notification existing = notificationListModel.get(i);
            if (existing.getMessage().equals(message) && 
                existing.getType().equals(type) && 
                existing.getTimestamp().isAfter(now.minusSeconds(5))) {
                duplicateExists = true;
                break;
            }
        }
        
        if (!duplicateExists) {
            Notification notification = new Notification(message, type, now, userId, userType);
            
            // Add to persistence
            notificationPersistence.addNotification(notification);
            
            // Add to the beginning of the list
            notificationListModel.add(0, notification);
            
            // Always add to unread notifications for new notifications
            unreadNotifications.add(notification);
            
            // Keep only the most recent notifications
            while (notificationListModel.size() > MAX_NOTIFICATIONS) {
                notificationListModel.remove(notificationListModel.size() - 1);
            }
            
            // Ensure the UI is updated
            revalidate();
            repaint();
        }
    }

    public int getUnreadNotificationCount() {
        // Force a refresh of notifications to ensure accurate count
        loadNotifications();
        return unreadNotifications.size();
    }

    public void markAllAsRead() {
        lastViewedTime = LocalDateTime.now();
        for (Notification notification : unreadNotifications) {
            if (notificationPersistence instanceof NotificationDB) {
                ((NotificationDB) notificationPersistence).markNotificationAsRead(notification);
            } else {
                notification.markAsRead();
            }
        }
        unreadNotifications.clear();
        
        // Force a refresh of notifications to ensure UI is up to date
        loadNotifications();
        
        // Ensure the UI is updated
        revalidate();
        repaint();
    }

    public void showNotificationPanel() {
        // Mark all notifications as read when panel is opened
        markAllAsRead();
    }

    private class NotificationCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof Notification) {
                Notification notification = (Notification) value;
                String typeColor;
                switch (notification.getType()) {
                    case "Appointment Cancellation!":
                    case "Invoice Deleted!":
                        typeColor = "#FF0000"; // Red
                        break;
                    case "Appointment Update!":
                        typeColor = "#FFA500"; // Orange
                        break;
                    case "New Prescription!":
                        typeColor = "#00008B"; // Dark Blue
                        break;
                    case "New Referral!":
                        typeColor = "#800080"; // Purple
                        break;
                    case "New Invoice!":
                        typeColor = "#2E7D32"; // Green
                        break;
                    case "Invoice Paid!":
                        typeColor = "#006400"; // Dark Green
                        break;
                    default:
                        typeColor = "#2E7D32"; // Default green
                }
                
                setText(String.format("<html><div style='width: 100%%; padding: 5px;'>" +
                        "<b style='color: %s;'>%s</b><br>" +
                        "<span style='color: black;'>%s</span><br>" +
                        "<i style='color: #666;'>%s</i></div></html>",
                        typeColor,
                        notification.getType(),
                        notification.getMessage(),
                        notification.getTimestamp().format(TIME_FORMATTER)));
                
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UITheme.PRIMARY_COLOR, 1),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
                
                if (isSelected) {
                    setBackground(new Color(240, 240, 240)); // Light gray for selection
                } else {
                    setBackground(Color.WHITE);
                }
            }
            return this;
        }
    }
} 