package physicianconnect.presentation;

import org.junit.jupiter.api.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

import static org.junit.jupiter.api.Assertions.*;

class NotificationBannerTest {

    JFrame owner;

    @BeforeEach
    void setup() {
        owner = new JFrame();
    }

    @AfterEach
    void tearDown() {
        owner.dispose();
    }

@Test
void testShowAndDismiss() throws Exception {
    NotificationBanner banner = new NotificationBanner(owner);

    final boolean[] clicked = {false};
    ActionListener listener = e -> clicked[0] = true;

    SwingUtilities.invokeAndWait(() -> banner.show("Test message", listener));
    assertTrue(banner.isVisible());

    // Simulate click
    SwingUtilities.invokeAndWait(() -> {
        for (var l : banner.getMouseListeners()) {
            l.mouseClicked(null);
        }
    });

    // Wait for fade-out to complete (max 500ms)
    int waited = 0;
    while (banner.isVisible() && waited < 600) {
        Thread.sleep(50);
        waited += 50;
    }

    assertFalse(banner.isVisible());
    assertTrue(clicked[0]);
}

    @Test
    void testDismissHidesBanner() throws Exception {
        NotificationBanner banner = new NotificationBanner(owner);
        SwingUtilities.invokeAndWait(() -> banner.show("Dismiss me", null));
        assertTrue(banner.isVisible());
        SwingUtilities.invokeAndWait(banner::dismiss);
        assertFalse(banner.isVisible());
    }
}