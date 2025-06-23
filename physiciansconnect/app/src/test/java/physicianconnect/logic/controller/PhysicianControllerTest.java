package physicianconnect.logic.controller;

import org.junit.jupiter.api.*;
import org.mockito.*;
import physicianconnect.logic.exceptions.InvalidCredentialException;
import physicianconnect.logic.manager.PhysicianManager;
import physicianconnect.logic.validation.PhysicianValidator;
import physicianconnect.objects.Physician;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PhysicianControllerTest {

    @Mock
    private PhysicianManager physicianManager;

    private PhysicianController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new PhysicianController(physicianManager);
    }

    @Test
    void loginCallsValidatorAndManager() throws Exception {
        try (MockedStatic<PhysicianValidator> validatorMock = mockStatic(PhysicianValidator.class)) {
            Physician p = new Physician("id", "Dr. Alice", "alice@email.com", "pw");
            when(physicianManager.login("alice@email.com", "pw")).thenReturn(p);

            Physician result = controller.login("alice@email.com", "pw");

            assertEquals(p, result);
            validatorMock.verify(() -> PhysicianValidator.validateEmail("alice@email.com"));
            validatorMock.verify(() -> PhysicianValidator.validatePassword("pw"));
        }
    }

    @Test
    void loginThrowsIfManagerReturnsNull() {
        try (MockedStatic<PhysicianValidator> validatorMock = mockStatic(PhysicianValidator.class)) {
            when(physicianManager.login("bad@email.com", "pw")).thenReturn(null);
            assertThrows(InvalidCredentialException.class, () ->
                    controller.login("bad@email.com", "pw"));
        }
    }

    @Test
    void registerSuccess() throws Exception {
        try (MockedStatic<PhysicianValidator> validatorMock = mockStatic(PhysicianValidator.class)) {
            when(physicianManager.getPhysicianByEmail("new@email.com")).thenReturn(null);
            Physician result = controller.register("Dr. Bob", "new@email.com", "pw", "pw");
            assertEquals("Dr. Bob", result.getName());
            verify(physicianManager).addPhysician(any());
            validatorMock.verify(() -> PhysicianValidator.validateEmail("new@email.com"));
            validatorMock.verify(() -> PhysicianValidator.validatePassword("pw"));
        }
    }

    @Test
    void registerThrowsIfNameBlank() {
        assertThrows(InvalidCredentialException.class, () ->
                controller.register("   ", "e@email.com", "pw", "pw"));
        assertThrows(InvalidCredentialException.class, () ->
                controller.register(null, "e@email.com", "pw", "pw"));
    }

    @Test
    void registerThrowsIfPasswordMismatch() {
        // No need to mock getPhysicianByEmail, password mismatch is checked first
        assertThrows(InvalidCredentialException.class, () ->
                controller.register("X", "x@email.com", "pw1", "pw2"));
    }

    @Test
    void registerThrowsIfDuplicateEmail() {
        when(physicianManager.getPhysicianByEmail("dup@email.com"))
                .thenReturn(new Physician("id", "Dup", "dup@email.com", "pw"));
        assertThrows(InvalidCredentialException.class, () ->
                controller.register("Dup", "dup@email.com", "pw", "pw"));
    }
}