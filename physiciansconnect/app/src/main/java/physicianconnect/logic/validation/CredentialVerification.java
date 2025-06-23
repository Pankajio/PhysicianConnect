package physicianconnect.logic.validation;

import physicianconnect.presentation.config.UIConfig;
import physicianconnect.logic.manager.PhysicianManager;
import physicianconnect.logic.manager.ReceptionistManager;

import javax.swing.*;

public class CredentialVerification {
    private final PhysicianManager physicianManager;
    private final ReceptionistManager receptionistManager;
    private final JDialog dialog;

    public CredentialVerification(PhysicianManager physicianManager, ReceptionistManager receptionistManager, JDialog dialog) {
        this.physicianManager = physicianManager;
        this.receptionistManager = receptionistManager;
        this.dialog = dialog;
    }

    public boolean verifySignUpData(String name, String email, String password, String confirmPassword) {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, UIConfig.ERROR_REQUIRED_FIELD,
                    UIConfig.ERROR_DIALOG_TITLE,
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(dialog, UIConfig.ERROR_INVALID_EMAIL,
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(dialog,
                    UIConfig.ERROR_PASSWORD_LENGTH, UIConfig.ERROR_DIALOG_TITLE,
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(dialog, UIConfig.ERROR_PASSWORD_MISMATCH,
                    UIConfig.ERROR_DIALOG_TITLE,
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (physicianManager.getPhysicianByEmail(email) != null ||
                receptionistManager.getReceptionistByEmail(email) != null) {
            JOptionPane.showMessageDialog(dialog,
                    UIConfig.ERROR_EMAIL_EXISTS, UIConfig.ERROR_DIALOG_TITLE,
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }
}