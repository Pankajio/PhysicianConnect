package physicianconnect.presentation;

import org.junit.jupiter.api.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import physicianconnect.presentation.util.TestUtils;

import static org.junit.jupiter.api.Assertions.*;

class NotificationButtonTest {

    @Test
    void testInitialState() {
        NotificationButton btn = new NotificationButton();
        JButton button = (JButton) TestUtils.getField(btn, "notificationButton");
        JLabel label = (JLabel) TestUtils.getField(btn, "notificationLabel");

        assertEquals("Alerts", button.getText());
        assertFalse(label.isVisible());
    }

    @Test
    void testUpdateNotificationCount() {
        NotificationButton btn = new NotificationButton();
        JLabel label = (JLabel) TestUtils.getField(btn, "notificationLabel");

        btn.updateNotificationCount(0);
        assertFalse(label.isVisible());

        btn.updateNotificationCount(5);
        assertTrue(label.isVisible());
        assertEquals("5", label.getText());

        btn.updateNotificationCount(0);
        assertFalse(label.isVisible());
    }

    @Test
    void testSetOnAction() {
        NotificationButton btn = new NotificationButton();
        JButton button = (JButton) TestUtils.getField(btn, "notificationButton");

        final boolean[] called = {false};
        btn.setOnAction(e -> called[0] = true);

        for (ActionListener l : button.getActionListeners()) {
            l.actionPerformed(new ActionEvent(button, ActionEvent.ACTION_PERFORMED, "Alerts"));
        }
        assertTrue(called[0]);
    }


}