package physicianconnect.logic.controller;

import physicianconnect.logic.exceptions.InvalidCredentialException;
import physicianconnect.logic.manager.ReceptionistManager;
import physicianconnect.logic.validation.ReceptionistValidator;
import physicianconnect.objects.Receptionist;

public class ReceptionistController {
    private final ReceptionistManager receptionistManager;

    public ReceptionistController(ReceptionistManager receptionistManager) {
        this.receptionistManager = receptionistManager;
    }

    public Receptionist login(String email, String password) throws InvalidCredentialException {
        ReceptionistValidator.validateEmail(email);
        ReceptionistValidator.validatePassword(password);
        Receptionist receptionist = receptionistManager.login(email, password);
        if (receptionist == null) {
            throw new InvalidCredentialException("Invalid email or password.");
        }
        return receptionist;
    }

    public Receptionist register(String name, String email, String password, String confirmPassword) throws InvalidCredentialException {
        ReceptionistValidator.validateRegistration(name, email, password, confirmPassword);
        if (receptionistManager.getReceptionistByEmail(email) != null) {
            throw new InvalidCredentialException("An account with that email already exists.");
        }
        String id = java.util.UUID.randomUUID().toString();
        Receptionist newReceptionist = new Receptionist(id, name, email, password);
        receptionistManager.addReceptionist(newReceptionist);
        return newReceptionist;
    }
}