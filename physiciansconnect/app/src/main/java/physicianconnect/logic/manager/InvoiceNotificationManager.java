package physicianconnect.logic.manager;

import java.awt.Window;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import physicianconnect.objects.Notification;
import physicianconnect.persistence.interfaces.NotificationPersistence;
import physicianconnect.persistence.sqlite.NotificationDB;
import physicianconnect.presentation.NotificationBanner;
import physicianconnect.presentation.NotificationPanel;

public class InvoiceNotificationManager {
    private final NotificationBanner banner;
    private final NotificationPanel notificationPanel;
    private final NotificationDB notificationDB;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("MMM d, h:mm a");

    public InvoiceNotificationManager(Window owner, NotificationPanel notificationPanel, 
                                    NotificationPersistence notificationPersistence) {
        this.banner = new NotificationBanner(owner);
        this.notificationPanel = notificationPanel;
        this.notificationDB = (NotificationDB) notificationPersistence;
    }

    public void notifyInvoiceCreated(String patientName) {
        String message = String.format("New invoice created for patient: %s", patientName);
        String type = "New Invoice!";
        
        // Show banner notification
        banner.show(message, null);
        
        // Broadcast to all receptionists
        notificationDB.broadcastToReceptionists(message, type);
    }

    public void notifyInvoicePaid(String patientName) {
        String message = String.format("Invoice paid in full for patient: %s", patientName);
        String type = "Invoice Paid!";
        
        // Show banner notification
        banner.show(message, null);
        
        // Broadcast to all receptionists
        notificationDB.broadcastToReceptionists(message, type);
    }

    public void notifyInvoiceDeleted(String patientName) {
        String message = String.format("Invoice deleted for patient: %s", patientName);
        String type = "Invoice Deleted!";
        
        // Show banner notification
        banner.show(message, null);
        
        // Broadcast to all receptionists
        notificationDB.broadcastToReceptionists(message, type);
    }
} 