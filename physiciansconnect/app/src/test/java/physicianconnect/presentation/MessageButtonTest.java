package physicianconnect.presentation;

import org.junit.jupiter.api.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import physicianconnect.presentation.util.TestUtils;

import static org.junit.jupiter.api.Assertions.*;

class MessageButtonTest {

    @Test
    void testInitialState() {
        MessageButton btn = new MessageButton();
        JButton button = (JButton) TestUtils.getField(btn, "messageButton");
        JLabel label = (JLabel) TestUtils.getField(btn, "notificationLabel");

        assertEquals("Messages", button.getText());
        assertFalse(label.isVisible());
    }

    @Test
    void testUpdateNotificationCount() {
        MessageButton btn = new MessageButton();
        JLabel label = (JLabel) TestUtils.getField(btn, "notificationLabel");

        btn.updateNotificationCount(0);
        assertFalse(label.isVisible());

        btn.updateNotificationCount(3);
        assertTrue(label.isVisible());
        assertEquals("3", label.getText());

        btn.updateNotificationCount(0);
        assertFalse(label.isVisible());
    }

    @Test
    void testSetOnAction() {
        MessageButton btn = new MessageButton();
        JButton button = (JButton) TestUtils.getField(btn, "messageButton");

        final boolean[] called = {false};
        btn.setOnAction(e -> called[0] = true);

        for (ActionListener l : button.getActionListeners()) {
            l.actionPerformed(new ActionEvent(button, ActionEvent.ACTION_PERFORMED, "Messages"));
        }
        assertTrue(called[0]);
    }


}