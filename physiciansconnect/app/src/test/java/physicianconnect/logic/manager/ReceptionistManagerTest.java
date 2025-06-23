package physicianconnect.logic.manager;

import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import physicianconnect.objects.Receptionist;
import physicianconnect.persistence.PersistenceFactory;
import physicianconnect.persistence.PersistenceType;
import physicianconnect.persistence.interfaces.ReceptionistPersistence;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.nio.file.Path;

public class ReceptionistManagerTest {

    private ReceptionistManager manager;
    private ReceptionistPersistence receptionistDB;

    @BeforeEach
    public void setup() {
        PersistenceFactory.initialize(PersistenceType.TEST, false);
        receptionistDB = PersistenceFactory.getReceptionistPersistence();
        manager = new ReceptionistManager(receptionistDB);
    }

    @AfterEach
    public void teardown() {
        PersistenceFactory.reset();
    }

    @Test
    public void testAddAndRetrieveReceptionist() {
        Receptionist r = new Receptionist("r001", "Alice", "alice@clinic.com", "pass");
        manager.addReceptionist(r);

        Receptionist fetched = manager.getReceptionistById("r001");
        assertNotNull(fetched);
        assertEquals("Alice", fetched.getName());
    }

    @Test
    public void testDuplicateReceptionistIsIgnored() {
        Receptionist r1 = new Receptionist("r002", "First", "f@f.com", "pw");
        Receptionist r2 = new Receptionist("r002", "Second", "s@s.com", "pw");

        manager.addReceptionist(r1);
        manager.addReceptionist(r2);

        Receptionist result = manager.getReceptionistById("r002");
        assertEquals("First", result.getName());
    }

    @Test
    public void testAddReceptionistNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> manager.addReceptionist(null));
    }

    @Test
    public void testAddReceptionistBlankIdThrows() {
        Receptionist r = new Receptionist("   ", "Name", "e@e.com", "pw");
        assertThrows(IllegalArgumentException.class, () -> manager.addReceptionist(r));
    }

    @Test
    public void testAddReceptionistNullIdThrows() {
        Receptionist r = new Receptionist(null, "Name", "e@e.com", "pw");
        assertThrows(IllegalArgumentException.class, () -> manager.addReceptionist(r));
    }

    @Test
    public void testGetAllReceptionistsDelegates() {
        ReceptionistPersistence mockDB = mock(ReceptionistPersistence.class);
        ReceptionistManager mgr = new ReceptionistManager(mockDB);
        List<Receptionist> list = Collections.singletonList(new Receptionist("id", "n", "e@e.com", "p"));
        when(mockDB.getAllReceptionists()).thenReturn(list);
        List<Receptionist> result = mgr.getAllReceptionists();
        assertEquals(1, result.size());
        assertEquals("id", result.get(0).getId());
    }

    // --- Coverage for getReceptionistByEmail ---
    @Test
    public void testGetReceptionistByEmailReturnsCorrectReceptionist() {
        ReceptionistPersistence mockDB = mock(ReceptionistPersistence.class);
        Receptionist r1 = new Receptionist("id1", "Name1", "test1@email.com", "pw1");
        Receptionist r2 = new Receptionist("id2", "Name2", "test2@email.com", "pw2");
        when(mockDB.getAllReceptionists()).thenReturn(List.of(r1, r2));
        ReceptionistManager mgr = new ReceptionistManager(mockDB);

        Receptionist found = mgr.getReceptionistByEmail("test2@email.com");
        assertNotNull(found);
        assertEquals("id2", found.getId());
    }

    @Test
    public void testGetReceptionistByEmailReturnsNullIfNotFound() {
        ReceptionistPersistence mockDB = mock(ReceptionistPersistence.class);
        when(mockDB.getAllReceptionists()).thenReturn(List.of());
        ReceptionistManager mgr = new ReceptionistManager(mockDB);

        Receptionist found = mgr.getReceptionistByEmail("notfound@email.com");
        assertNull(found);
    }

    // --- Coverage for login ---
    @Test
    public void testLoginSuccess() {
        ReceptionistPersistence mockDB = mock(ReceptionistPersistence.class);
        Receptionist r = new Receptionist("id", "Name", "login@email.com", "pw123");
        when(mockDB.getAllReceptionists()).thenReturn(List.of(r));
        ReceptionistManager mgr = new ReceptionistManager(mockDB);

        Receptionist loggedIn = mgr.login("login@email.com", "pw123");
        assertNotNull(loggedIn);
        assertEquals("id", loggedIn.getId());
    }

    @Test
    public void testLoginWrongPasswordReturnsNull() {
        ReceptionistPersistence mockDB = mock(ReceptionistPersistence.class);
        Receptionist r = new Receptionist("id", "Name", "login@email.com", "pw123");
        when(mockDB.getAllReceptionists()).thenReturn(List.of(r));
        ReceptionistManager mgr = new ReceptionistManager(mockDB);

        Receptionist loggedIn = mgr.login("login@email.com", "wrongpw");
        assertNull(loggedIn);
    }

    @Test
    public void testLoginNotFoundReturnsNull() {
        ReceptionistPersistence mockDB = mock(ReceptionistPersistence.class);
        when(mockDB.getAllReceptionists()).thenReturn(List.of());
        ReceptionistManager mgr = new ReceptionistManager(mockDB);

        Receptionist loggedIn = mgr.login("notfound@email.com", "pw");
        assertNull(loggedIn);
    }

    // --- Coverage for validateAndUpdateReceptionist ---
