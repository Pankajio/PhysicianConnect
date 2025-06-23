package physicianconnect;

import physicianconnect.config.AppConfig;
import physicianconnect.logic.manager.AppointmentManager;
import physicianconnect.logic.manager.PhysicianManager;
import physicianconnect.logic.manager.ReceptionistManager;
import physicianconnect.persistence.PersistenceFactory;

public class App {
    public static void main(String[] args) {
        PersistenceFactory.initialize(AppConfig.getPersistenceType(), AppConfig.shouldSeedData());

        PhysicianManager physicianManager = new PhysicianManager(PersistenceFactory.getPhysicianPersistence());
        AppointmentManager appointmentManager = new AppointmentManager(PersistenceFactory.getAppointmentPersistence());
        ReceptionistManager receptionistManager = new ReceptionistManager(PersistenceFactory.getReceptionistPersistence());

        AppController controller = new AppController(physicianManager, appointmentManager, receptionistManager);
        controller.showLoginScreen();
        controller.showLoginScreen(); //second one to test messaging/ receptionist - physician appointment updates
    }
}