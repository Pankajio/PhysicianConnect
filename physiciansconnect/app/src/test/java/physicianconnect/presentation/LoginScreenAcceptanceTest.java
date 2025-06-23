package physicianconnect.presentation;

import static org.assertj.swing.fixture.Containers.showInFrame;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.JTextComponent;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.*;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import physicianconnect.AppController;
import physicianconnect.objects.Physician;
import physicianconnect.objects.Receptionist;
import physicianconnect.logic.controller.PhysicianController;
import physicianconnect.logic.controller.ReceptionistController;
import physicianconnect.logic.exceptions.InvalidCredentialException;
import physicianconnect.logic.manager.AppointmentManager;
import physicianconnect.logic.manager.PhysicianManager;
import physicianconnect.logic.manager.ReceptionistManager;

@RunWith(GUITestRunner.class)
public class LoginScreenAcceptanceTest extends AssertJSwingJUnitTestCase {

    private FrameFixture window;
    private LoginScreen loginScreen;
    private PhysicianManager physicianManager;
    private ReceptionistManager receptionistManager;
    private AppointmentManager appointmentManager;
    private AppController appController;

    @Override
    protected void onSetUp() {
        physicianManager = mock(PhysicianManager.class);
        receptionistManager = mock(ReceptionistManager.class);
        appointmentManager = mock(AppointmentManager.class);
        appController = mock(AppController.class);

        loginScreen = GuiActionRunner.execute(() ->
                new LoginScreen(physicianManager, appointmentManager, receptionistManager, appController));

        window = showInFrame(loginScreen);
        window.resizeTo(new Dimension(1000, 600));
    }

    @Test
    public void shouldDisplayAllUIComponents() {
        window.label("WelcomeLabel").requireVisible();
        window.label("AppNameLabel").requireVisible();
        window.textBox("emailField").requireVisible();
        window.textBox("passField").requireVisible();
        window.button("loginBtn").requireVisible();
        window.button("createBtn").requireVisible();

        window.label("PhysicianLoginHeader").requireVisible();
        window.label("PhysicianLoginInfo").requireVisible();
        window.label("ReceptionistLoginHeader").requireVisible();
        window.label("ReceptionistLoginInfo").requireVisible();
    }

    @Test
    public void shouldShowErrorWhenFieldsAreEmpty() {
        window.button("loginBtn").click();
        window.label("errorLabel").requireText("Please enter both email and password");
    }

    @Test
    public void shouldNavigateFieldsWithEnterKey() {
        window.textBox("emailField").enterText("test@example.com");
        window.textBox("emailField").pressAndReleaseKeys(KeyEvent.VK_ENTER);
        assertThat(window.textBox("passField").target().hasFocus()).isTrue();

        window.textBox("passField").enterText("password");
        window.textBox("passField").pressAndReleaseKeys(KeyEvent.VK_ENTER);
        verify(appController, atLeastOnce()).showPhysicianApp(any());
    }

    @Test
    public void shouldShowFocusBorderOnFields() {
        JTextComponent emailField = window.textBox("emailField").target();
        Color initialBorderColor = getBorderColor(emailField);

        window.textBox("emailField").focus();
        Color focusedBorderColor = getBorderColor(emailField);
        assertThat(focusedBorderColor).isNotEqualTo(initialBorderColor);

        window.textBox("passField").focus();
        Color unfocusedBorderColor = getBorderColor(emailField);
        assertThat(unfocusedBorderColor).isEqualTo(initialBorderColor);
    }

    @Test
    public void shouldSuccessfullyLoginAsPhysician() throws InvalidCredentialException {
        Physician testPhysician = new Physician("doc123", "Dr. Test", "physician@test.com", "password123");
        when(physicianManager.login("physician@test.com", "password123")).thenReturn(testPhysician);

        window.textBox("emailField").enterText("physician@test.com");
        window.textBox("passField").enterText("password123");
        window.button("loginBtn").click();

        verify(appController).showPhysicianApp(testPhysician);
        assertThat(loginScreen.isVisible()).isFalse();
    }

    @Test
    public void shouldSuccessfullyLoginAsReceptionist() throws InvalidCredentialException {
        Receptionist testReceptionist = new Receptionist("rec456", "Receptionist Test", "receptionist@test.com", "password123");
        when(receptionistManager.login("receptionist@test.com", "password123")).thenReturn(testReceptionist);

        window.textBox("emailField").enterText("receptionist@test.com");
        window.textBox("passField").enterText("password123");
        window.button("loginBtn").click();

        verify(appController).showReceptionistApp(testReceptionist);
        assertThat(loginScreen.isVisible()).isFalse();
    }

    @Test
    public void shouldShowErrorForInvalidCredentials() throws InvalidCredentialException {
        when(physicianManager.login("wrong@email.com", "wrongpass")).thenThrow(
                new InvalidCredentialException("Invalid credentials"));

        window.textBox("emailField").enterText("wrong@email.com");
        window.textBox("passField").enterText("wrongpass");
        window.button("loginBtn").click();

        window.label("errorLabel").requireText("Invalid credentials");
    }

    @Test
    public void shouldOpenCreateAccountDialog() {
        window.button("createBtn").click();

        DialogFixture dialog = window.dialog(new GenericTypeMatcher<JDialog>(JDialog.class) {
            @Override
            protected boolean isMatching(JDialog dialog) {
                return "Create Account".equals(dialog.getTitle());
            }
        });

        dialog.comboBox().requireVisible();
        dialog.textBox("nameField").requireVisible();
        dialog.textBox("emailField").requireVisible();
        dialog.textBox("passwordField").requireVisible();
        dialog.textBox("confirmPasswordField").requireVisible();
        dialog.button("registerBtn").requireVisible();

        dialog.close();
    }

    @Test
    public void shouldSuccessfullyCreatePhysicianAccount() throws InvalidCredentialException {
        Physician newPhysician = new Physician("doc789", "New Doctor", "new@doctor.com", "newpass123");
        doNothing().when(physicianManager).addPhysician(any(Physician.class));


        window.button("createBtn").click();
        DialogFixture dialog = window.dialog(new GenericTypeMatcher<JDialog>(JDialog.class) {
            @Override
            protected boolean isMatching(JDialog dialog) {
                return "Create Account".equals(dialog.getTitle());
            }
        });

        dialog.comboBox().selectItem("Physician");
        dialog.textBox("nameField").enterText("New Doctor");
        dialog.textBox("emailField").enterText("new@doctor.com");
        dialog.textBox("passwordField").enterText("newpass123");
        dialog.textBox("confirmPasswordField").enterText("newpass123");
        dialog.button("registerBtn").click();

        verify(appController).showPhysicianApp(newPhysician);
        assertThat(loginScreen.isVisible()).isFalse();
    }

    private Color getBorderColor(JComponent component) {
        Border border = component.getBorder();
        if (border instanceof CompoundBorder) {
            border = ((CompoundBorder) border).getOutsideBorder();
        }
        if (border instanceof LineBorder) {
            return ((LineBorder) border).getLineColor();
        }
        return null;
    }
}