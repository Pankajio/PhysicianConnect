package physicianconnect.logic.integration;

import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

import physicianconnect.logic.MessageService;
import physicianconnect.logic.controller.PhysicianController;
import physicianconnect.logic.exceptions.InvalidCredentialException;
import physicianconnect.logic.manager.PhysicianManager;
import physicianconnect.objects.Message;
import physicianconnect.objects.Physician;
import physicianconnect.persistence.ConnectionManager;
import physicianconnect.persistence.sqlite.MessageDB;
import physicianconnect.persistence.sqlite.PhysicianDB;
import physicianconnect.persistence.sqlite.SchemaInitializer;

/**
 * Full-stack integration test for:
 * ▸ physician registration + login
 * ▸ messaging workflow
 *
 * Layers touched:
 * controller → manager → DB (SQLite in-memory)
 * MessageService → MessageDB (persistence) → messages table
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PhysicianMessagingIntegrationTest {

    private Connection conn;
    private PhysicianController physicianCtrl;
    private MessageService messageService;

    /* ───────────── one-time boot-strap ───────────── */
    @BeforeAll
    void initDatabaseAndWiring() throws Exception {
        // 1️⃣ start fresh in-memory DB
        ConnectionManager.initialize(":memory:"); // jdbc:sqlite::memory:
        conn = ConnectionManager.get();

        // 2️⃣ create full schema
        SchemaInitializer.initializeSchema(conn);

        // 3️⃣ set up persistence → manager → controller
        PhysicianDB physicianDB = new PhysicianDB(conn);
        PhysicianManager physicianMgr = new PhysicianManager(physicianDB);
        physicianCtrl = new PhysicianController(physicianMgr);

        MessageDB messageDB = new MessageDB(conn);
        messageService = new MessageService(messageDB);

        // 4️⃣ optional: seed a receptionist user ID for message target
        try (Statement st = conn.createStatement()) {
            st.executeUpdate(
                    "INSERT INTO receptionists (id,name,email,password) " +
                            "VALUES ('rec-1','Reception Bot','bot@clinic.test','dummy')");
        }
    }

    @AfterAll
    void tearDown() {
        ConnectionManager.close(); // destroys :memory: database
    }

    /*
     * ────────────────────────────────────────────────
     * USER STORY 1: Register → Login
     * ────────────────────────────────────────────────
     */
    @Test
    void physicianCanRegisterAndLogin() throws Exception {
        /* WHEN: the physician registers */
        Physician registered = physicianCtrl.register(
                "Dr. Bob Brown",
                "bob.brown@clinic.test",
                "Secret123!",
                "Secret123!");

        /* THEN: row is in DB & returned object sane */
        Assertions.assertNotNull(registered.getId());
        Assertions.assertEquals("Dr. Bob Brown", registered.getName());

        /* AND WHEN: he logs in with same credentials */
        Physician loggedIn = physicianCtrl.login(
                "bob.brown@clinic.test",
                "Secret123!");

        /* THEN: login succeeds & same account returned */
        Assertions.assertEquals(registered.getId(), loggedIn.getId());
    }

    /*
     * ────────────────────────────────────────────────
     * USER STORY 2: Send → Retrieve Message
     * ────────────────────────────────────────────────
     */
    @Test
    void physicianCanSendAndRetrieveMessage() throws Exception {
        // Ensure we have a logged-in physician (reuse the one from story 1)
        Physician sender = physicianCtrl.login("bob.brown@clinic.test", "Secret123!");

        /* WHEN: he sends a message to receptionist rec-1 */
        Message sent = messageService.sendMessage(
                sender.getId(), // senderId
                sender.getUserType(), // "physician"
                "rec-1", // receiverId
                "receptionist", // receiverType
                "Hello – patient files ready?");

        /* THEN: DB assigned a UUID & timestamp */
        Assertions.assertNotNull(sent.getMessageId());
        Assertions.assertTrue(sent.getTimestamp() != null);

        /* AND WHEN: he fetches ALL messages for himself */
        List<Message> inbox = messageService.getMessagesForUser(
                sender.getId(),
                sender.getUserType());

        /* THEN: the just-sent message is present */
        Assertions.assertEquals(1, inbox.size());
        Message stored = inbox.get(0);

        Assertions.assertEquals(sent.getMessageId(), stored.getMessageId());
        Assertions.assertEquals("Hello – patient files ready?", stored.getContent());
        Assertions.assertEquals("physician", stored.getSenderType());
        Assertions.assertEquals("receptionist", stored.getReceiverType());
    }

    /*
     * ────────────────────────────────────────────────
     * Negative path (bonus): wrong password fails
     * ────────────────────────────────────────────────
     */
    @Test
    void loginFailsWithWrongPassword() {
        Assertions.assertThrows(InvalidCredentialException.class,
                () -> physicianCtrl.login("bob.brown@clinic.test", "wrongPass!"));
    }
}
