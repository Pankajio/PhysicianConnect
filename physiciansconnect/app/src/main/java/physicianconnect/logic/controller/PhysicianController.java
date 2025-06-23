package physicianconnect.logic.controller;

import physicianconnect.logic.exceptions.InvalidCredentialException;
import physicianconnect.logic.manager.PhysicianManager;
import physicianconnect.logic.validation.PhysicianValidator;
import physicianconnect.objects.Physician;

/**
 * Controller for physician‐related use cases: login and registration.
 * Delegates validation to PhysicianValidator and persistence to PhysicianManager.
 */
public class PhysicianController {

    private final PhysicianManager physicianManager;

    public PhysicianController(PhysicianManager physicianManager) {
        this.physicianManager = physicianManager;
    }

    /**
     * Attempt to log in with the given email and password.
     *
     * @param email    the physician’s email
     * @param password the physician’s password
     * @return the logged‐in Physician object
     * @throws InvalidCredentialException if email/password are invalid or credentials don’t match
     */
    public Physician login(String email, String password) throws InvalidCredentialException {
        // 1) Validate raw inputs
        PhysicianValidator.validateEmail(email);
        PhysicianValidator.validatePassword(password);

        // 2) Delegate to manager
        Physician existing = physicianManager.login(email, password);
        if (existing == null) {
            throw new InvalidCredentialException("Invalid email or password.");
        }
        return existing;
    }

    /**
     * Register a brand‐new physician account.
     *
     * @param name            the physician’s full name
     * @param email           the physician’s email
     * @param password        the physician’s chosen password
     * @param confirmPassword must match password exactly
     * @return the newly created Physician object
     * @throws InvalidCredentialException if validation fails or email already exists
     */
    public Physician register(
            String name,
            String email,
            String password,
            String confirmPassword
    ) throws InvalidCredentialException {
        // 1) Basic non‐empty checks
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidCredentialException("Name cannot be blank.");
        }

        // 2) Email & password format checks
        PhysicianValidator.validateEmail(email);
        PhysicianValidator.validatePassword(password);

        // 3) Confirm password match
        if (!password.equals(confirmPassword)) {
            throw new InvalidCredentialException("Passwords do not match.");
        }

        // 4) Ensure no existing account uses this email
        if (physicianManager.getPhysicianByEmail(email) != null) {
            throw new InvalidCredentialException("An account with that email already exists.");
        }

        // 5) Create a new Physician object (ID generated here)
        String id = java.util.UUID.randomUUID().toString();
        Physician newPhysician = new Physician(id, name, email, password);

        // 6) Persist via manager
        physicianManager.addPhysician(newPhysician);

        return newPhysician;
    }
}