package physicianconnect;

import org.junit.jupiter.api.*;
import org.mockito.*;
import physicianconnect.config.AppConfig;
import physicianconnect.logic.manager.AppointmentManager;
import physicianconnect.logic.manager.PhysicianManager;
import physicianconnect.logic.manager.ReceptionistManager;
import physicianconnect.persistence.PersistenceFactory;
import physicianconnect.persistence.interfaces.PhysicianPersistence;
import physicianconnect.persistence.interfaces.AppointmentPersistence;
import physicianconnect.persistence.interfaces.ReceptionistPersistence;

import static org.mockito.Mockito.*;

class AppTest {

    @Test
    void testMainInitializesAndShowsLoginScreen() {
        try (MockedStatic<PersistenceFactory> pfMock = mockStatic(PersistenceFactory.class);
             MockedConstruction<PhysicianManager> pmMock = mockConstruction(PhysicianManager.class);
             MockedConstruction<AppointmentManager> amMock = mockConstruction(AppointmentManager.class);
             MockedConstruction<ReceptionistManager> rmMock = mockConstruction(ReceptionistManager.class);
             MockedConstruction<AppController> acMock = mockConstruction(AppController.class)) {

            pfMock.when(() -> PersistenceFactory.getPhysicianPersistence()).thenReturn(mock(PhysicianPersistence.class));
            pfMock.when(() -> PersistenceFactory.getAppointmentPersistence()).thenReturn(mock(AppointmentPersistence.class));
            pfMock.when(() -> PersistenceFactory.getReceptionistPersistence()).thenReturn(mock(ReceptionistPersistence.class));

            App.main(new String[0]);

            // The AppController is constructed once, and showLoginScreen is called twice
            AppController controller = acMock.constructed().get(0);
            verify(controller, times(2)).showLoginScreen();
        }
    }
}