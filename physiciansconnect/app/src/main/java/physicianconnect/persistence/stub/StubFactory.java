package physicianconnect.persistence.stub;

import physicianconnect.persistence.interfaces.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import physicianconnect.objects.Notification;

public class StubFactory {

    public static PhysicianPersistence createPhysicianPersistence() {
        return new PhysicianPersistenceStub(true); // seeded
    }

    public static AppointmentPersistence createAppointmentPersistence() {
        return new AppointmentPersistenceStub(true); // seeded
    }

    public static MedicationPersistence createMedicationPersistence() {
        return new MedicationPersistenceStub(true); // seeded
    }

    public static PrescriptionPersistence createPrescriptionPersistence() {
        return new PrescriptionPersistenceStub(true); // seeded
    }

    public static ReferralPersistence createReferralPersistence() {
        return new ReferralPersistenceStub(true); // seeded
    }

    public static ReceptionistPersistence createReceptionistPersistence() {
        return new ReceptionistPersistenceStub(true); // seeded
    }

    public static InvoicePersistence createInvoicePersistence() {
        return new InvoicePersistenceStub(true); // seeded
    }

    public static PaymentPersistence createPaymentPersistence() {
        return new PaymentPersistenceStub(true); // seeded
    }

    public static NotificationPersistence createNotificationPersistence() {
        return new StubNotificationPersistence();
    }

    private static class StubNotificationPersistence implements NotificationPersistence {
        private final List<Notification> notifications = new ArrayList<>();

        @Override
        public void addNotification(Notification notification) {
            notifications.add(notification);
        }

        @Override
        public List<Notification> getNotificationsForUser(String userId, String userType) {
            return notifications.stream()
                .filter(n -> n.getUserId().equals(userId) && n.getUserType().equals(userType))
                .collect(Collectors.toList());
        }

        @Override
        public void clearNotificationsForUser(String userId, String userType) {
            notifications.removeIf(n -> n.getUserId().equals(userId) && n.getUserType().equals(userType));
        }
    }
}