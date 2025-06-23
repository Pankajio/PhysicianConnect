package physicianconnect.logic.controller;

import org.junit.jupiter.api.*;
import org.mockito.*;
import physicianconnect.logic.exceptions.InvalidCredentialException;
import physicianconnect.logic.manager.ReceptionistManager;
import physicianconnect.logic.validation.ReceptionistValidator;
import physicianconnect.objects.Receptionist;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReceptionistControllerTest {

    @Mock
    private ReceptionistManager receptionistManager;

    private ReceptionistController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new ReceptionistController(receptionistManager);
    }

    @Test
    void testLoginSuccess() throws Exception {
        Receptionist r = new Receptionist("id", "Alice", "alice@email.com", "pw");
        when(receptionistManager.login("alice@email.com", "pw")).thenReturn(r);

        try (MockedStatic<ReceptionistValidator> validatorMock = mockStatic(ReceptionistValidator.class)) {
            Receptionist result = controller.login("alice@email.com", "pw");
            assertEquals(r, result);
            validatorMock.verify(() -> ReceptionistValidator.validateEmail("alice@email.com"));
            validatorMock.verify(() -> ReceptionistValidator.validatePassword("pw"));
        }
    }

    @Test
    void testLoginInvalidThrows() {
        when(receptionistManager.login("bad@email.com", "pw")).thenReturn(null);
        try (MockedStatic<ReceptionistValidator> validatorMock = mockStatic(ReceptionistValidator.class)) {
            assertThrows(InvalidCredentialException.class, () ->
                    controller.login("bad@email.com", "pw"));
            validatorMock.verify(() -> ReceptionistValidator.validateEmail("bad@email.com"));
            validatorMock.verify(() -> ReceptionistValidator.validatePassword("pw"));
        }
    }

    @Test
    void testRegisterSuccess() throws Exception {
        when(receptionistManager.getReceptionistByEmail("new@email.com")).thenReturn(null);
        try (MockedStatic<ReceptionistValidator> validatorMock = mockStatic(ReceptionistValidator.class)) {
            Receptionist result = controller.register("Alice", "new@email.com", "pw", "pw");
            assertEquals("Alice", result.getName());
            verify(receptionistManager).addReceptionist(any());
            validatorMock.verify(() -> ReceptionistValidator.validateRegistration("Alice", "new@email.com", "pw", "pw"));
        }
    }

    @Test
    void testRegisterDuplicateThrows() {
        when(receptionistManager.getReceptionistByEmail("dup@email.com"))
                .thenReturn(new Receptionist("id", "Dup", "dup@email.com", "pw"));
        try (MockedStatic<ReceptionistValidator> validatorMock = mockStatic(ReceptionistValidator.class)) {
            assertThrows(InvalidCredentialException.class, () ->
                    controller.register("Dup", "dup@email.com", "pw", "pw"));
            validatorMock.verify(() -> ReceptionistValidator.validateRegistration("Dup", "dup@email.com", "pw", "pw"));
        }
    }

@Test
void testRegisterPasswordMismatchThrows() {
    when(receptionistManager.getReceptionistByEmail("x@email.com")).thenReturn(null);
    try (MockedStatic<ReceptionistValidator> validatorMock = mockStatic(ReceptionistValidator.class)) {
        validatorMock
            .when(() -> ReceptionistValidator.validateRegistration("X", "x@email.com", "pw1", "pw2"))
            .thenThrow(new InvalidCredentialException("Passwords do not match."));
        assertThrows(InvalidCredentialException.class, () ->
            controller.register("X", "x@email.com", "pw1", "pw2"));
        validatorMock.verify(() -> ReceptionistValidator.validateRegistration("X", "x@email.com", "pw1", "pw2"));
    }
}
}