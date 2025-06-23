package physicianconnect.logic.validation;

import physicianconnect.logic.exceptions.InvalidCredentialException;

public final class PhysicianValidator {

    private PhysicianValidator() { }

    public static void validateEmail(String email) throws InvalidCredentialException {
        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            throw new InvalidCredentialException("Please enter a valid email address.");
        }
    }

    public static void validatePassword(String password) throws InvalidCredentialException {
        if (password == null || password.length() < 6) {
            throw new InvalidCredentialException("Password must be at least 6 characters.");
        }
    }
}
