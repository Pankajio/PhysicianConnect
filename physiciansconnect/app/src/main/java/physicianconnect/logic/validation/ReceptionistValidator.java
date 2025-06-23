package physicianconnect.logic.validation;

import physicianconnect.logic.exceptions.InvalidCredentialException;

public class ReceptionistValidator {
    public static void validateEmail(String email) throws InvalidCredentialException {
        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new InvalidCredentialException("Invalid email address.");
        }
    }

    public static void validatePassword(String password) throws InvalidCredentialException {
        if (password == null || password.length() < 6) {
            throw new InvalidCredentialException("Password must be at least 6 characters.");
        }
    }

    public static void validateRegistration(String name, String email, String password, String confirmPassword) throws InvalidCredentialException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidCredentialException("Name cannot be blank.");
        }
        validateEmail(email);
        validatePassword(password);
        if (!password.equals(confirmPassword)) {
            throw new InvalidCredentialException("Passwords do not match.");
        }
    }
}