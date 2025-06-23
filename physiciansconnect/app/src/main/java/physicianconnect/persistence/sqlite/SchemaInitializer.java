package physicianconnect.persistence.sqlite;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SchemaInitializer {

        public static void initializeSchema(Connection connection) {
                String createPhysiciansTable = "CREATE TABLE IF NOT EXISTS physicians (" +
                                "id TEXT PRIMARY KEY, " +
                                "name TEXT NOT NULL, " +
                                "email TEXT NOT NULL, " +
                                "password TEXT NOT NULL, " +
                                "specialty TEXT, " +
                                "officeHours TEXT, " +
                                "notifyAppointment BOOLEAN DEFAULT 1, " +
                                "notifyBilling BOOLEAN DEFAULT 0, " +
                                "notifyMessages BOOLEAN DEFAULT 1, " +
                                "phone TEXT, " +
                                "officeAddress TEXT" +
                                ");";

                String createAppointmentsTable = "CREATE TABLE IF NOT EXISTS appointments ("
                                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                                + "physician_id TEXT NOT NULL, "
                                + "patient_name TEXT NOT NULL, "
                                + "datetime TEXT NOT NULL, "
                                + "notes TEXT, "
                                + "FOREIGN KEY (physician_id) REFERENCES physicians(id) ON DELETE CASCADE, "
                                + "UNIQUE (physician_id, patient_name, datetime)"
                                + ");";

                String createMedicationsTable = "CREATE TABLE IF NOT EXISTS medications ("
                                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                                + "name TEXT NOT NULL, "
                                + "dosage TEXT NOT NULL, "
                                + "default_frequency TEXT, "
                                + "default_notes TEXT, "
                                + "UNIQUE (name, dosage)"
                                + ");";

                String createPrescriptionsTable = "CREATE TABLE IF NOT EXISTS prescriptions ("
                                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                                + "physician_id TEXT NOT NULL, "
                                + "patient_name TEXT NOT NULL, "
                                + "medication_name TEXT NOT NULL, "
                                + "default_dosage TEXT NOT NULL, "
                                + "dosage TEXT, "
                                + "frequency TEXT, "
                                + "notes TEXT, "
                                + "date_prescribed TEXT NOT NULL, "
                                + "FOREIGN KEY (physician_id) REFERENCES physicians(id) ON DELETE CASCADE, "
                                + "UNIQUE (physician_id, patient_name, medication_name, date_prescribed)"
                                + ");";

                String createReferralsTable = "CREATE TABLE IF NOT EXISTS referrals ("
                                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                                + "physician_id TEXT NOT NULL, "
                                + "patient_name TEXT NOT NULL, "
                                + "referral_type TEXT NOT NULL, "
                                + "details TEXT, "
                                + "date_created TEXT NOT NULL, "
                                + "FOREIGN KEY (physician_id) REFERENCES physicians(id) ON DELETE CASCADE, "
                                + "UNIQUE (physician_id, patient_name, referral_type, date_created)"
                                + ");";

                String createReceptionistTable = "CREATE TABLE IF NOT EXISTS receptionists ("
                                + "id TEXT PRIMARY KEY, "
                                + "name TEXT NOT NULL, "
                                + "email TEXT NOT NULL, "
                                + "password TEXT NOT NULL,"
                                + "notifyAppointment BOOLEAN DEFAULT TRUE,"
                                + "notifyBilling BOOLEAN DEFAULT TRUE,"
                                + "notifyMessages BOOLEAN DEFAULT TRUE"
                                + ");";

                String createInvoicesTable = "CREATE TABLE IF NOT EXISTS invoices ("
                                + "id TEXT PRIMARY KEY, "
                                + "appointment_id INTEGER, "
                                + "patient_name TEXT, "
                                + "services TEXT, "
                                + "insurance_adjustment REAL, "
                                + "total_amount REAL, "
                                + "balance REAL, "
                                + "status TEXT, "
                                + "created_at TEXT, "
                                + "FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE CASCADE"
                                + ");";

                String createPaymentsTable = "CREATE TABLE IF NOT EXISTS payments ("
                                + "id TEXT PRIMARY KEY, "
                                + "invoice_id TEXT, "
                                + "amount REAL, "
                                + "method TEXT, "
                                + "paid_at TEXT"
                                + ");";

                String createMessagesTable = "CREATE TABLE IF NOT EXISTS messages (" +
                                "message_id TEXT PRIMARY KEY, " +
                                "sender_id TEXT NOT NULL, " +
                                "sender_type TEXT NOT NULL, " +
                                "receiver_id TEXT NOT NULL, " +
                                "receiver_type TEXT NOT NULL, " +
                                "content TEXT NOT NULL, " +
                                "timestamp TEXT NOT NULL, " +
                                "is_read BOOLEAN NOT NULL" +
                                ");";

                String createNotificationsTable = "CREATE TABLE IF NOT EXISTS notifications (" +
                                "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                                + "user_id TEXT NOT NULL, "
                                + "user_type TEXT NOT NULL, "
                                + "message TEXT NOT NULL, "
                                + "type TEXT NOT NULL, "
                                + "timestamp TEXT NOT NULL, "
                                + "FOREIGN KEY (user_id) REFERENCES physicians(id) ON DELETE CASCADE"
                                + ");";

                try (Statement stmt = connection.createStatement()) {
                        stmt.execute("PRAGMA foreign_keys = ON;");
                        stmt.execute(createPhysiciansTable);
                        stmt.execute(createAppointmentsTable);
                        stmt.execute(createMedicationsTable);
                        stmt.execute(createPrescriptionsTable);
                        stmt.execute(createReferralsTable);
                        stmt.execute(createReceptionistTable);
                        stmt.execute(createInvoicesTable);
                        stmt.execute(createPaymentsTable);
                        stmt.execute(createMessagesTable);
                        stmt.execute(createNotificationsTable);
                } catch (SQLException e) {
                        throw new RuntimeException("Failed to initialize PhysicianConnect schema", e);
                }
        }
}