@Test
public void testValidateAndUpdateReceptionistUpdatesFieldsAndCallsUpdate() {
    ReceptionistPersistence mockDB = mock(ReceptionistPersistence.class);
    ReceptionistManager mgr = new ReceptionistManager(mockDB);
    Receptionist r = spy(new Receptionist("id", "OldName", "old@email.com", "pw"));

    
    ReceptionistManager mgrSpy = spy(mgr);
    doNothing().when(mgrSpy).validateBasicInfo(any());

    mgrSpy.validateAndUpdateReceptionist(r, "NewName", true, false, true);

    verify(r).setName("NewName");
    verify(r).setNotifyAppointment(true);
    verify(r).setNotifyBilling(false);
    verify(r).setNotifyMessages(true);
    verify(mgrSpy).validateBasicInfo(r);
    verify(mockDB).updateReceptionist(r); // <-- verify persistence, not private method
}

    @Test
    public void testValidateAndUpdateReceptionistNullThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            manager.validateAndUpdateReceptionist(null, "Name", true, true, true)
        );
    }

    // --- Coverage for updateReceptionist (private) ---
    @Test
    public void testUpdateReceptionistNullThrows() throws Exception {
        ReceptionistManager mgr = new ReceptionistManager(mock(ReceptionistPersistence.class));
        var m = ReceptionistManager.class.getDeclaredMethod("updateReceptionist", Receptionist.class);
        m.setAccessible(true);
        assertThrows(IllegalArgumentException.class, () -> {
            try {
                m.invoke(mgr, new Object[]{null});
            } catch (Exception e) {
                throw e.getCause();
            }
        });
    }

    @Test
    public void testUpdateReceptionistNullIdThrows() throws Exception {
        ReceptionistManager mgr = new ReceptionistManager(mock(ReceptionistPersistence.class));
        Receptionist r = new Receptionist(null, "Name", "e@e.com", "pw");
        var m = ReceptionistManager.class.getDeclaredMethod("updateReceptionist", Receptionist.class);
        m.setAccessible(true);
        assertThrows(IllegalArgumentException.class, () -> {
            try {
                m.invoke(mgr, r);
            } catch (Exception e) {
                throw e.getCause();
            }
        });
    }

    @Test
    public void testUpdateReceptionistCallsPersistence() throws Exception {
        ReceptionistPersistence mockDB = mock(ReceptionistPersistence.class);
        ReceptionistManager mgr = new ReceptionistManager(mockDB);
        Receptionist r = new Receptionist("id", "Name", "e@e.com", "pw");
        var m = ReceptionistManager.class.getDeclaredMethod("updateReceptionist", Receptionist.class);
        m.setAccessible(true);
        m.invoke(mgr, r);
        verify(mockDB).updateReceptionist(r);
    }

    // --- Existing validation and photo tests unchanged ---

    @Test
    public void testValidationRejectsEmptyName() {
        Receptionist r = new Receptionist("r888", "", "valid@email.com", "x");
        Exception ex = assertThrows(IllegalArgumentException.class, () -> manager.validateBasicInfo(r));
        assertEquals("Name cannot be empty.", ex.getMessage());
    }

    @Test
    public void testValidationRejectsNullName() {
        Receptionist r = new Receptionist("r888", null, "valid@email.com", "x");
        Exception ex = assertThrows(IllegalArgumentException.class, () -> manager.validateBasicInfo(r));
        assertEquals("Name cannot be empty.", ex.getMessage());
    }

    @Test
    public void testValidationRejectsInvalidEmail() {
        Receptionist r = new Receptionist("r999", "Name", "notAnEmail", "x");
        Exception ex = assertThrows(IllegalArgumentException.class, () -> manager.validateBasicInfo(r));
        assertEquals("Invalid email format.", ex.getMessage());
    }

    @Test
    public void testValidationRejectsBlankEmail() {
        Receptionist r = new Receptionist("r999", "Name", "   ", "x");
        Exception ex = assertThrows(IllegalArgumentException.class, () -> manager.validateBasicInfo(r));
        assertEquals("Invalid email format.", ex.getMessage());
    }

    @Test
    public void testValidationRejectsNullEmail() {
        Receptionist r = new Receptionist("r999", "Name", null, "x");
        Exception ex = assertThrows(IllegalArgumentException.class, () -> manager.validateBasicInfo(r));
        assertEquals("Invalid email format.", ex.getMessage());
    }

    @Test
    public void testValidationPassesValidInput() {
        Receptionist r = new Receptionist("r777", "Bob", "bob@clinic.com", "x");
        assertDoesNotThrow(() -> manager.validateBasicInfo(r));
    }

    @Test
    public void testUploadProfilePhotoInvalidImageThrows() {
        InputStream badStream = new ByteArrayInputStream(new byte[]{1, 2, 3});
        assertThrows(RuntimeException.class, () -> manager.uploadProfilePhoto("id", badStream));
    }

    @Test
    public void testUploadProfilePhotoIOExceptionThrows() {
        ReceptionistManager mgr = new ReceptionistManager(mock(ReceptionistPersistence.class));
        try (var imageIOMock = mockStatic(ImageIO.class)) {
            imageIOMock.when(() -> ImageIO.read(any(InputStream.class))).thenThrow(new IOException("fail"));
            InputStream stream = new ByteArrayInputStream(new byte[]{1, 2, 3});
            assertThrows(RuntimeException.class, () -> mgr.uploadProfilePhoto("id", stream));
        }
    }

    @Test
    void testUploadProfilePhotoHappyPath() throws Exception {
        BufferedImage img = new BufferedImage(400, 300, BufferedImage.TYPE_INT_ARGB);
        InputStream stream = new ByteArrayInputStream(new byte[]{1, 2, 3});
        ReceptionistPersistence receptionistDB = mock(ReceptionistPersistence.class);
        ReceptionistManager manager = new ReceptionistManager(receptionistDB);

        try (
            MockedStatic<ImageIO> imageIOMock = mockStatic(ImageIO.class);
            MockedStatic<Files> filesMock = mockStatic(Files.class)
        ) {
            imageIOMock.when(() -> ImageIO.read(any(InputStream.class))).thenReturn(img);
            imageIOMock.when(() -> ImageIO.write(any(BufferedImage.class), eq("png"), any(File.class))).thenReturn(true);
            filesMock.when(() -> Files.createDirectories(any(Path.class))).thenReturn(null);

            assertDoesNotThrow(() -> manager.uploadProfilePhoto("rid", stream));
            imageIOMock.verify(() -> ImageIO.read(any(InputStream.class)));
            imageIOMock.verify(() -> ImageIO.write(any(BufferedImage.class), eq("png"), any(File.class)));
            filesMock.verify(() -> Files.createDirectories(any(Path.class)));
        }
    }
}