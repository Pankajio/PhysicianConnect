package physicianconnect.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import physicianconnect.persistence.interfaces.*;
import physicianconnect.persistence.sqlite.*;
import physicianconnect.persistence.stub.StubFactory;

public class PersistenceFactory {

    private static PhysicianPersistence physicianPersistence;
    private static AppointmentPersistence appointmentPersistence;
    private static MedicationPersistence medicationPersistence;
    private static PrescriptionPersistence prescriptionPersistence;
    private static ReferralPersistence referralPersistence;
    private static MessageRepository messageRepository;
    private static ReceptionistPersistence receptionistPersistence;
    private static InvoicePersistence invoicePersistence;
    private static PaymentPersistence paymentPersistence;
    private static NotificationPersistence notificationPersistence;

    public static void initialize(PersistenceType type, boolean seed) {
        if (physicianPersistence != null || appointmentPersistence != null || medicationPersistence != null
                || prescriptionPersistence != null || referralPersistence != null || messageRepository != null
                || receptionistPersistence != null || invoicePersistence != null || paymentPersistence != null 
                || notificationPersistence != null)
            return;

        switch (type) {
            case PROD, TEST -> {
                String dbPath = type == PersistenceType.PROD ? "prod.db" : "test.db";
                try {
                    ConnectionManager.initialize(dbPath);
                    Connection conn = ConnectionManager.get();

                    SchemaInitializer.initializeSchema(conn);

                    if (seed) {
                        DatabaseSeeder.seed(conn, List.of(
                                "database_seeds/seed_physicians.sql",
                                "database_seeds/seed_appointments.sql",
                                "database_seeds/seed_medications.sql",
                                "database_seeds/seed_prescriptions.sql",
                                "database_seeds/seed_referrals.sql",
                                "database_seeds/seed_receptionists.sql",
                                "database_seeds/seed_invoices.sql",
                                "database_seeds/seed_payments.sql"));
                    }

                    physicianPersistence = new PhysicianDB(conn);
                    appointmentPersistence = new AppointmentDB(conn);
                    medicationPersistence = new MedicationDB(conn);
                    prescriptionPersistence = new PrescriptionDB(conn);
                    referralPersistence = new ReferralDB(conn);
                    messageRepository = new MessageDB(conn);
                    receptionistPersistence = new ReceptionistDB(conn);
                    invoicePersistence = new InvoiceDB(conn);
                    paymentPersistence = new PaymentDB(conn);
                    notificationPersistence = new NotificationDB(conn, getReceptionistPersistence());

                    /*
                     * In production this line wouldn't exist but because we want to make
                     * it convienient for you, we add a test user that you can use to login
                     * instead of having to make an account, WHICH OUR APP CAN DO!!!
                     * And the test user comes with pre loaded appointments
                     */
                    injectTestUserForGrader();

                } catch (Exception e) {
                    fallbackToStubs(e);
                }
            }
            case STUB -> fallbackToStubs(null);
        }
    }

    private static void fallbackToStubs(Exception e) {
        physicianPersistence = StubFactory.createPhysicianPersistence();
        appointmentPersistence = StubFactory.createAppointmentPersistence();
        medicationPersistence = StubFactory.createMedicationPersistence();
        prescriptionPersistence = StubFactory.createPrescriptionPersistence();
        referralPersistence = StubFactory.createReferralPersistence();
        messageRepository = new InMemoryMessageRepository();
        receptionistPersistence = StubFactory.createReceptionistPersistence();
        invoicePersistence = StubFactory.createInvoicePersistence();
        paymentPersistence = StubFactory.createPaymentPersistence();
        notificationPersistence = StubFactory.createNotificationPersistence();

        if (e != null) {
            System.err.println("Falling back to stubs due to: " + e.getMessage());
        }
    }

    public static PhysicianPersistence getPhysicianPersistence() {
        return physicianPersistence;
    }

    public static AppointmentPersistence getAppointmentPersistence() {
        return appointmentPersistence;
    }

    public static MedicationPersistence getMedicationPersistence() {
        return medicationPersistence;
    }

    public static PrescriptionPersistence getPrescriptionPersistence() {
        return prescriptionPersistence;
    }

    public static ReferralPersistence getReferralPersistence() {
        return referralPersistence;
    }

    public static MessageRepository getMessageRepository() {
        return messageRepository;
    }

    public static ReceptionistPersistence getReceptionistPersistence() {
        return receptionistPersistence;
    }

    public static InvoicePersistence getInvoicePersistence() {
        return invoicePersistence;
    }

    public static PaymentPersistence getPaymentPersistence() {
        return paymentPersistence;

    }

    public static NotificationPersistence getNotificationPersistence() {
        if (notificationPersistence == null) {
            try {
                Connection conn = ConnectionManager.get();
                notificationPersistence = new NotificationDB(conn, getReceptionistPersistence());
            } catch (Exception e) {
                e.printStackTrace();
                notificationPersistence = StubFactory.createNotificationPersistence();
            }
        }
        return notificationPersistence;
    }

    public static void reset() {
        ConnectionManager.close();
        physicianPersistence = null;
        appointmentPersistence = null;
        medicationPersistence = null;
        prescriptionPersistence = null;
        referralPersistence = null;
        messageRepository = null;
        receptionistPersistence = null;
        invoicePersistence = null;
        paymentPersistence = null;
        notificationPersistence = null;
    }

    private static void injectTestUserForGrader() {
        String testEmail = "testP@email.com";
        String testId = "0";
        String testName = "Dr. Stephen Vincent Strange";
        String testPassword = "test123";

        String receptionistId = "0";
        String receptionistEmail = "testR@email.com";
        String receptionistName = "Mrs. Christine Palmer";

        boolean testUserExists = physicianPersistence.getAllPhysicians().stream()
                .anyMatch(p -> p.getEmail().equalsIgnoreCase(testEmail));

        boolean receptionistExists = receptionistPersistence.getAllReceptionists().stream()
                .anyMatch(r -> r.getEmail().equalsIgnoreCase(receptionistEmail));

        if (!testUserExists) {
            physicianPersistence.addPhysician(
                    new physicianconnect.objects.Physician(testId, testName, testEmail, testPassword));

            // Add fake appointments after adding the physician
            if (appointmentPersistence != null) {
                appointmentPersistence.addAppointment(new physicianconnect.objects.Appointment(
                        testId, "Peter Parker", LocalDateTime.of(2025, 6, 1, 9, 0)));
                appointmentPersistence.addAppointment(new physicianconnect.objects.Appointment(
                        testId, "Tony Stark", LocalDateTime.of(2025, 6, 2, 13, 30)));
                appointmentPersistence.addAppointment(new physicianconnect.objects.Appointment(
                        testId, "Wanda Maximoff", LocalDateTime.of(2025, 6, 3, 11, 0)));
            }
        }

        if (!receptionistExists) {
            receptionistPersistence.addReceptionist(
                    new physicianconnect.objects.Receptionist(receptionistId, receptionistName, receptionistEmail,
                            testPassword));
        }

    }

}