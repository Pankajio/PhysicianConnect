package physicianconnect.logic.manager;

import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import physicianconnect.objects.Physician;
import physicianconnect.persistence.interfaces.PhysicianPersistence;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


import java.nio.file.Files;
import java.nio.file.Path;

class PhysicianManagerTest {

    private PhysicianManager manager;
    private PhysicianPersistence physicianDB;

    @BeforeEach
    void setup() {
        physicianDB = mock(PhysicianPersistence.class);
        manager = new PhysicianManager(physicianDB);
    }

    @Test
    void testAddPhysicianNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> manager.addPhysician(null));
    }

    @Test
    void testAddPhysicianBlankIdThrows() {
        Physician p = mock(Physician.class);
        when(p.getId()).thenReturn("   ");
        assertThrows(IllegalArgumentException.class, () -> manager.addPhysician(p));
    }

    @Test
    void testAddPhysicianNullIdThrows() {
        Physician p = mock(Physician.class);
        when(p.getId()).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> manager.addPhysician(p));
    }

    @Test
    void testAddPhysicianDelegates() {
        Physician p = new Physician("id", "n", "e", "pw");
        when(physicianDB.getPhysicianById("id")).thenReturn(null);
        manager.addPhysician(p);
        verify(physicianDB).addPhysician(p);
    }

    @Test
    void testRemovePhysicianDelegates() {
        manager.removePhysician("abc");
        verify(physicianDB).deletePhysicianById("abc");
    }

    @Test
    void testGetAllPhysiciansDelegates() {
        List<Physician> list = Collections.singletonList(new Physician("id", "n", "e", "p"));
        when(physicianDB.getAllPhysicians()).thenReturn(list);
        List<Physician> result = manager.getAllPhysicians();
        assertEquals(1, result.size());
        assertEquals("id", result.get(0).getId());
    }

    @Test
    void testUpdatePhysicianNullThrows() throws Exception {
        var m = PhysicianManager.class.getDeclaredMethod("updatePhysician", Physician.class);
        m.setAccessible(true);
        assertThrows(IllegalArgumentException.class, () -> {
            try {
                m.invoke(manager, new Object[]{null});
            } catch (Exception e) {
                throw e.getCause();
            }
        });
    }

    @Test
    void testUpdatePhysicianNullIdThrows() throws Exception {
        Physician p = mock(Physician.class);
        when(p.getId()).thenReturn(null);
        var m = PhysicianManager.class.getDeclaredMethod("updatePhysician", Physician.class);
        m.setAccessible(true);
        assertThrows(IllegalArgumentException.class, () -> {
            try {
                m.invoke(manager, p);
            } catch (Exception e) {
                throw e.getCause();
            }
        });
    }

    @Test
    void testValidateBasicInfoNullNameThrows() {
        Physician p = new Physician("id", null, "e", "pw");
        Exception ex = assertThrows(IllegalArgumentException.class, () -> manager.validateBasicInfo(p));
        assertEquals("Name cannot be empty.", ex.getMessage());
    }

    @Test
    void testValidateBasicInfoBlankNameThrows() {
        Physician p = new Physician("id", "   ", "e", "pw");
        Exception ex = assertThrows(IllegalArgumentException.class, () -> manager.validateBasicInfo(p));
        assertEquals("Name cannot be empty.", ex.getMessage());
    }

    @Test
    void testValidateBasicInfoInvalidPhoneThrows() {
        Physician p = new Physician("id", "Dr. X", "e", "pw");
        p.setPhone("badphone");
        Exception ex = assertThrows(IllegalArgumentException.class, () -> manager.validateBasicInfo(p));
        assertEquals("Phone must match (204) 123-4567 format.", ex.getMessage());
    }

    @Test
    void testValidateBasicInfoValid() {
        Physician p = new Physician("id", "Dr. X", "e", "pw");
        p.setPhone("(204) 123-4567");
        assertDoesNotThrow(() -> manager.validateBasicInfo(p));
    }

    @Test
    void testUploadProfilePhotoInvalidImageThrows() {
        InputStream badStream = new ByteArrayInputStream(new byte[]{1, 2, 3});
        assertThrows(RuntimeException.class, () -> manager.uploadProfilePhoto("id", badStream));
    }

    @Test
    void testUploadProfilePhotoIOExceptionThrows() {
        try (var imageIOMock = mockStatic(ImageIO.class)) {
            imageIOMock.when(() -> ImageIO.read(any(InputStream.class))).thenThrow(new IOException("fail"));
            InputStream stream = new ByteArrayInputStream(new byte[]{1, 2, 3});
            assertThrows(RuntimeException.class, () -> manager.uploadProfilePhoto("id", stream));
        }
    }

    @Test
void testUploadProfilePhotoHappyPath() throws Exception {
    // Arrange: create a valid BufferedImage to be returned by ImageIO.read
    BufferedImage img = new BufferedImage(400, 300, BufferedImage.TYPE_INT_ARGB);
    InputStream stream = new ByteArrayInputStream(new byte[]{1, 2, 3});

    try (
        MockedStatic<ImageIO> imageIOMock = mockStatic(ImageIO.class);
        MockedStatic<Files> filesMock = mockStatic(Files.class)
    ) {
        imageIOMock.when(() -> ImageIO.read(any(InputStream.class))).thenReturn(img);
        imageIOMock.when(() -> ImageIO.write(any(BufferedImage.class), eq("png"), any(File.class))).thenReturn(true);
        filesMock.when(() -> Files.createDirectories(any(Path.class))).thenReturn(null);

        // Act & Assert: should not throw and should call ImageIO.write
        assertDoesNotThrow(() -> manager.uploadProfilePhoto("docid", stream));
        imageIOMock.verify(() -> ImageIO.read(any(InputStream.class)));
        imageIOMock.verify(() -> ImageIO.write(any(BufferedImage.class), eq("png"), any(File.class)));
        filesMock.verify(() -> Files.createDirectories(any(Path.class)));
    }
}
}