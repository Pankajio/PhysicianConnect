package physicianconnect;

import org.junit.jupiter.api.*;
import org.mockito.*;
import physicianconnect.logic.manager.AppointmentManager;
import physicianconnect.logic.manager.PhysicianManager;
import physicianconnect.logic.manager.ReceptionistManager;
import physicianconnect.logic.controller.AppointmentController;
import physicianconnect.presentation.LoginScreen;
import physicianconnect.presentation.physician.PhysicianApp;
import physicianconnect.presentation.receptionist.ReceptionistApp;
import physicianconnect.objects.Physician;
import physicianconnect.objects.Receptionist;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppControllerTest {

    @Mock PhysicianManager physicianManager;
    @Mock AppointmentManager appointmentManager;
    @Mock ReceptionistManager receptionistManager;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testShowLoginScreenLaunchesLoginScreen() {
        try (MockedConstruction<LoginScreen> loginMock = mockConstruction(LoginScreen.class)) {
            AppController controller = new AppController(physicianManager, appointmentManager, receptionistManager);

            // Mock SwingUtilities.invokeLater to run immediately
            try (MockedStatic<SwingUtilities> swingMock = mockStatic(SwingUtilities.class)) {
                swingMock.when(() -> SwingUtilities.invokeLater(any(Runnable.class)))
                        .thenAnswer(invocation -> {
                            ((Runnable) invocation.getArgument(0)).run();
                            return null;
                        });

                controller.showLoginScreen();
                assertEquals(1, loginMock.constructed().size());
                LoginScreen loginScreen = loginMock.constructed().get(0);
                assertNotNull(loginScreen);
            }
        }
    }

    @Test
    void testShowPhysicianAppLaunchesPhysicianApp() {
        Physician user = mock(Physician.class);
        try (MockedStatic<PhysicianApp> appMock = mockStatic(PhysicianApp.class)) {
            AppController controller = new AppController(physicianManager, appointmentManager, receptionistManager);

            try (MockedStatic<SwingUtilities> swingMock = mockStatic(SwingUtilities.class)) {
                swingMock.when(() -> SwingUtilities.invokeLater(any(Runnable.class)))
                        .thenAnswer(invocation -> {
                            ((Runnable) invocation.getArgument(0)).run();
                            return null;
                        });

                controller.showPhysicianApp(user);
                appMock.verify(() -> PhysicianApp.launchSingleUser(
                        eq(user),
                        eq(physicianManager),
                        eq(appointmentManager),
                        eq(receptionistManager),
                        any(AppointmentController.class),
                        any()
                ));
            }
        }
    }

    @Test
    void testShowReceptionistAppLaunchesReceptionistApp() {
        Receptionist user = mock(Receptionist.class);
        try (MockedConstruction<ReceptionistApp> recMock = mockConstruction(ReceptionistApp.class)) {
            AppController controller = new AppController(physicianManager, appointmentManager, receptionistManager);

            try (MockedStatic<SwingUtilities> swingMock = mockStatic(SwingUtilities.class)) {
                swingMock.when(() -> SwingUtilities.invokeLater(any(Runnable.class)))
                        .thenAnswer(invocation -> {
                            ((Runnable) invocation.getArgument(0)).run();
                            return null;
                        });

                controller.showReceptionistApp(user);
                assertEquals(1, recMock.constructed().size());
                ReceptionistApp recApp = recMock.constructed().get(0);
                assertNotNull(recApp);
            }
        }
    }
}